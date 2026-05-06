package com.graey.Balgs.dto.termii;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TermiiSmsResponse {
    @JsonProperty("message_id")
    private String messageId;
    private String message;
    private String balance;
    private String user;
}
