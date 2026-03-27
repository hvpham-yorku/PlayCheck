package com.example.playcheck.puremodel;

public class Team {
    private String teamName;



    public Team(String teamName) {
        this.teamName = teamName;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }


    // TODO: 2026-03-05  work on this function that is used in PlayerLinkToDatabase class 
    public void setTeamId(String teamId) {
        
    }


    // TODO: 2026-03-05 work on this as it used in the player class
    public String getTeamId() {
        String s = "";

        return s;
    }
}
