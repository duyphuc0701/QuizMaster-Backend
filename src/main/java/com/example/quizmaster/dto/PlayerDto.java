package com.example.quizmaster.dto;

public class PlayerDto {
    public static class PlayerJoinedMessage {
        public String type = "PLAYER_JOINED";
        public String nickname;
        public Long playerId;

        public PlayerJoinedMessage(String nickname, Long playerId) {
            this.nickname = nickname;
            this.playerId = playerId;
        }
    }
}
