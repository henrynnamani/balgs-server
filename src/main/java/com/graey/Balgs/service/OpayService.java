package com.graey.Balgs.service;

import com.graey.Balgs.common.interfaces.PaymentGateway;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class OpayService implements PaymentGateway {

    @Override
    public Object initiate(UUID reference, String email, BigDecimal amount) {
        return null;
    }

    @Override
    public boolean verify(String reference) {
        return false;
    }
}
