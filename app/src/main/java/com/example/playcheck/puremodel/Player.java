package com.example.playcheck.puremodel;

import com.example.playcheck.puremodel.User;


public class Player extends User {


    private Team team;

    public Player(Team team){

        this.team = team;
    }

    public Player(String firstName,String lastName,String email,String dob, String gender){
        super(firstName, lastName, email, dob, gender);

    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }
}
