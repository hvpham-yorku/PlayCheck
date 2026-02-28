package com.example.playcheck.puremodel;

import com.example.playcheck.puremodel.User;


public class Player extends User {
    private Team team;

    public Player(Team team){
        this.team = team;
    }
}
