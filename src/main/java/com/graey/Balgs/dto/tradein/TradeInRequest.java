package com.graey.Balgs.dto.tradein;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TradeInRequest {
    @NotBlank
    private String brand;
    @NotBlank private String model;
    @NotNull
    private Integer storageSize;
    @Min(0) @Max(100) private Integer batteryHealth;
    private boolean faceIdPresent;
    private boolean trueTonePresent;
    private String faults;
    private String recentRepairs;
    @NotBlank private String modelTradedFor;
    @NotNull private Integer modelTradedForStorageSize;
}
