package com.example.quizmaster.entity;

@jakarta.persistence.Entity
@jakarta.persistence.Table(name = "users")
public class User {

    @jakarta.persistence.Id
    private String id;

    private String email;

    @com.fasterxml.jackson.annotation.JsonProperty("firstName")
    private String firstName;

    @com.fasterxml.jackson.annotation.JsonProperty("lastName")
    private String lastName;

    public User() {
    }

    public User(String email, String password, String firstName, String lastName) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
