package com.example.quizmaster.entity;

@jakarta.persistence.Entity
@jakarta.persistence.Table(name = "options")
public class Option {

    @jakarta.persistence.Id
    @jakarta.persistence.GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @com.fasterxml.jackson.annotation.JsonBackReference
    @jakarta.persistence.ManyToOne
    @jakarta.persistence.JoinColumn(name = "question_id")
    private Question question;

    @com.fasterxml.jackson.annotation.JsonProperty("correct")
    private boolean isCorrect;

    private String text;

    public Option() {
    }

    public Option(Question question, boolean isCorrect, String text) {
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

    @com.fasterxml.jackson.annotation.JsonProperty("correct")
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
