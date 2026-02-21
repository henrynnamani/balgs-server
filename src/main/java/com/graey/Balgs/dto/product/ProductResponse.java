package com.graey.Balgs.dto.product;

import com.graey.Balgs.common.enums.ProductCondition;
import com.graey.Balgs.common.enums.RamSize;
import com.graey.Balgs.common.enums.RomSize;
import com.graey.Balgs.dto.vendor.VendorResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {
    private String model;
    private BigDecimal price;
    private int batteryHealth;
    private String color;
    private ProductCondition condition;
    private RamSize ramSize;
    private RomSize romSize;
    private boolean faceIdPresent;
    private boolean trueTonePresent;
    private List<String> imageUrls;
    private VendorResponse vendor;
}
