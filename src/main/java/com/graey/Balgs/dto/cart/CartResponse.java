package com.graey.Balgs.dto.cart;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
public class CartResponse {
    private UUID id;
    private UUID userId;
    private List<CartItemResponse> items;
    private BigDecimal totalPrice;
}
