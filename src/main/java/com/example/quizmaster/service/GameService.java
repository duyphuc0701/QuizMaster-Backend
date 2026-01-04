package com.example.quizmaster.service;

import com.example.quizmaster.dto.GameSession;
import com.example.quizmaster.entity.Quiz;
import com.example.quizmaster.entity.User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GameService {

    private final QuizService quizService;
    private final UserService userService;

    // In-memory storage for active game sessions
    private final Map<String, GameSession> activeSessions = new ConcurrentHashMap<>();

    public GameService(QuizService quizService, UserService userService) {
        this.quizService = quizService;
        this.userService = userService;
    }

    public GameSession createGame(Long quizId, String hostEmail) {
        Quiz quiz = quizService.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        User host = userService.findByEmail(hostEmail)
                .orElseThrow(() -> new RuntimeException("Host not found"));

        String gamePin = generateGamePin();
        GameSession session = new GameSession(gamePin, quiz.getId(), host.getId());
        activeSessions.put(gamePin, session);

        return session;
    }

    public GameSession getGameSession(String gamePin) {
        return activeSessions.get(gamePin);
    }

    public void joinGame(String gamePin, String nickname) {
        GameSession session = activeSessions.get(gamePin);
        if (session == null) {
            throw new RuntimeException("Game session not found");
        }
        if (!"WAITING".equals(session.getStatus())) {
            throw new RuntimeException("Game has already started or finished");
        }

        session.addPlayer(nickname);
    }

    private String generateGamePin() {
        Random random = new Random();
        String pin;
        do {
            pin = String.format("%06d", random.nextInt(1000000));
        } while (activeSessions.containsKey(pin));
        return pin;
    }
}
