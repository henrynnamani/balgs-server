package com.graey.Balgs.dto.order;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class OrderItemAddOnResponse {
    private UUID id;
    private UUID orderItemId;
    private UUID productId;
    private String productName;
    private BigDecimal priceAtPurchase;
}
