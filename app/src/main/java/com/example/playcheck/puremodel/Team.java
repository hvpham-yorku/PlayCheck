package com.example.playcheck.puremodel;

import com.example.playcheck.database.TeamLinkToDatabase;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.HashMap;

/**
 * Model class for Team standings
 * Stored in Firebase under /teams
 */
public class Team {
    private String teamName;
    private String teamId;
    private String captainId;
    private List<String> memberIds;
    private String teamCreator;
    private HashMap<String, String> Captain;
    private HashMap<String, String> players;
    private int teamWins;
    private int teamLosses;
    private int wins;
    private int losses;
    private int rank;
    private String division;

    private static TeamLinkToDatabase databaseService = new TeamLinkToDatabase();

    public Team() {
    }

    public Team(String teamId, String teamName, String teamCreator,
                HashMap<String, String> captain,
                HashMap<String, String> players, int teamWins, int teamLosses) {
        this.teamId = teamId;
        this.teamName = teamName;
        this.teamCreator = teamCreator;
        this.Captain = captain;
        this.players = players;
        this.teamWins = teamWins;
        this.teamLosses = teamLosses;
    }

    //Constructor for tests
    public Team(String teamName) {
        this.teamName = teamName;
    }

    //-------------------------------------------------------------------------------------------
    // Business Logic Methods
    //-------------------------------------------------------------------------------------------

    public CompletableFuture<String> save() {
        return databaseService.createTeam(this);
    }

    public static CompletableFuture<List<Team>> fetchAll() {
        return databaseService.getAllTeams();
    }

    public static CompletableFuture<Team> fetchById(String id) {
        return databaseService.getTeamById(id);
    }

    //-------------------------------------------------------------------------------------------
    // Getters and Setters
    //-------------------------------------------------------------------------------------------

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public String getTeamCreator() {
        return teamCreator;
    }

    public void setTeamCreator(String teamCreator) {
        this.teamCreator = teamCreator;
    }

    public String getCaptainId() {
        return captainId;
    }

    public void setCaptainId(String captainId) {
        this.captainId = captainId;
    }

    public HashMap<String, String> getCaptain() {
        return Captain;
    }

    public void setCaptain(HashMap<String, String> captain) {
        this.Captain = captain;
    }

    public HashMap<String, String> getPlayers() {
        return players;
    }

    public void setPlayers(HashMap<String, String> players) {
        this.players = players;
    }

    public List<String> getMemberIds() {
        return memberIds;
    }

    public void setMemberIds(List<String> memberIds) {
        this.memberIds = memberIds;
    }

    public int getTeamWins() {
        return teamWins;
    }

    public void setTeamWins(int teamWins) {
        this.teamWins = teamWins;
    }

    public int getTeamLosses() {
        return teamLosses;
    }

    public void setTeamLosses(int teamLosses) {
        this.teamLosses = teamLosses;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getLosses() {
        return losses;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }
}
