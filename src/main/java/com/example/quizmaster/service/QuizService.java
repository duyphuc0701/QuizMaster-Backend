package com.example.quizmaster.service;

// Service to manage Quizzes

import com.example.quizmaster.entity.Quiz;

import org.springframework.stereotype.Service;

@Service
public class QuizService {

    private final static java.util.List<Quiz> quizzes = new java.util.ArrayList<>();

    public QuizService() {
    }

    public java.util.List<Quiz> getAllPublicQuizzes() {
        return quizzes.stream().filter(Quiz::isPublic).toList();
    }

    public java.util.List<Quiz> getQuizzesByUser(Long userId) {
        return quizzes.stream().filter(q -> q.getCreator().getId().equals(userId)).toList();
    }

    public void saveQuiz(Quiz quiz) {
        if (quiz.getId() == null) {
            quiz.setId((long) (quizzes.size() + 1));
        }
        quizzes.add(quiz);
    }

    public java.util.Optional<Quiz> findById(Long id) {
        return quizzes.stream().filter(q -> q.getId().equals(id)).findFirst();
    }
}
