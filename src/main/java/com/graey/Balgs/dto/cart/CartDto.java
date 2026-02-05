package com.graey.Balgs.dto.cart;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CartDto {
    @NotNull
    private String userId;

    @NotNull
    private String productId;
}
