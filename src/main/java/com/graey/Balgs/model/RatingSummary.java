package com.graey.Balgs.model;

import com.graey.Balgs.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "rating_summary")
public class RatingSummary extends BaseEntity {
    @OneToOne(cascade = CascadeType.ALL)
    private Vendor vendor;

    @Column(nullable = false)
    private long totalRatings = 0;

    private long fiveStar = 0;
    private long fourStar = 0;
    private long threeStar = 0;
    private long twoStar = 0;
    private long oneStar = 0;

    private double averageRating = 0.0;

    @Version
    private long version;
}
