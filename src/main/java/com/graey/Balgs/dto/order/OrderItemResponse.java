package com.graey.Balgs.dto.order;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@RequiredArgsConstructor
public class OrderItemResponse {
    private UUID id;
    private UUID orderId;
    private UUID productId;
    private String productName;
    private BigDecimal priceAtPurchase;
    private List<OrderItemAddOnResponse> addons = new ArrayList<>();
}
