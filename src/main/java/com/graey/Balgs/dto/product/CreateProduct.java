package com.graey.Balgs.dto.product;

import com.graey.Balgs.common.enums.ProductCondition;
import com.graey.Balgs.common.enums.RamSize;
import com.graey.Balgs.common.enums.RomSize;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateProduct {
    @NotBlank
    private String model;

    @NotNull
    private RamSize ramSize;

    @NotNull
    private RomSize romSize;
    private ProductCondition condition;
    private Boolean faceIdPresent;
    private Boolean trueTonePresent;

    private String vendorId;

    @Min(0)
    @Max(100)
    private Integer batteryHealth;

    @DecimalMin("0.0")
    private BigDecimal price;

    private String color;
}
