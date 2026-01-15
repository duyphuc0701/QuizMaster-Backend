package com.example.quizmaster.service;

import com.example.quizmaster.entity.Quiz;
import com.example.quizmaster.entity.User;
import com.example.quizmaster.repository.QuizRepository;
import com.example.quizmaster.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class QuizService {

    private final QuizRepository quizRepository;
    private final UserRepository userRepository;

    @Autowired
    public QuizService(QuizRepository quizRepository, UserRepository userRepository) {
        this.quizRepository = quizRepository;
        this.userRepository = userRepository;
    }

    public List<Quiz> getAllQuizzes(String search, int page, int limit, String sort, boolean publicOnly) {
        Sort sortObj = Sort.by(Sort.Direction.DESC, "id"); // Default "newest"
        if ("title".equalsIgnoreCase(sort)) {
            sortObj = Sort.by(Sort.Direction.ASC, "title");
        }

        Pageable pageable = PageRequest.of(Math.max(0, page - 1), limit, sortObj);

        Specification<Quiz> spec = Specification.where((Specification<Quiz>) null);

        if (publicOnly) {
            spec = spec.and((root, query, cb) -> cb.isTrue(root.get("isPublic")));
        }

        if (search != null && !search.isEmpty()) {
            String lowerSearch = search.toLowerCase();
            spec = spec.and((root, query, cb) -> cb.or(
                    cb.like(cb.lower(root.get("title")), "%" + lowerSearch + "%"),
                    cb.like(cb.lower(root.get("description")), "%" + lowerSearch + "%")));
        }

        Page<Quiz> quizPage = quizRepository.findAll(spec, pageable);
        return quizPage.getContent();
    }

    public void saveQuiz(Quiz quiz) {
        // Handle User
        if (quiz.getCreator() != null && quiz.getCreator().getId() != null) {
            String userId = quiz.getCreator().getId();
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
            quiz.setCreator(user);
        }

        if (quiz.getQuestions() != null) {
            for (com.example.quizmaster.entity.Question question : quiz.getQuestions()) {
                question.setQuiz(quiz); // Back reference
                if (question.getAnswers() != null) {
                    for (com.example.quizmaster.entity.Option answer : question.getAnswers()) {
                        answer.setQuestion(question); // Back reference
                    }
                }
            }
        }
        quizRepository.save(quiz);
    }

    public void updateQuiz(Quiz quiz) {
        // saveQuiz handles update if ID exists, but we need to ensure relationships are
        // set
        saveQuiz(quiz);
    }

    public void deleteQuiz(Long id) {
        quizRepository.deleteById(id);
    }

    public Optional<Quiz> findById(Long id) {
        return quizRepository.findById(id);
    }
}
