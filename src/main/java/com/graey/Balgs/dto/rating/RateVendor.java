package com.graey.Balgs.dto.rating;

import lombok.Data;

@Data
public class RateVendor {
    private String vendorId;
    private String userId;
    private String review;
    private int rating;
}
