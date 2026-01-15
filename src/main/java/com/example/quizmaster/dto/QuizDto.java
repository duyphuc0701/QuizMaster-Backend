package com.example.quizmaster.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class QuizDto {

    // Main Quiz Request
    public static class CreateRequest {
        @NotBlank(message = "Title is required")
        private String title;

        private String description;
        private boolean isPublic;
        private Integer timeLimitMinutes;

        @NotEmpty(message = "Quiz must have at least one question")
        private List<QuestionRequest> questions;

        // Getters & Setters
        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public boolean isPublic() {
            return isPublic;
        }

        public void setPublic(boolean aPublic) {
            isPublic = aPublic;
        }

        public Integer getTimeLimitMinutes() {
            return timeLimitMinutes;
        }

        public void setTimeLimitMinutes(Integer timeLimitMinutes) {
            this.timeLimitMinutes = timeLimitMinutes;
        }

        public List<QuestionRequest> getQuestions() {
            return questions;
        }

        public void setQuestions(List<QuestionRequest> questions) {
            this.questions = questions;
        }
    }

    // Question Part
    public static class QuestionRequest {
        @NotBlank(message = "Question text is required")
        private String text;

        @NotNull
        private Integer points;

        private String type; // e.g., "SINGLE_CHOICE"

        @NotEmpty(message = "Question must have options")
        private List<OptionRequest> options;

        // Getters & Setters
        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public Integer getPoints() {
            return points;
        }

        public void setPoints(Integer points) {
            this.points = points;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public List<OptionRequest> getOptions() {
            return options;
        }

        public void setOptions(List<OptionRequest> options) {
            this.options = options;
        }
    }

    // Option Part
    public static class OptionRequest {
        @NotBlank(message = "Option text is required")
        private String text;

        private boolean isCorrect;

        // Getters & Setters
        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public boolean isCorrect() {
            return isCorrect;
        }

        public void setCorrect(boolean correct) {
            isCorrect = correct;
        }
    }
}