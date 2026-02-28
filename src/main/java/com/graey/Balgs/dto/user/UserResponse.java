package com.graey.Balgs.dto.user;

import lombok.*;

import java.util.UUID;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    private UUID id;
    private String email;
    private String username;
    private String phoneNumber;
}
