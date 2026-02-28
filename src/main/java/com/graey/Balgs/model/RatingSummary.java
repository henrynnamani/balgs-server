package com.graey.Balgs.model;

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
public class RatingSummary {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

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
