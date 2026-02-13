package com.graey.Balgs.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OpayPaymentResponse {
    private String authorizationUrl;
}
