package com.graey.Balgs.dto.vendor;

import com.graey.Balgs.common.enums.VendorStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VendorResponse {
    private UUID id;
    private UUID userId;
    private String location;
    private VendorStatus status;
    private Boolean verified;
    private String phoneNumber;
    private Integer stocksAvailable;
    private String accountNumber;
    private String bankName;
}
