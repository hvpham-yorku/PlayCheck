package com.example.playcheck.puremodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Tournament model - represents a multi-game tournament event
 */
public class Tournament {
    private String tournamentId;
    private String tournamentName;
    private String description;
    private long startDate;
    private long endDate;
    private String organizerId;
    private String organizerName;
    private String status; // "upcoming", "ongoing", "completed"
    private List<String> gameIds; // List of game IDs in this tournament
    private Map<String, Object> bracket; // Bracket structure
    private int numberOfTeams;
    private String tournamentType; // "single_elimination", "double_elimination", "round_robin"
    
    public Tournament() {
        // Required for Firebase
        this.gameIds = new ArrayList<>();
        this.bracket = new HashMap<>();
        this.status = "upcoming";
    }
    
    public Tournament(String tournamentName, String description, long startDate, long endDate, 
                      String organizerId, String organizerName, String tournamentType) {
        this.tournamentName = tournamentName;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.organizerId = organizerId;
        this.organizerName = organizerName;
        this.tournamentType = tournamentType;
        this.status = "upcoming";
        this.gameIds = new ArrayList<>();
        this.bracket = new HashMap<>();
    }
    
    // Getters
    public String getTournamentId() { return tournamentId; }
    public String getTournamentName() { return tournamentName; }
    public String getDescription() { return description; }
    public long getStartDate() { return startDate; }
    public long getEndDate() { return endDate; }
    public String getOrganizerId() { return organizerId; }
    public String getOrganizerName() { return organizerName; }
    public String getStatus() { return status; }
    public List<String> getGameIds() { return gameIds; }
    public Map<String, Object> getBracket() { return bracket; }
    public int getNumberOfTeams() { return numberOfTeams; }
    public String getTournamentType() { return tournamentType; }
    
    // Setters
    public void setTournamentId(String tournamentId) { this.tournamentId = tournamentId; }
    public void setTournamentName(String tournamentName) { this.tournamentName = tournamentName; }
    public void setDescription(String description) { this.description = description; }
    public void setStartDate(long startDate) { this.startDate = startDate; }
    public void setEndDate(long endDate) { this.endDate = endDate; }
    public void setOrganizerId(String organizerId) { this.organizerId = organizerId; }
    public void setOrganizerName(String organizerName) { this.organizerName = organizerName; }
    public void setStatus(String status) { this.status = status; }
    public void setGameIds(List<String> gameIds) { this.gameIds = gameIds; }
    public void setBracket(Map<String, Object> bracket) { this.bracket = bracket; }
    public void setNumberOfTeams(int numberOfTeams) { this.numberOfTeams = numberOfTeams; }
    public void setTournamentType(String tournamentType) { this.tournamentType = tournamentType; }
    
    // Helper methods
    public void addGame(String gameId) {
        if (this.gameIds == null) {
            this.gameIds = new ArrayList<>();
        }
        if (!this.gameIds.contains(gameId)) {
            this.gameIds.add(gameId);
        }
    }
    
    public void removeGame(String gameId) {
        if (this.gameIds != null) {
            this.gameIds.remove(gameId);
        }
    }
}
