package com.graey.Balgs.controller;

import com.graey.Balgs.common.utils.ApiResponse;
import com.graey.Balgs.dto.user.CreateUser;
import com.graey.Balgs.model.User;
import com.graey.Balgs.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("users")
public class UserController {

    @Autowired
    private UserService service;

    @Operation(summary = "create new user")
    @PostMapping
    public ResponseEntity<ApiResponse<User>> createUser(@RequestBody CreateUser user) {
        return service.createUser(user);
    }
}
