package com.example.quizmaster.controller;

import com.example.quizmaster.dto.GameSession;
import com.example.quizmaster.service.GameService;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/game")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping("/start")
    public ResponseEntity<GameSession> startGame(@RequestBody Map<String, Object> request) {
        String currentPrincipalName = (String) request.get("email");
        if (currentPrincipalName == null) {
            // Fallback for testing if email is not provided
            currentPrincipalName = "user@example.com";
        }

        Long quizId = ((Number) request.get("quizId")).longValue();
        GameSession session = gameService.createGame(quizId, currentPrincipalName);
        return ResponseEntity.ok(session);
    }

    @PostMapping("/join")
    public ResponseEntity<String> joinGame(@RequestBody Map<String, String> request) {
        String gamePin = request.get("gamePin");
        String nickname = request.get("nickname");

        try {
            gameService.joinGame(gamePin, nickname);
            return ResponseEntity.ok("Joined game successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @MessageMapping("/game/{gamePin}/join")
    @SendTo("/topic/game/{gamePin}")
    public String broadcastJoin(@DestinationVariable String gamePin, @Payload String nickname) {
        return nickname + " joined the game!";
    }
}
