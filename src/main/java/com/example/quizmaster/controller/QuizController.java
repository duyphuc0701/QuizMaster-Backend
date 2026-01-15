package com.example.quizmaster.controller;

import com.example.quizmaster.dto.QuizDto;
import com.example.quizmaster.entity.Quiz;
import com.example.quizmaster.service.QuizService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/quizzes")
public class QuizController {

    @Autowired
    private QuizService quizService;

    @PostMapping
    public ResponseEntity<?> createQuiz(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody QuizDto.CreateRequest request) {

        // 1. Extract User ID from Keycloak Token
        String userId = jwt.getSubject();

        // 2. Call Service
        Quiz createdQuiz = quizService.createQuiz(request, userId);

        // 3. Return Response (Returning ID is usually enough)
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(java.util.Map.of(
                        "message", "Quiz created successfully",
                        "id", createdQuiz.getId()));
    }
}
