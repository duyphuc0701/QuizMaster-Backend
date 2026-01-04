package com.example.quizmaster.controller;

import com.example.quizmaster.entity.Quiz;
import com.example.quizmaster.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quizzes")
public class QuizController {

    private final QuizService quizService;

    @Autowired
    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @GetMapping("/public")
    public ResponseEntity<List<Quiz>> getAllPublicQuizzes() {
        return ResponseEntity.ok(quizService.getAllPublicQuizzes());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Quiz>> getQuizzesByUser(@PathVariable String userId) {
        return ResponseEntity.ok(quizService.getQuizzesByUser(userId));
    }
}
