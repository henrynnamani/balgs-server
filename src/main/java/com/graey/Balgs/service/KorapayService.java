package com.graey.Balgs.service;

import com.graey.Balgs.common.interfaces.PaymentGateway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class KorapayService implements PaymentGateway {

    @Value("${korapay.secret-key}")
    private String secretKey;

    @Value("${korapay.base-url}")
    private String baseUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    private HttpHeaders authHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + secretKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    // ── Initialize Payment ──
    public Map initiate(String reference, BigDecimal amount, String email, String name, String callbackUrl) {
        Map<String, Object> body = new HashMap<>();
        body.put("reference", reference);
        body.put("amount", amount);
        body.put("currency", "NGN");
        body.put("notification_url", callbackUrl);
        body.put("customer", Map.of("email", email, "name", name));

        HttpEntity<Map> request = new HttpEntity<>(body, authHeaders());
        ResponseEntity<Map> response = restTemplate.postForEntity(
                baseUrl + "/charges/initialize", request, Map.class
        );
        return response.getBody();
    }

    public boolean verify(String reference) {
        HttpEntity<Void> request = new HttpEntity<>(authHeaders());
        ResponseEntity<Map> response = restTemplate.exchange(
                baseUrl + "/charges/" + reference,
                HttpMethod.GET, request, Map.class
        );
        return response.getBody().isEmpty();
    }

    @Override
    public Object initiate(UUID reference, String email, BigDecimal amount) {
        return null;
    }
}