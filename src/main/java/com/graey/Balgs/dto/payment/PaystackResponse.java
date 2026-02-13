package com.graey.Balgs.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaystackResponse {
    private String authorizationUrl;
}
