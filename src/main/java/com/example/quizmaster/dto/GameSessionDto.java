package com.example.quizmaster.dto;

import com.example.quizmaster.entity.GameSession;

import jakarta.validation.constraints.NotBlank;

public class GameSessionDto {

    // 1. INCOMING: Request to start a session
    public static class CreateRequest {
        @NotBlank(message = "Quiz ID is required")
        private String quizId;

        // Default Constructor
        public CreateRequest() {
        }

        public String getQuizId() {
            return quizId;
        }

        public void setQuizId(String quizId) {
            this.quizId = quizId;
        }
    }

    // 2. OUTGOING: Response back to the host
    public static class Response {
        private String sessionId;
        private String gamePin;
        private String status;
        private String quizTitle;
        private String hostId;

        // Default Constructor
        public Response() {
        }

        // Constructor for easy mapping
        public Response(String sessionId, String gamePin, String status, String quizTitle, String hostId) {
            this.sessionId = sessionId;
            this.gamePin = gamePin;
            this.status = status;
            this.quizTitle = quizTitle;
            this.hostId = hostId;
        }

        public static Response from(GameSession session) {
            return new Response(
                    session.getId(),
                    session.getGamePin(),
                    session.getStatus().name(),
                    session.getQuiz().getTitle(),
                    session.getHostId());
        }

        public String getSessionId() {
            return sessionId;
        }

        public void setSessionId(String sessionId) {
            this.sessionId = sessionId;
        }

        public String getGamePin() {
            return gamePin;
        }

        public void setGamePin(String gamePin) {
            this.gamePin = gamePin;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getQuizTitle() {
            return quizTitle;
        }

        public void setQuizTitle(String quizTitle) {
            this.quizTitle = quizTitle;
        }

        public String getHostId() {
            return hostId;
        }

        public void setHostId(String hostId) {
            this.hostId = hostId;
        }
    }

    public static class JoinRequest {
        @NotBlank(message = "Game PIN is required")
        private String gamePin;

        @NotBlank(message = "Nickname is required")
        private String nickname;

        // Getters & Setters
        public String getGamePin() {
            return gamePin;
        }

        public void setGamePin(String gamePin) {
            this.gamePin = gamePin;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }
    }

    public static class JoinResponse {
        private Long playerId;
        private String nickname;
        private String sessionId;

        public JoinResponse(Long playerId, String nickname, String sessionId) {
            this.playerId = playerId;
            this.nickname = nickname;
            this.sessionId = sessionId;
        }

        // Getters & Setters
        public Long getPlayerId() {
            return playerId;
        }

        public void setPlayerId(Long playerId) {
            this.playerId = playerId;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getSessionId() {
            return sessionId;
        }

        public void setSessionId(String sessionId) {
            this.sessionId = sessionId;
        }
    }
}