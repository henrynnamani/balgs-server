package com.graey.Balgs.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.graey.Balgs.common.constant.PaymentConstant;
import com.graey.Balgs.common.enums.BroadcastPaymentStatus;
import com.graey.Balgs.common.enums.PaymentProvider;
import com.graey.Balgs.common.enums.PaymentType;
import com.graey.Balgs.common.interfaces.PaymentGateway;
import com.graey.Balgs.common.messages.OrderMessages;
import com.graey.Balgs.common.messages.PaymentMessages;
import com.graey.Balgs.common.utils.ApiResponse;
import com.graey.Balgs.dto.payment.PaymentDto;
import com.graey.Balgs.model.Order;
import com.graey.Balgs.model.TradeIn;
import com.graey.Balgs.model.User;
import com.graey.Balgs.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("payments")
@Tag(name = "payment", description = "manage payment")
public class PaymentController {

    @Autowired
    private PaystackService paystackService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private CartCleanUpService cartCleanupService;

    @Autowired
    private TradeInService tradeInService;

    @Autowired
    private VendorNotificationService notificationService;

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
    public ResponseEntity<ApiResponse<Object>> initiatePayment(
            @RequestBody PaymentDto paymentDto,
            @AuthenticationPrincipal User user) {

        PaymentGateway gateway = getGateway(paymentDto.provider());

        if (paymentDto.type() == PaymentType.TRADE_IN) {
            if (paymentDto.tradeInId() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("Trade-in ID is required"));
            }

            TradeIn tradeIn = tradeInService.getTradeInById(UUID.fromString(paymentDto.tradeInId()));

            // Ownership check — user can only pay for their own trade-in
            if (!tradeIn.getUser().getId().equals(user.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error("You do not own this trade-in"));
            }

            // Prevent double payment
            if (tradeIn.getBroadcastPaymentStatus() == BroadcastPaymentStatus.PAID) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error(PaymentMessages.PAYMENT_ALREADY_COMPLETED));
            }

            Object response = gateway.initiate(
                    new String[]{paymentDto.tradeInId()},
                    user.getEmail(),
                    PaymentConstant.BROADCAST_FEE,
                    Map.of(
                            "type",      "TRADE_IN",
                            "tradeInId", paymentDto.tradeInId()
                    )

            );

            return ResponseEntity.ok(ApiResponse.success(
                    PaymentMessages.PAYMENT_INITIATED_SUCCESSFULLY, response));
        }

        // ── Regular order payment ──
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (String orderId : paymentDto.orderIds()) {
            if (orderService.isOrderPaymentCompleted(UUID.fromString(orderId))) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error(OrderMessages.ORDER_ALREADY_PLACED));
            }
            totalAmount = totalAmount.add(orderService.getOrderTotalAmount(UUID.fromString(orderId)));
        }

        Object response = gateway.initiate(paymentDto.orderIds(), user.getEmail(), totalAmount, Map.of("type", "ORDER"));

        return ResponseEntity.ok(ApiResponse.success(
                PaymentMessages.PAYMENT_INITIATED_SUCCESSFULLY, response));
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
            JsonNode root = mapper.readTree(payload);
            JsonNode metadata = root.get("data").get("metadata");
            String type = metadata.has("type") ? metadata.get("type").asText() : "ORDER";

            if ("TRADE_IN".equals(type)) {
                handleTradeInWebhook(root, metadata);
                return ResponseEntity.ok("Webhook received");
            }

            handleOrderWebhook(metadata);
            return ResponseEntity.ok("Webhook received");

        } catch (Exception e) {
            log.error("Paystack webhook error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing webhook");
        }
    }

    private void handleTradeInWebhook(JsonNode root, JsonNode metadata) {
        String tradeInId = metadata.get("tradeInId").asText();
        String reference = root.get("data").get("reference").asText();
        tradeInService.markBroadcastPaid(UUID.fromString(tradeInId), UUID.fromString(reference));
    }

    private void handleOrderWebhook(JsonNode metadata) {
        List<String> orderIds = new ArrayList<>();
        metadata.get("orderIds").forEach(node -> orderIds.add(node.asText()));

        List<UUID> purchasedProductIds = new ArrayList<>();

        for (String orderId : orderIds) {
            Order order = orderService.completePayment(UUID.fromString(orderId));
            order.getItem().getProduct().setAvailable(false);
            notificationService.notifyNewOrder(order.getVendor(), order);
            purchasedProductIds.add(order.getItem().getProduct().getId());
        }

        cartCleanupService.removeProductsFromAllCarts(purchasedProductIds, null);
    }
}
