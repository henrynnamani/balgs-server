package com.graey.Balgs.dto.tradein;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TradeInReview {
    @NotNull
    @DecimalMin("0.0")
    private BigDecimal vendorValuation;
}
