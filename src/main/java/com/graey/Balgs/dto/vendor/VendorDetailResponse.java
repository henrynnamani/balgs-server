package com.graey.Balgs.dto.vendor;

import com.graey.Balgs.common.enums.VendorStatus;
import com.graey.Balgs.dto.product.ProductDetailResponse;
import com.graey.Balgs.dto.product.ProductResponse;
import com.graey.Balgs.dto.rating.RatingResponse;
import com.graey.Balgs.dto.rating.RatingSummaryResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VendorDetailResponse {
    private UUID id;
    private String businessName;
    private UUID userId;
    private String location;
    private VendorStatus status;
    private Boolean verified;
    private String phoneNumber;
    private Integer stocksAvailable;
    private String accountNumber;
    private String bankName;
    private List<RatingResponse> ratings = new ArrayList<>();
    private RatingSummaryResponse ratingSummary;
}
