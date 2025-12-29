package com.example.quizmaster.service;

import com.example.quizmaster.entity.Quiz;
import com.example.quizmaster.repository.QuizRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class QuizService {

    private final QuizRepository quizRepository;

    @Autowired
    public QuizService(QuizRepository quizRepository) {
        this.quizRepository = quizRepository;
    }

    public Page<Quiz> getAllPublicQuizzes(Pageable pageable) {
        return quizRepository.findByIsPublicTrue(pageable);
    }

    public Page<Quiz> getQuizzesByUser(Long userId, Pageable pageable) {
        return quizRepository.findByCreatorId(userId, pageable);
    }
}
