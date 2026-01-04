package com.example.quizmaster.bootstrap;

import com.example.quizmaster.entity.Answer;
import com.example.quizmaster.entity.Question;
import com.example.quizmaster.entity.Quiz;
import com.example.quizmaster.entity.User;
import com.example.quizmaster.service.QuizService;
import com.example.quizmaster.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserService userService;
    private final QuizService quizService;

    public DataSeeder(UserService userService, QuizService quizService) {
        this.userService = userService;
        this.quizService = quizService;
    }

    @Override
    public void run(String... args) throws Exception {
        // Create Users
        User admin = new User("admin@example.com", "password", "Admin", "User");
        User user = new User("user@example.com", "password", "Regular", "User");

        userService.saveUser(admin);
        userService.saveUser(user);

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
        quizService.saveQuiz(publicQuiz);

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
        quizService.saveQuiz(privateQuiz);

        System.out.println("Data seeding completed!");
    }
}
