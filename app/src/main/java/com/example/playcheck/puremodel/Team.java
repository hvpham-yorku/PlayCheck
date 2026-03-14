package com.example.playcheck.puremodel;

/**
 * Model class for Team standings
 * Stored in Firebase under /teams
 */
public class Team {
    private String teamId;
    private String teamName;
    private int wins;
    private int losses;
    private int rank;
    private String division;

    public Team() {
        // Required empty constructor for Firebase
    }

    public Team(String teamName, int wins, int losses) {
        this.teamName = teamName;
        this.wins = wins;
        this.losses = losses;
    }

    // Getters
    public String getTeamId() {
        return teamId;
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

    // Setters
    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
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

    public void setDivision(String division) {
        this.division = division;
    }
}