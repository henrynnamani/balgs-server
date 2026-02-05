package com.graey.Balgs.dto.cart;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
public class CartItemResponse {
    private UUID id;
    private UUID productId;
    private BigDecimal priceAtAdd;
}
