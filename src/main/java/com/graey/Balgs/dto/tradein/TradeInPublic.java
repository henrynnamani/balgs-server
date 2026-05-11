package com.graey.Balgs.dto.tradein;

import com.graey.Balgs.common.enums.TradeInStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TradeInPublic {
    private String brand;
    private String model;
    private Integer storageSize;
    private String modelTradedFor;
    private Integer modelTradedForStorageSize;
    private TradeInStatus status;
    private LocalDateTime createdAt;
}
