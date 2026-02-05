package com.graey.Balgs.common.messages;

import com.graey.Balgs.common.enums.VendorStatus;

public final class VendorMessages {

    private VendorMessages() {}

    public static final String VENDOR_CREATED_SUCCESSFULLY = "Vendor created successfully";
    public static final String VENDOR_NOTFOUND = "Vendor not found";
    public static final String VENDOR_STATUS_UPDATED = "Vendor status updated successfully";
    public static final String VENDOR_VERIFIED = "Vendor verified successfully";
    public static final String VERIFICATION_REVOKED = "Vendor verification revoked";
    public static final String VENDOR_PROFILE_UPDATED = "Vendor profile updated successfully";

    public static String VENDOR_HAS_CURRENT_STATUS(VendorStatus status) {
        return "Vendor already has set status " + status;
    }
}