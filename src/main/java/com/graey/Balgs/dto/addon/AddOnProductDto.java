package com.graey.Balgs.dto.addon;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AddOnProductDto {
    private String name;
    private BigDecimal price;
}
