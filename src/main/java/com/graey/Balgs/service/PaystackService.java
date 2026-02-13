package com.graey.Balgs.service;

import com.graey.Balgs.common.interfaces.PaymentGateway;
import com.graey.Balgs.config.PaystackConfig;
import com.graey.Balgs.dto.payment.PaystackResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaystackService implements PaymentGateway {

    @Autowired
    private PaystackConfig config;

    private final RestTemplate restTemplate = new RestTemplate();

    public boolean verifyPaystackSignature(String payload, String signature) {

        try {
            String secretKey = config.getSecretKey();

            Mac sha512Hmac = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKeySpec =
                    new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8),
                            "HmacSHA512");

            sha512Hmac.init(secretKeySpec);

            byte[] hash = sha512Hmac.doFinal(
                    payload.getBytes(StandardCharsets.UTF_8)
            );

            String generatedSignature =
                    HexFormat.of().formatHex(hash);

            return generatedSignature.equalsIgnoreCase(signature);

        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Object initiate(UUID reference, String email, BigDecimal amount) {
        String url = config.getBaseUrl() + "/transaction/initialize";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(config.getSecretKey());

        Map<String, Object> body = Map.of(
                "email", email,
                "amount", amount.multiply(BigDecimal.valueOf(100)), // kobo
                "reference", reference.toString()
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

        Map<String, Object> data = (Map<String, Object>) response.getBody().get("data");
        String authUrl = data.get("authorization_url").toString();

        return new PaystackResponse(authUrl);
    }

    @Override
    public boolean verify(String reference) {
        String url = config.getBaseUrl() + "/transaction/verify/" + reference;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(config.getSecretKey());

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                request,
                Map.class
        );

        Map<String, Object> data = (Map<String, Object>) response.getBody().get("data");
        String status = data.get("status").toString(); // success, failed, etc.

        return "success".equalsIgnoreCase(status);
    }
}
