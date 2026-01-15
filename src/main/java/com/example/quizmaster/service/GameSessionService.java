package com.example.quizmaster.service;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;

import com.example.quizmaster.constants.SessionStatus;
import com.example.quizmaster.exception.ApiException;
import com.example.quizmaster.entity.GameSession;
import com.example.quizmaster.entity.Quiz;
import com.example.quizmaster.repository.GameSessionRepository;
import com.example.quizmaster.repository.QuizRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class GameSessionService {
    @Autowired
    private GameSessionRepository sessionRepository;
    @Autowired
    private QuizRepository quizRepository;

    @Transactional
    public GameSession createSession(String quizId, String userId) {
        // 1. Fetch the Quiz
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new EntityNotFoundException("Quiz not found"));

        // 2. Authorization Check
        // If quiz is NOT public, only the creator can host it
        if (!quiz.isPublic()) {
            // Assuming Creator has an ID field that matches the JWT subject (String)
            String creatorId = quiz.getCreator().getId().toString();
            if (!creatorId.equals(userId)) {
                throw new AccessDeniedException("You cannot host a private quiz that belongs to someone else.");
            }
        }

        // 3. Generate a unique 6-digit PIN
        String gamePin = generateUniquePin();

        // 4. Create and Save the Session
        GameSession session = new GameSession();
        session.setQuiz(quiz);
        session.setHostId(userId);
        session.setGamePin(gamePin);
        session.setStatus(SessionStatus.WAITING_FOR_PLAYERS);

        return sessionRepository.save(session);
    }

    @Transactional
    public GameSession startGameSession(String sessionId, String userId) {
        GameSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("Session not found"));

        if (!session.getHostId().equals(userId)) {
            throw new AccessDeniedException("Only the host can start the session");
        }

        // Idempotency check: If already in progress, just return it (don't crash)
        if (session.getStatus() == SessionStatus.IN_PROGRESS) {
            return session;
        }

        if (session.getStatus() != SessionStatus.WAITING_FOR_PLAYERS) {
            throw new ApiException("Cannot start session. Status is: " + session.getStatus(), HttpStatus.BAD_REQUEST);
        }

        session.setStatus(SessionStatus.IN_PROGRESS);
        GameSession savedSession = sessionRepository.save(session);

        // TODO: Broadcast "GAME_STARTED" event via WebSocket to topic
        // /topic/session/{sessionId}

        return savedSession;
    }

    @Transactional
    public GameSession endGameSession(String sessionId, String userId) {
        GameSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("Session not found"));

        if (!session.getHostId().equals(userId)) {
            throw new AccessDeniedException("Only the host can end the session");
        }

        // Idempotency check
        if (session.getStatus() == SessionStatus.FINISHED) {
            return session;
        }

        session.setStatus(SessionStatus.FINISHED);
        GameSession savedSession = sessionRepository.save(session);

        // TODO: Broadcast "GAME_ENDED" event via WebSocket to topic
        // /topic/session/{sessionId}

        return savedSession;
    }

    private String generateUniquePin() {
        Random random = new Random();
        String pin;
        do {
            // Generate number between 100000 and 999999
            int number = 100000 + random.nextInt(900000);
            pin = String.valueOf(number);
        } while (sessionRepository.existsByGamePinAndStatusNot(pin, SessionStatus.FINISHED));
        return pin;
    }
}
