package com.graey.Balgs.common.interfaces;

import com.graey.Balgs.common.enums.PaymentProvider;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

public interface PaymentGateway {
    // Update the PaymentGateway interface
    Object initiate(String[] entityIds, String email, BigDecimal amount, Map<String, Object> extraMetadata);

    boolean verify(String reference);
}
