package com.example.playcheck.puremodel;

import com.example.playcheck.database.TeamLinkToDatabase;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Team {
    private String teamName;
    private String teamId;
    private String captainId;
    private List<String> memberIds;

    private static TeamLinkToDatabase databaseService = new TeamLinkToDatabase();

    public Team() {}

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

    public String getCaptainId() {
        return captainId;
    }

    public void setCaptainId(String captainId) {
        this.captainId = captainId;
    }

    public List<String> getMemberIds() {
        return memberIds;
    }

    public void setMemberIds(List<String> memberIds) {
        this.memberIds = memberIds;
    }
}
