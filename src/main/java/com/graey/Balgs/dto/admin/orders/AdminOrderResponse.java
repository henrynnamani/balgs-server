package com.graey.Balgs.dto.admin.orders;

import com.graey.Balgs.dto.order.OrderItemResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminOrderResponse {
    private UUID id;
    private String customer;
    private String phoneNumber;
    private String status;
    private LocalDateTime createdAt;
    private BigDecimal totalPrice;
    private List<AdminOrderItemResponse> items;
}
