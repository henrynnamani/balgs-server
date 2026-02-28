package com.graey.Balgs.service;

import com.graey.Balgs.common.enums.Role;
import com.graey.Balgs.dto.auths.AuthRequest;
import com.graey.Balgs.dto.auths.AuthResponse;
import com.graey.Balgs.dto.user.RegisterRequest;
import com.graey.Balgs.dto.user.UserResponse;
import com.graey.Balgs.model.User;
import com.graey.Balgs.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.valueOf(request.getRole()))
                .build();

        userRepo.save(user);

        String token = jwtService.generateToken(user);

        return AuthResponse.builder()
                .token(token)
                .user(
                        UserResponse.builder()
                                .id(user.getId())
                                .username(user.getUsername())
                                .email(user.getEmail())
                                .phoneNumber(user.getPhoneNumber())
                                .build()
                )
                .build();
    }

    public AuthResponse authenticate(AuthRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );
        } catch (AuthenticationException e) {
            System.out.println(e.getMessage());
        }

        User user = userRepo.findByUsername(request.getUsername())
                .orElseThrow();

        String jwt = jwtService.generateToken(user);

        return AuthResponse.builder()
                .token(jwt)
                .user(
                        UserResponse.builder()
                                .id(user.getId())
                                .username(user.getUsername())
                                .email(user.getEmail())
                                .phoneNumber(user.getPhoneNumber())
                                .build()
                )
                .build();
    }
}
