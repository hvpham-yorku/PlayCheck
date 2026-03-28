package com.example.playcheck.puremodel;

import java.util.HashMap;

/**
 * Model class for Team standings
 * Stored in Firebase under /teams
 */
public class Team {
    private String teamName;
    private String teamId;
    private String teamCreator;
    private HashMap<String, String> Captain;
    private HashMap<String, String> players;
    private int teamWins;
    private int teamLosses;

    public Team(){}
    private int wins;
    private int losses;
    private int rank;
    private String division;

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
        this.wins = wins; //tony
        this.losses = losses; //tony
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

    public int getWins() {
        return wins;
    }

    public int getLosses() {
        return losses;
    }

    public int getRank() {
        return rank;
    }

    public String getDivision() {
        return division;
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
    public void setWins(int wins) {
        this.wins = wins;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }


    // TODO: 2026-03-05 work on this as it used in the player class
    public String getTeamId() {
        return teamId;
//        String s = "";
//        return s;
        }
    public void setDivision(String division) {
        this.division = division;
    }

}