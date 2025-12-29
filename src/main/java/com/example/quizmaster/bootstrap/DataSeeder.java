package com.example.quizmaster.bootstrap;

import com.example.quizmaster.entity.Answer;
import com.example.quizmaster.entity.Question;
import com.example.quizmaster.entity.Quiz;
import com.example.quizmaster.entity.User;
import com.example.quizmaster.repository.QuizRepository;
import com.example.quizmaster.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final QuizRepository quizRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository, QuizRepository quizRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.quizRepository = quizRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() > 0) {
            return;
        }

        // Create Users
        User admin = new User("admin@example.com", passwordEncoder.encode("password"), "Admin", "User");
        User user = new User("user@example.com", passwordEncoder.encode("password"), "Regular", "User");

        userRepository.save(admin);
        userRepository.save(user);

        // Create Public Quiz
        Quiz publicQuiz = new Quiz("General Knowledge", "A random quiz about everything", true, admin);
        List<Question> publicQuestions = new ArrayList<>();

        Question q1 = new Question("What is the capital of France?", publicQuiz);
        List<Answer> a1 = List.of(
                new Answer(q1, true, "Paris"),
                new Answer(q1, false, "London"),
                new Answer(q1, false, "Berlin"),
                new Answer(q1, false, "Madrid"));
        q1.setAnswers(a1);
        publicQuestions.add(q1);

        Question q2 = new Question("What is 2 + 2?", publicQuiz);
        List<Answer> a2 = List.of(
                new Answer(q2, true, "4"),
                new Answer(q2, false, "5"));
        q2.setAnswers(a2);
        publicQuestions.add(q2);

        publicQuiz.setQuestions(publicQuestions);
        quizRepository.save(publicQuiz);

        // Create Private Quiz
        Quiz privateQuiz = new Quiz("Secret Quiz", "For authorized personnel only", false, user);
        List<Question> privateQuestions = new ArrayList<>();

        Question q3 = new Question("What is the secret code?", privateQuiz);
        List<Answer> a3 = List.of(
                new Answer(q3, true, "1234"),
                new Answer(q3, false, "0000"));
        q3.setAnswers(a3);
        privateQuestions.add(q3);

        privateQuiz.setQuestions(privateQuestions);
        quizRepository.save(privateQuiz);

        System.out.println("Data seeding completed!");
    }
}
