package com.graey.Balgs.dto.cart;

import com.graey.Balgs.common.enums.ProductCondition;
import com.graey.Balgs.common.enums.RamSize;
import com.graey.Balgs.common.enums.RomSize;
import com.graey.Balgs.dto.addon.AddOnProductResponse;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class CartListResponse {
    private UUID id;
    private String imageUrl;
    private RomSize romSize;
    private String color;
    private ProductCondition condition;
    private BigDecimal price;
    private List<AddOnProductResponse> addons = new ArrayList<>();
}
