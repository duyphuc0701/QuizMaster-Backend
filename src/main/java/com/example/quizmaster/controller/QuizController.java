package com.example.quizmaster.controller;

import com.example.quizmaster.entity.Quiz;
import com.example.quizmaster.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/quizzes")
public class QuizController {

    private final QuizService quizService;

    @Autowired
    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @GetMapping("/public")
    public ResponseEntity<Page<Quiz>> getAllPublicQuizzes(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(quizService.getAllPublicQuizzes(pageable));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<Quiz>> getQuizzesByUser(@PathVariable Long userId,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(quizService.getQuizzesByUser(userId, pageable));
    }
}
