package com.graey.Balgs.dto.order;

import com.graey.Balgs.model.Order;
import com.graey.Balgs.model.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemResponse {
    private UUID id;
    private Product product;
    private UUID orderId;
    private BigDecimal priceAtPurchase;
    private List<OrderItemAddOnResponse> addons;
    private LocalDateTime purchaseTime;
}
