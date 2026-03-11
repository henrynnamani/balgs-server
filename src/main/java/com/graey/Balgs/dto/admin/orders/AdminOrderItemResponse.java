package com.graey.Balgs.dto.admin.orders;

import com.graey.Balgs.dto.order.OrderItemAddOnResponse;
import com.graey.Balgs.dto.order.OrderProductSummary;
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
public class AdminOrderItemResponse {
    private UUID id;
    private UUID orderId;
    private BigDecimal priceAtPurchase;
    private LocalDateTime purchaseTime;
    private OrderProductSummary product;
    private List<OrderItemAddOnResponse> addons;
    private String vendorName;
    private UUID vendorId;
}
