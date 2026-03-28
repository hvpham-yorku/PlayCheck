package com.example.playcheck.puremodel;

import java.util.HashMap;

public class Team {
    private String teamName;
    private String teamId;
    private String teamCreator;
    private HashMap<String, String> Captain;
    private HashMap<String, String> players;
    private int teamWins;
    private int teamLosses;

    public Team(){}

    public Team(String teamId, String teamName, String teamCreator,
                HashMap<String, String> captain,
                HashMap<String, String> players, int teamWins, int teamLosses) {
        this.teamId = teamId;
        this.teamName = teamName;
        this.teamCreator = teamCreator;
        this.Captain = Captain;
        this.players = players;
        this.teamWins = teamWins;
        this.teamLosses = teamLosses;
    }

    //Constructor for tests
    public Team(String teamName) {
        this.teamName = teamName;
    }

    //getters and setters
    public HashMap<String, String> getCaptain() {
        return Captain;
    }

    public void setCaptain(HashMap<String, String> captain) {
        this.Captain = Captain;
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

    public int getTeamLosses() {
        return teamLosses;
    }

    public void setTeamLosses(int teamLosses) {
        this.teamLosses = teamLosses;
    }

    public int getTeamWins() {
        return teamWins;
    }

    public void setTeamWins(int teamWins) {
        this.teamWins = teamWins;
    }


    // TODO: 2026-03-05 work on this as it used in the player class
    public String getTeamId() {
        return teamId;
//        String s = "";
//        return s;
    }
}
