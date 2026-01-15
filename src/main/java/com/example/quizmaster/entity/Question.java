package com.example.quizmaster.entity;

@jakarta.persistence.Entity
@jakarta.persistence.Table(name = "questions")
public class Question {

    @jakarta.persistence.Id
    @jakarta.persistence.GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    private String questionTitle;

    @com.fasterxml.jackson.annotation.JsonBackReference
    @jakarta.persistence.ManyToOne
    @jakarta.persistence.JoinColumn(name = "quiz_id")
    private Quiz quiz;

    @com.fasterxml.jackson.annotation.JsonManagedReference
    @jakarta.persistence.OneToMany(mappedBy = "question", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    private java.util.List<Option> answers;

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

    public java.util.List<Option> getAnswers() {
        return answers;
    }

    public void setAnswers(java.util.List<Option> answers) {
        this.answers = answers;
    }
}
