package com.graey.Balgs.service;

import com.graey.Balgs.common.messages.UserMessages;
import com.graey.Balgs.common.utils.ApiResponse;
import com.graey.Balgs.dto.user.CreateUser;
import com.graey.Balgs.model.User;
import com.graey.Balgs.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepo repo;

    public ResponseEntity<ApiResponse<User>> createUser(CreateUser user) {
        try {
            User newUser = new User();

            newUser.setUsername(user.getUsername());
            newUser.setPassword(user.getPassword());
            newUser.setEmail(user.getEmail());

            User savedUser = repo.save(newUser);

            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(UserMessages.USER_CREATED, savedUser));
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }
}