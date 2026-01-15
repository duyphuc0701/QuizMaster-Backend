package com.example.quizmaster.service;

import com.example.quizmaster.dto.QuizDto;
import com.example.quizmaster.entity.Option;
import com.example.quizmaster.entity.Question;
import com.example.quizmaster.entity.Quiz;
import com.example.quizmaster.entity.User;
import com.example.quizmaster.repository.QuizRepository;
import com.example.quizmaster.repository.UserRepository;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class QuizService {

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private UserRepository userRepository;

    public Quiz createQuiz(QuizDto.CreateRequest request, String userId) {
        // 1. Fetch the Creator (User)
        // We use the ID from Keycloak (JWT) to find our local User entity
        User creator = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found. Ensure sync is working."));

        // 2. Map Quiz Basic Info
        Quiz quiz = new Quiz();
        quiz.setTitle(request.getTitle());
        quiz.setDescription(request.getDescription());
        quiz.setPublic(request.isPublic());
        quiz.setTimeLimitMinutes(request.getTimeLimitMinutes());
        quiz.setCreator(creator); // Link the creator

        // 3. Map Questions (and maintain Order)
        if (request.getQuestions() != null) {
            int questionOrder = 0;

            for (QuizDto.QuestionRequest qDto : request.getQuestions()) {
                Question question = new Question();
                question.setText(qDto.getText());
                question.setPoints(qDto.getPoints());
                question.setType(qDto.getType());
                question.setOrderIndex(questionOrder++); // Auto-increment order

                // IMPORTANT: Link Parent to Child
                quiz.addQuestion(question);

                // 4. Map Options
                if (qDto.getOptions() != null) {
                    int optionOrder = 0;
                    for (QuizDto.OptionRequest oDto : qDto.getOptions()) {
                        Option option = new Option();
                        option.setText(oDto.getText());
                        option.setCorrect(oDto.isCorrect());
                        option.setOrderIndex(optionOrder++);

                        // IMPORTANT: Link Parent to Child
                        question.addOption(option);
                    }
                }
            }
        }

        // 5. Save Everything
        // Because of CascadeType.ALL, this saves Quiz, Questions, and Options
        return quizRepository.save(quiz);
    }

}
