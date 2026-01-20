package com.example.quizmaster.controller;

import com.example.quizmaster.dto.AnswerSubmissionDto;
import com.example.quizmaster.service.GameSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sessions")
public class GameplayController {

    private final GameSessionService gameSessionService;

    @Autowired
    public GameplayController(GameSessionService gameSessionService) {
        this.gameSessionService = gameSessionService;
    }

    @PostMapping("/{sessionId}/next-question")
    public ResponseEntity<Void> nextQuestion(
            @PathVariable String sessionId,
            @AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        gameSessionService.sendNextQuestion(sessionId, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{sessionId}/submit-answer")
    public ResponseEntity<AnswerSubmissionDto.Response> submitAnswer(
            @PathVariable String sessionId,
            @RequestBody AnswerSubmissionDto.Request request // Using the nested class
    ) {

        AnswerSubmissionDto.Response response = gameSessionService.submitAnswer(sessionId, request);

        return ResponseEntity.ok(response);
    }
}