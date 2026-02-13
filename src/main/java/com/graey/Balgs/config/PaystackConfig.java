package com.graey.Balgs.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class PaystackConfig {
    @Value("${paystack.secretKey}")
    private String secretKey;

    @Value("${paystack.baseUrl}")
    private String baseUrl;
}
