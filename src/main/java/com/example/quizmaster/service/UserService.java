package com.example.quizmaster.service;

import com.example.quizmaster.dto.LoginRequest;
import com.example.quizmaster.dto.LoginResponse;
import com.example.quizmaster.entity.User;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {

    private final static java.util.List<User> users = new java.util.ArrayList<>();

    public UserService() {
    }

    public LoginResponse login(LoginRequest request) {
        User user = users.stream()
                .filter(u -> u.getEmail().equals(request.getEmail()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getPassword().equals(request.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        // Just return a dummy token since we are not using JWT anymore
        String token = UUID.randomUUID().toString();

        return new LoginResponse(token, user.getId(), user.getFirstName(), user.getLastName());
    }

    public com.example.quizmaster.dto.MessageResponse register(com.example.quizmaster.dto.SignUpRequest request) {
        if (users.stream().anyMatch(u -> u.getEmail().equals(request.getEmail()))) {
            throw new RuntimeException("Email already in use");
        }

        User user = new User();
        user.setId((long) (users.size() + 1));
        user.setEmail(request.getEmail());
        // Store plain text password
        user.setPassword(request.getPassword());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());

        users.add(user);

        return new com.example.quizmaster.dto.MessageResponse("User registered successfully");
    }

    public void saveUser(User user) {
        if (user.getId() == null) {
            user.setId((long) (users.size() + 1));
        }
        users.add(user);
    }

    public java.util.Optional<User> findByEmail(String email) {
        return users.stream().filter(u -> u.getEmail().equals(email)).findFirst();
    }
}
