package com.example.quizmaster.entity;

public class Answer {

    private Long id;
    private Question question;
    private boolean isCorrect;
    private String text;

    public Answer() {
    }

    public Answer(Question question, boolean isCorrect, String text) {
        this.question = question;
        this.isCorrect = isCorrect;
        this.text = text;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean isCorrect) {
        this.isCorrect = isCorrect;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
