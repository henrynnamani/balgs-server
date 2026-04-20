package com.graey.Balgs.service;

import com.graey.Balgs.common.enums.OrderStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class OrderSseService {

    private final Map<String, SseEmitter> orderEmitters = new ConcurrentHashMap<>();
    private final Map<String, SseEmitter> userEmitters = new ConcurrentHashMap<>();

    // Client subscribes to order updates
    public SseEmitter subscribe(String orderId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE); // no timeout

        orderEmitters.put(orderId, emitter);

        // Clean up when connection closes
        emitter.onCompletion(() -> {
            orderEmitters.remove(orderId);
            log.info("SSE connection closed for order: {}", orderId);
        });

        emitter.onTimeout(() -> {
            orderEmitters.remove(orderId);
            log.info("SSE connection timed out for order: {}", orderId);
        });

        emitter.onError(e -> {
            orderEmitters.remove(orderId);
            log.error("SSE error for order {}: {}", orderId, e.getMessage());
        });

        // Send initial connection confirmation
        try {
            emitter.send(SseEmitter.event()
                    .name("connected")
                    .data("Listening for updates on order: " + orderId));
        } catch (IOException e) {
            log.error("Failed to send initial SSE event", e);
        }

        return emitter;
    }

    // Subscribe by userId — gets all their order updates
    public SseEmitter subscribeUser(String userId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

        userEmitters.put(userId, emitter);

        emitter.onCompletion(() -> userEmitters.remove(userId));
        emitter.onTimeout(() -> userEmitters.remove(userId));
        emitter.onError(e -> userEmitters.remove(userId));

        try {
            emitter.send(SseEmitter.event().name("connected").data("Connected"));
        } catch (IOException e) {
            log.error("Failed to send initial SSE event", e);
        }

        return emitter;
    }

    // Push update to order subscribers
    public void pushOrderUpdate(String orderId, String userId, OrderStatus status) {
        Map<String, Object> payload = Map.of(
                "orderId", orderId,
                "status", status.name(),
                "message", getStatusMessage(status),
                "timestamp", LocalDateTime.now().toString()
        );

        // Notify order-specific subscriber
        sendToEmitter(orderEmitters.get(orderId), "order_update", payload);

        // Notify user-specific subscriber
        sendToEmitter(userEmitters.get(userId), "order_update", payload);
    }

    private void sendToEmitter(SseEmitter emitter, String eventName, Object data) {
        if (emitter == null) return;
        try {
            emitter.send(SseEmitter.event()
                    .name(eventName)
                    .data(data, MediaType.APPLICATION_JSON));
        } catch (IOException e) {
            log.error("Failed to push SSE event: {}", e.getMessage());
            emitter.completeWithError(e);
        }
    }

    private String getStatusMessage(OrderStatus status) {
        return switch (status) {
            case PENDING -> "Your order has been received.";
            case PROCESSING -> "";
            case SHIPPED -> "Your order is on its way!";
            case DELIVERY_PENDING -> "";
            case DELIVERED -> "Your order has been delivered. Enjoy! 🎉";
        };
    }
}