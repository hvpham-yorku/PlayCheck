package com.example.playcheck.puremodel;


import java.time.LocalDate;
import java.time.LocalDateTime;

public abstract class User {

    private String userId;
    private String firstName;
    private String lastName;
    private String gender;
    private LocalDate dateOfBirth;

    public String getName() {
        return this.firstName + " " + this.lastName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setName(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

}
