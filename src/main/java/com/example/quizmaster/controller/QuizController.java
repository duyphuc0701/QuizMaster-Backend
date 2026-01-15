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

    @PostMapping("")
    public ResponseEntity<?> createQuiz(@RequestBody Quiz quiz,
            @org.springframework.security.core.annotation.AuthenticationPrincipal org.springframework.security.oauth2.jwt.Jwt jwt) {
        if (jwt == null) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.UNAUTHORIZED).build();
        }

        com.example.quizmaster.entity.User creator = new com.example.quizmaster.entity.User();
        creator.setId(jwt.getClaimAsString("sub"));
        // We only accept the ID from the token. Name/Email will be fetched from DB by
        // Service.

        quiz.setCreator(creator);

        try {
            quizService.saveQuiz(quiz);
            return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED).body(quiz);
        } catch (RuntimeException e) {
            // Likely "User not found"
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("")
    public ResponseEntity<List<Quiz>> getQuizzes(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "id") String sort) {

        // Public endpoint only returns public quizzes
        return ResponseEntity.ok(quizService.getAllQuizzes(search, page, limit, sort, true));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Quiz> getQuizById(@PathVariable Long id,
            @org.springframework.security.core.annotation.AuthenticationPrincipal org.springframework.security.oauth2.jwt.Jwt jwt) {
        java.util.Optional<Quiz> quizOpt = quizService.findById(id);
        if (quizOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Quiz quiz = quizOpt.get();

        boolean isCreator = jwt != null && quiz.getCreator() != null
                && quiz.getCreator().getId().equals(jwt.getSubject());

        if (quiz.isPublic() || isCreator) {
            if (!isCreator) {
                // Hide answers if not creator (and not implemented separate logic/DTO yet,
                // assuming FE handles hiding or we clone)
                // For now returning as is, per requirements "If the user is a player, you might
                // only return metadata... not the questions yet"
                // Let's hide questions entirely for players for now as requested
                Quiz metadata = new Quiz(quiz.getTitle(), quiz.getDescription(), quiz.isPublic(), quiz.getCreator());
                metadata.setId(quiz.getId());
                return ResponseEntity.ok(metadata);
            }
            return ResponseEntity.ok(quiz);
        } else {
            return ResponseEntity.status(org.springframework.http.HttpStatus.FORBIDDEN).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Quiz> updateQuiz(@PathVariable Long id, @RequestBody Quiz quizUpdate,
            @org.springframework.security.core.annotation.AuthenticationPrincipal org.springframework.security.oauth2.jwt.Jwt jwt) {
        java.util.Optional<Quiz> quizOpt = quizService.findById(id);
        if (quizOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Quiz quiz = quizOpt.get();

        if (jwt != null && quiz.getCreator() != null && quiz.getCreator().getId().equals(jwt.getSubject())) {
            quiz.setTitle(quizUpdate.getTitle());
            quiz.setDescription(quizUpdate.getDescription());
            quiz.setPublic(quizUpdate.isPublic());
            // Update questions if provided
            if (quizUpdate.getQuestions() != null) {
                quiz.setQuestions(quizUpdate.getQuestions());
            }
            quizService.updateQuiz(quiz); // in-memory, just placeholder
            return ResponseEntity.ok(quiz);
        } else {
            return ResponseEntity.status(org.springframework.http.HttpStatus.FORBIDDEN).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuiz(@PathVariable Long id,
            @org.springframework.security.core.annotation.AuthenticationPrincipal org.springframework.security.oauth2.jwt.Jwt jwt) {
        java.util.Optional<Quiz> quizOpt = quizService.findById(id);
        if (quizOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Quiz quiz = quizOpt.get();

        if (jwt != null && quiz.getCreator() != null && quiz.getCreator().getId().equals(jwt.getSubject())) {
            quizService.deleteQuiz(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(org.springframework.http.HttpStatus.FORBIDDEN).build();
        }
    }
}
