package com.graey.Balgs.dto.vendor;

import com.graey.Balgs.common.enums.VendorStatus;
import com.graey.Balgs.dto.product.ProductResponse;
import com.graey.Balgs.dto.rating.RatingResponse;
import com.graey.Balgs.dto.rating.RatingSummaryResponse;
import com.graey.Balgs.model.Vendor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VendorResponse {
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

    public static VendorResponse from(Vendor vendor) {
        return VendorResponse.builder()
                .id(vendor.getId())
                .businessName(vendor.getBusinessName())
                .userId(vendor.getUser().getId())
                .location(vendor.getLocation())
                .status(vendor.getStatus())
                .verified(vendor.getVerified())
                .phoneNumber(vendor.getPhoneNumber())
                .stocksAvailable(vendor.getStocksAvailable())
                .accountNumber(vendor.getAccountNumber())
                .bankName(vendor.getBankName())
                .build();
    }
}
