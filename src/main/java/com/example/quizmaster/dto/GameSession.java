package com.example.quizmaster.dto;

import java.util.HashSet;
import java.util.Set;

public class GameSession {
    private String gamePin;
    private Long quizId;
    private Long hostId; // User ID of the host
    private Set<String> players = new HashSet<>();
    private String status; // WAITING, STARTED, FINISHED

    public GameSession(String gamePin, Long quizId, Long hostId) {
        this.gamePin = gamePin;
        this.quizId = quizId;
        this.hostId = hostId;
        this.status = "WAITING";
    }

    public String getGamePin() {
        return gamePin;
    }

    public void setGamePin(String gamePin) {
        this.gamePin = gamePin;
    }

    public Long getQuizId() {
        return quizId;
    }

    public void setQuizId(Long quizId) {
        this.quizId = quizId;
    }

    public Long getHostId() {
        return hostId;
    }

    public void setHostId(Long hostId) {
        this.hostId = hostId;
    }

    public Set<String> getPlayers() {
        return players;
    }

    public void setPlayers(Set<String> players) {
        this.players = players;
    }

    public void addPlayer(String nickname) {
        this.players.add(nickname);
    }

    public void removePlayer(String nickname) {
        this.players.remove(nickname);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
