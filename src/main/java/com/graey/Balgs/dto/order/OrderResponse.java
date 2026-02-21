package com.graey.Balgs.dto.order;

import com.graey.Balgs.common.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {
    private UUID id;
    private UUID userId;
    private BigDecimal totalPrice;
    private List<OrderItemResponse> items;
    private OrderStatus status;
    private LocalDateTime createdAt;
}
