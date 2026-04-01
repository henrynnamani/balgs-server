package com.graey.Balgs.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.graey.Balgs.common.enums.PaymentProvider;
import com.graey.Balgs.common.interfaces.PaymentGateway;
import com.graey.Balgs.common.messages.OrderMessages;
import com.graey.Balgs.common.messages.PaymentMessages;
import com.graey.Balgs.common.utils.ApiResponse;
import com.graey.Balgs.dto.payment.PaymentDto;
import com.graey.Balgs.model.User;
import com.graey.Balgs.service.OrderService;
import com.graey.Balgs.service.PaystackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("payments")
@Tag(name = "payment", description = "manage payment")
public class PaymentController {

    @Autowired
    private PaystackService paystackService;

    @Autowired
    private OrderService orderService;

    public PaymentGateway getGateway(PaymentProvider provider) {
        switch (provider) {
            case PAYSTACK ->  {
                return paystackService;
            }
        }
        return null;
    }

    @PostMapping("initiate")
    @Operation(summary = "initiate payment")
    public ResponseEntity<ApiResponse<Object>> initiatePayment(@RequestBody PaymentDto paymentDto, @AuthenticationPrincipal User user) {
        PaymentGateway gateway = getGateway(paymentDto.provider());
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (String orderId : paymentDto.orderIds()) {
            if(orderService.isOrderPaymentCompleted(UUID.fromString(orderId))) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(OrderMessages.ORDER_ALREADY_PLACED));
            }

            totalAmount = totalAmount.add(orderService.getOrderTotalAmount(UUID.fromString(orderId)));
        }



        Object response = gateway.initiate(paymentDto.orderIds(), user.getEmail(), totalAmount);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(PaymentMessages.PAYMENT_INITIATED_SUCCESSFULLY,
                response
        ));
    }

    @PostMapping("/webhook/paystack")
    @Operation(summary = "webhook")
    public ResponseEntity<String> handlePaystackWebhook(
            @RequestBody String payload,
            @RequestHeader("x-paystack-signature") String signature) {

        try {
            if (!paystackService.verifyPaystackSignature(payload, signature)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid signature");
            }

            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(payload);
            String event = jsonNode.get("event").asText();

            List<String> orderIds = new ArrayList<>();

            jsonNode.get("data")
                    .get("metadata")
                    .get("orderIds")
                    .forEach(node -> orderIds.add(node.asText()));

            for (String orderId : orderIds) {
                orderService.completePayment(UUID.fromString(orderId));
            }

            return ResponseEntity.ok("Webhook received");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing webhook");
        }
    }
}
