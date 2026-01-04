package com.example.quizmaster.controller;

import com.example.quizmaster.dto.LoginRequest;
import com.example.quizmaster.dto.LoginResponse;
import com.example.quizmaster.dto.MessageResponse;
import com.example.quizmaster.dto.SignUpRequest;
import com.example.quizmaster.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signin")
    public ResponseEntity<LoginResponse> signin(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.login(loginRequest));
    }

    @PostMapping("/signup")
    public ResponseEntity<MessageResponse> signup(
            @RequestBody SignUpRequest signUpRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.register(signUpRequest));
    }
}
