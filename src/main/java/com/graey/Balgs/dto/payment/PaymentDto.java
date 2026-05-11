package com.graey.Balgs.dto.payment;

import com.graey.Balgs.common.enums.PaymentProvider;
import com.graey.Balgs.common.enums.PaymentType;
import lombok.Data;

public record PaymentDto(
        String[] orderIds,
        String tradeInId,
        PaymentType type,
        PaymentProvider provider
) {
}
