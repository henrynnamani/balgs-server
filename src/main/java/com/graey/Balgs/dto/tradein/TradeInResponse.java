package com.graey.Balgs.dto.tradein;

import com.graey.Balgs.common.enums.TradeInStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class TradeInResponse {
        private UUID id;
        private String brand;
        private String model;
        private Integer storageSize;
        private Integer batteryHealth;
        private boolean faceIdPresent;
        private boolean trueTonePresent;
        private String faults;
        private String recentRepairs;
        private String modelTradedFor;
        private Integer modelTradedForStorageSize;
        private String phoneVideo;
        private String receiptImage;
        private TradeInStatus status;
        private BigDecimal vendorValuation;
        private UUID userId;
}
