package com.example.quizmaster.controller;

import com.example.quizmaster.dto.CreateSessionDto;
import com.example.quizmaster.entity.GameSession;
import com.example.quizmaster.service.GameSessionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sessions")
public class GameSessionController {

    private final GameSessionService gameSessionService;

    @Autowired
    public GameSessionController(GameSessionService gameSessionService) {
        this.gameSessionService = gameSessionService;
    }

    @PostMapping
    public ResponseEntity<CreateSessionDto.Response> createSession(
            @Valid @RequestBody CreateSessionDto.CreateRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        // 1. Extract User ID (subject) from the JWT token
        String userId = jwt.getSubject();

        // 2. Call Service to create the session
        // (Service handles the logic: Is it Public? Is User the Creator?)
        GameSession session = gameSessionService.createSession(request.getQuizId(), userId);

        // 3. Map Entity to DTO Response
        CreateSessionDto.Response response = CreateSessionDto.Response.from(session);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{sessionId}/start")
    public ResponseEntity<CreateSessionDto.Response> startSession(
            @PathVariable String sessionId,
            @AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        GameSession session = gameSessionService.startGameSession(sessionId, userId);
        CreateSessionDto.Response response = CreateSessionDto.Response.from(session);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{sessionId}/end")
    public ResponseEntity<CreateSessionDto.Response> endSession(
            @PathVariable String sessionId,
            @AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        GameSession session = gameSessionService.endGameSession(sessionId, userId);
        CreateSessionDto.Response response = CreateSessionDto.Response.from(session);
        return ResponseEntity.ok(response);
    }
}