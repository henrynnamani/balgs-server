package com.graey.Balgs.model;

import com.graey.Balgs.common.enums.VendorStatus;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "vendors")
public class Vendor {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = true)
    private String businessName;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    private String location;

    @Enumerated(EnumType.STRING)
    private VendorStatus status = VendorStatus.PENDING;

    @Column(nullable = false)
    private Boolean verified = false;

    @Column(nullable = false)
    private String phoneNumber;

    private Integer stocksAvailable = 0;
    private String accountNumber;
    private String bankName;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Product> products = new ArrayList<>();

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Rating> ratings = new ArrayList<>();

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    private RatingSummary ratingSummary;

    @CreationTimestamp
    private Instant createdAt;
}
