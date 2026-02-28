package com.graey.Balgs.dto.rating;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class RatingSummaryResponse {
    private UUID id;
    private long totalRatings;
    private double averageRating;
}
