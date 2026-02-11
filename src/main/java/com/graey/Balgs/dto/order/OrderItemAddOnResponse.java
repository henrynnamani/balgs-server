package com.graey.Balgs.dto.order;

import com.graey.Balgs.model.AddOnProduct;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class OrderItemAddOnResponse {
    private UUID id;
    private UUID orderItemId;
    private AddOnProduct product;
    private BigDecimal priceAtPurchase;
}
