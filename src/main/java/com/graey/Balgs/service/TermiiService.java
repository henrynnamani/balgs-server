package com.graey.Balgs.service;

import com.graey.Balgs.dto.termii.TermiiSmsRequest;
import com.graey.Balgs.dto.termii.TermiiSmsResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@Slf4j
public class TermiiService {

    private final RestClient restClient;
    private final String apiKey;
    private final String senderId;

    public TermiiService(
            @Value("${termii.api-key}") String apiKey,
            @Value("${termii.sender-id}") String senderId,
            @Value("${termii.base-url}") String baseUrl
    ) {
        this.apiKey = apiKey;
        this.senderId = senderId;
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public void sendSms(String phoneNumber, String message) {
        TermiiSmsRequest request = TermiiSmsRequest.builder()
                .to(formatPhoneNumber(phoneNumber))
                .from(senderId)
                .sms(message)
                .type("plain")
                .channel("generic")
                .apiKey(apiKey)
                .build();

        try {
            TermiiSmsResponse response = restClient.post()
                    .uri("/api/sms/send")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(TermiiSmsResponse.class);

            log.info("SMS sent to {}: {}", phoneNumber, response.getMessageId());
        } catch (Exception e) {
            log.error("SMS failed to {}: {}", phoneNumber, e.getMessage());
        }
    }

    private String formatPhoneNumber(String phone) {
        if (phone.startsWith("+")) return phone.substring(1);
        if (phone.startsWith("0")) return "234" + phone.substring(1);
        return phone;
    }
}