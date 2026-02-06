package com.graey.Balgs.dto.addon;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class AddOnProductResponse {
    private UUID id;
    private String name;
    private BigDecimal price;
    private String imageUrl;
    private int stock;
}
