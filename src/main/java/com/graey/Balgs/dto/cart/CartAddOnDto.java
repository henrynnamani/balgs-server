package com.graey.Balgs.dto.cart;

import lombok.Data;

import java.util.UUID;

@Data
public class CartAddOnDto {
    private UUID cartItemId;
    private UUID addOnProductId;
}
