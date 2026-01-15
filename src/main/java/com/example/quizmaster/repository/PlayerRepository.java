package com.example.quizmaster.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.quizmaster.entity.GameSession;
import com.example.quizmaster.entity.Player;

public interface PlayerRepository extends JpaRepository<Player, Long> {
    boolean existsByGameSessionAndNickname(GameSession session, String nickname);
}
