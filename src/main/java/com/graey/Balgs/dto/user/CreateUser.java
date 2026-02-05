package com.graey.Balgs.dto.user;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateUser {
    @NotNull
    private String email;

    @NotNull
    private String username;

    @NotNull
    private String password;
}
