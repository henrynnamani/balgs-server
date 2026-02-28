package com.graey.Balgs.controller;

import com.graey.Balgs.common.messages.AuthMessages;
import com.graey.Balgs.common.utils.ApiResponse;
import com.graey.Balgs.dto.auths.AuthRequest;
import com.graey.Balgs.dto.auths.AuthResponse;
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

    @PostMapping("/register")
    @Operation(summary = "register new user", security = {})
    public ResponseEntity<ApiResponse<AuthResponse>> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(AuthMessages.REGISTRATION_SUCCESSFUL, authService.register(request)));
    }

    @PostMapping("/login")
    @Operation(summary = "login as existing user", security = {})
    public ResponseEntity<ApiResponse<AuthResponse>> authenticate(@RequestBody AuthRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(AuthMessages.LOGIN_SUCCESSFUL, authService.authenticate(request)));
    }
}
