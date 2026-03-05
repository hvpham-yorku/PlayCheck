package com.example.playcheck.puremodel;


import java.time.LocalDate;

public abstract class User {

    private String firstName;
    private String lastName;
    private String gender;
    private LocalDate dateOfBirth;
    private String email;

    private String password;
    private String uid;


    public User() {

    }

    User(String email, String password){
        this.email = email;
        this.password = password;
        this.uid = null;
    }


    User(String firstName,String lastName, String email, String dob, String gender){
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        setDateOfBirth(LocalDate.parse(dob));
        this.gender = gender;
    }


    public String getUid() {
        return uid;
    }

    public void setUid(String uid){

        this.uid = uid;
    }

    public String getEmail() {
        return  email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public String getName() {
        return this.firstName + " " + this.lastName;
    }

    public void setName(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public String getDOBasString(){

        return this.dateOfBirth.toString();
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
