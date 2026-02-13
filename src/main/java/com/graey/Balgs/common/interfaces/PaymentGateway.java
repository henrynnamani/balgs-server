package com.graey.Balgs.common.interfaces;

import com.graey.Balgs.common.enums.PaymentProvider;

import java.math.BigDecimal;
import java.util.UUID;

public interface PaymentGateway {
    Object initiate(UUID reference, String email,  BigDecimal amount);

    boolean verify(String reference);
}
