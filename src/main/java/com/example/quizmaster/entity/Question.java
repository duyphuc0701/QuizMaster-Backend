package com.example.quizmaster.entity;

import java.util.List;

public class Question {

    private Long id;
    private String questionTitle;
    private Quiz quiz;
    private List<Answer> answers;

    public Question() {
    }

    public Question(String questionTitle, Quiz quiz) {
        this.questionTitle = questionTitle;
        this.quiz = quiz;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuestionTitle() {
        return questionTitle;
    }

    public void setQuestionTitle(String questionTitle) {
        this.questionTitle = questionTitle;
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }
}
