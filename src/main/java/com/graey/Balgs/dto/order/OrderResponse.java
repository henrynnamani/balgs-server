package com.graey.Balgs.dto.order;

import com.graey.Balgs.common.enums.OrderStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class OrderResponse {
    private UUID id;
    private UUID userId;
    private BigDecimal totalPrice;
    private List<OrderItemResponse> items;
    private OrderStatus status;
    private LocalDateTime createdAt;
}
