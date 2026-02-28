package com.graey.Balgs.dto.auths;

import com.graey.Balgs.dto.user.UserResponse;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
    private String token;
    private UserResponse user;
}
