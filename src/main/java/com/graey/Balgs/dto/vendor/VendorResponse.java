package com.graey.Balgs.dto.vendor;

import com.graey.Balgs.common.enums.VendorStatus;
import lombok.Data;

import java.util.UUID;

@Data
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
