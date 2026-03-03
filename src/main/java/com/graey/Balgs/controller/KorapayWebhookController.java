package com.graey.Balgs.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/webhook")
public class KorapayWebhookController {

    @Value("${korapay.secret-key}")
    private String secretKey;

    @PostMapping("/korapay")
    public ResponseEntity<Void> handleWebhook(
            @RequestHeader("x-korapay-signature") String signature,
            @RequestBody String rawBody
    ) {
        try {
            // 1. Parse body to extract data object
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(rawBody);
            String dataJson = mapper.writeValueAsString(root.get("data"));

            // 2. Verify signature
            if (!isValidSignature(dataJson, signature)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            // 3. Handle event
            String event = root.get("event").asText();
            if ("charge.success".equals(event)) {
                String reference = root.get("data").get("payment_reference").asText();
                String status = root.get("data").get("status").asText();
                // TODO: update your order/payment status in DB
            }

            return ResponseEntity.ok().build();

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    private boolean isValidSignature(String data, String signature) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(keySpec);
        byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));

        StringBuilder hex = new StringBuilder();
        for (byte b : hash) {
            hex.append(String.format("%02x", b));
        }
        return hex.toString().equals(signature);
    }
}