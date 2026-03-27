package com.example.playcheck.puremodel;

import java.util.HashMap;

public class Team {
    private String teamName;
    private String teamId;
    private String teamCreator;
    private HashMap<String, String> captain;
    private HashMap<String, String> players;

    public Team(){}

    public Team(String teamId, String teamName, String teamCreator,
                HashMap<String, String> captain,
                HashMap<String, String> players) {
        this.teamId = teamId;
        this.teamName = teamName;
        this.teamCreator = teamCreator;
        this.captain = captain;
        this.players = players;
    }

    //Constructor for tests
    public Team(String teamName) {
        this.teamName = teamName;
    }

    //getters and setters
    public HashMap<String, String> getCaptain() {
        return captain;
    }

    public void setCaptain(HashMap<String, String> captain) {
        this.captain = captain;
    }

    public String getTeamCreator() {
        return teamCreator;
    }

    public void setTeamCreator(String teamCreator) {
        this.teamCreator = teamCreator;
    }

    public HashMap<String, String> getPlayers() {
        return players;
    }

    public void setPlayers(HashMap<String, String> players) {
        this.players = players;
    }
    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    // TODO: 2026-03-05  work on this function that is used in PlayerLinkToDatabase class 
    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    // TODO: 2026-03-05 work on this as it used in the player class
    public String getTeamId() {
        return teamId;
//        String s = "";
//        return s;
    }
}
