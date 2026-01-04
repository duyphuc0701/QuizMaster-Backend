package com.example.quizmaster.entity;

import java.util.List;

public class Quiz {

    private Long id;
    private String title;
    private String description;
    private boolean isPublic;
    private User creator;
    private List<Question> questions;

    public Quiz() {
    }

    public Quiz(String title, String description, boolean isPublic, User creator) {
        this.title = title;
        this.description = description;
        this.isPublic = isPublic;
        this.creator = creator;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }
}
