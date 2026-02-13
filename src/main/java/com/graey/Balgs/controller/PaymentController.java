package com.graey.Balgs.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.graey.Balgs.common.enums.PaymentProvider;
import com.graey.Balgs.common.interfaces.PaymentGateway;
import com.graey.Balgs.common.messages.OrderMessages;
import com.graey.Balgs.common.messages.PaymentMessages;
import com.graey.Balgs.common.utils.ApiResponse;
import com.graey.Balgs.dto.payment.PaymentDto;
import com.graey.Balgs.service.OpayService;
import com.graey.Balgs.service.OrderService;
import com.graey.Balgs.service.PaystackService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("payments")
public class PaymentController {

    @Autowired
    private PaystackService paystackService;

    @Autowired
    private OpayService opayService;

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
    public ResponseEntity<ApiResponse<Object>> initiatePayment(@RequestBody PaymentDto paymentDto) {
        PaymentGateway gateway = getGateway(paymentDto.provider());

        if(orderService.isOrderPaymentCompleted(UUID.fromString(paymentDto.orderId()))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(OrderMessages.ORDER_ALREADY_PLACED));
        }

        BigDecimal amount = orderService.getOrderTotalAmount(UUID.fromString(paymentDto.orderId()));

        Object response = gateway.initiate(UUID.fromString(paymentDto.orderId()), paymentDto.email(), amount);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(PaymentMessages.PAYMENT_INITIATED_SUCCESSFULLY,
                response
        ));
    }

    @PostMapping("/webhook/paystack")
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

            if ("charge.success".equals(event)) {
                String reference = jsonNode.get("data").get("reference").asText();
                orderService.completePayment(UUID.fromString(reference));
            }

            return ResponseEntity.ok("Webhook received");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing webhook");
        }
    }
}
