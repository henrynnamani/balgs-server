package com.graey.Balgs.model;

import com.graey.Balgs.common.enums.BroadcastPaymentStatus;
import com.graey.Balgs.common.enums.TradeInStatus;
import com.graey.Balgs.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity()
@Table(name = "trade-in")
public class TradeIn extends BaseEntity {
    private String brand;
    private String model;
    private Integer storageSize;
    private Integer batteryHealth;

    @Column(nullable = true)
    private boolean faceIdPresent;

    @Column(nullable = true)
    private boolean trueTonePresent;
    private String faults;
    private String recentRepairs;
    private String modelTradedFor;
    private Integer modelTradedForStorageSize;
    private String phoneVideo;

    @Column(nullable = true)
    private String IMEI;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BroadcastPaymentStatus broadcastPaymentStatus = BroadcastPaymentStatus.UNPAID;

    private String broadcastPaymentReference;

    @Column(nullable = true)
    private String receiptImage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TradeInStatus status = TradeInStatus.PENDING;

    @Column(nullable = true)
    private BigDecimal vendorValuation; // set by vendor during review

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // the customer who submitted
}