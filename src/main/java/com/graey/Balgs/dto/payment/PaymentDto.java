package com.graey.Balgs.dto.payment;

import com.graey.Balgs.common.enums.PaymentProvider;
import lombok.Data;

public record PaymentDto(
        String orderId,
        String email,
        PaymentProvider provider
) {
}
