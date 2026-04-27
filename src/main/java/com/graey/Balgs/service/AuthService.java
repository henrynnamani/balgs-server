package com.graey.Balgs.service;

import com.graey.Balgs.common.enums.Role;
import com.graey.Balgs.dto.auths.AuthRequest;
import com.graey.Balgs.dto.auths.AuthResponse;
import com.graey.Balgs.dto.auths.GoogleAuthRequest;
import com.graey.Balgs.dto.user.RegisterRequest;
import com.graey.Balgs.dto.user.UserResponse;
import com.graey.Balgs.model.Cart;
import com.graey.Balgs.model.User;
import com.graey.Balgs.repo.CartRepo;
import com.graey.Balgs.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepo userRepository;
    private final JwtService jwtService;
    private final CartRepo cartRepository;

    public AuthResponse googleSignIn(GoogleAuthRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseGet(() -> createUser(request));

        // Link googleId if missing (first time Google login)
        if (user.getGoogleId() == null) {
            user.setGoogleId(request.getGoogleId());
            user.setAvatar(request.getAvatar());
            userRepository.save(user);
        }

        return AuthResponse.builder()
                .token(jwtService.generateToken(user))
                .userId(user.getId().toString())
                .name(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    private User createUser(GoogleAuthRequest request) {
//        Cart cart = cartRepository.save(new Cart());

        return userRepository.save(User.builder()
                .email(request.getEmail())
                .fullName(request.getName())
                .googleId(request.getGoogleId())
                .avatar(request.getAvatar())
                .role(Role.USER)
//                .cart(cart)
                .build());
    }
}