package com.graey.Balgs.dto.user;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class RegisterRequest {
    @NotNull
    private String email;

    @NotNull
    private String username;

    @NotNull
    private String phoneNumber;

    @NotNull
    private String password;

    private String role;
}
