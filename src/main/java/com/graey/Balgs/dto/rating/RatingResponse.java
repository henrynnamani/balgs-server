package com.graey.Balgs.dto.rating;

import com.graey.Balgs.dto.user.UserResponse;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class RatingResponse {
    private UUID id;
    private UUID vendorId;
    private UserResponse user;
    private int rating;
    private String review;
    private LocalDateTime createdAt;
}
