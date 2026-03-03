package com.graey.Balgs.dto.cart;

import com.graey.Balgs.common.enums.ProductCondition;
import com.graey.Balgs.common.enums.RomSize;
import com.graey.Balgs.dto.addon.AddOnProductResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDetailResponse {
    private UUID id;
    private String name;
    private RomSize romSize;
    private String color;
    private ProductCondition condition;
    private String imageUrl;
    private BigDecimal price;
    private List<AddOnProductResponse> addons;
}
