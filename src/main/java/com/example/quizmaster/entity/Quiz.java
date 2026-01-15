package com.example.quizmaster.entity;

@jakarta.persistence.Entity
@jakarta.persistence.Table(name = "quizzes")
public class Quiz {

    @jakarta.persistence.Id
    @jakarta.persistence.GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;

    @com.fasterxml.jackson.annotation.JsonProperty("public")
    private boolean isPublic;

    @jakarta.persistence.ManyToOne
    @jakarta.persistence.JoinColumn(name = "creator_id")
    private User creator;

    @com.fasterxml.jackson.annotation.JsonManagedReference
    @jakarta.persistence.OneToMany(mappedBy = "quiz", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    private java.util.List<Question> questions;

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

    public java.util.List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(java.util.List<Question> questions) {
        this.questions = questions;
    }
}
