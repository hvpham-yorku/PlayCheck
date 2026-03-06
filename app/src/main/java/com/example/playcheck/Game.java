package com.example.playcheck;

import java.util.List;

public class Game {

    private String teamA;
    private String teamB;
    private String date;
    private String location;
    private String score;
    private List<String> teamAPlayers;
    private List<String> teamBPlayers;

    public Game() {

    }

    public Game(String teamA,
                String teamB,
                String date,
                String location,
                String score,
                List<String> teamAPlayers,
                List<String> teamBPlayers) {

        this.teamA = teamA;
        this.teamB = teamB;
        this.date = date;
        this.location = location;
        this.score = score;
        this.teamAPlayers = teamAPlayers;
        this.teamBPlayers = teamBPlayers;
    }

    public String getTeamA() {
        return teamA;
    }

    public String getTeamB() {
        return teamB;
    }

    public String getDate() {
        return date;
    }

    public String getLocation() {
        return location;
    }

    public String getScore() {
        return score;
    }

    public List<String> getTeamAPlayers() {
        return teamAPlayers;
    }

    public List<String> getTeamBPlayers() {
        return teamBPlayers;
    }
}
