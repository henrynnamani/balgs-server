package com.graey.Balgs.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name = "rating_summary")
public class RatingSummary {
    @Id
    private UUID vendorId;

    private long totalRatings;

    private long fiveStar;
    private long fourStar;
    private long threeStar;
    private long twoStar;
    private long oneStar;

    private double averageRating;

    @Version
    private long version;
}
