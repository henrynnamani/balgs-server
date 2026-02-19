package com.graey.Balgs.dto.order;

import com.graey.Balgs.model.AddOnProduct;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemAddOnResponse {
    private UUID id;
    private UUID orderItemId;
    private AddOnProduct product;
    private BigDecimal priceAtPurchase;
}
