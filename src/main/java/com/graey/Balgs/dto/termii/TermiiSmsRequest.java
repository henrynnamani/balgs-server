package com.graey.Balgs.dto.termii;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TermiiSmsRequest {
    private String to;
    @JsonProperty("from")
    private String from;
    private String sms;
    private String type;
    private String channel;
    @JsonProperty("api_key")
    private String apiKey;
}
