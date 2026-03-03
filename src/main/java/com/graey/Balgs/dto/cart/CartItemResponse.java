package com.graey.Balgs.dto.cart;

import com.graey.Balgs.dto.addon.AddOnProductResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartItemResponse {
    private UUID id;
    private UUID productId;
    private BigDecimal priceAtAdd;
    private List<AddOnProductResponse> addons = new ArrayList<>();
}
