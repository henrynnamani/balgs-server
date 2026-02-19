package com.graey.Balgs.model;

import com.graey.Balgs.common.enums.Role;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name = "users")
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;
    private String username;
    private String password;

    @OneToOne
    private Cart cart;

    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;
}
