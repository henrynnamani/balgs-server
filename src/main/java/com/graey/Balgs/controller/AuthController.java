package com.graey.Balgs.controller;

import com.graey.Balgs.common.messages.AuthMessages;
import com.graey.Balgs.common.utils.ApiResponse;
import com.graey.Balgs.dto.auths.AuthRequest;
import com.graey.Balgs.dto.auths.AuthResponse;
import com.graey.Balgs.dto.auths.GoogleAuthRequest;
import com.graey.Balgs.dto.user.RegisterRequest;
import com.graey.Balgs.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("auths")
@Tag(name = "authentication", description = "")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/google")
    public ResponseEntity<ApiResponse<AuthResponse>> googleSignIn(
            @RequestBody GoogleAuthRequest request) {

        AuthResponse response = authService.googleSignIn(request);
        return ResponseEntity.ok(ApiResponse.success("Sign in successful", response));
    }
}
