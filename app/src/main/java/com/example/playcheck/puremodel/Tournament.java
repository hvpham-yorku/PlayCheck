package com.example.playcheck.puremodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Tournament model - represents a multi-game tournament event
 */
public class Tournament extends Event{
    // Unique Identifiers and Basic Info
    private String tournamentId;
    private String tournamentName;
    private String tournamentDescription;
    private String status; // "upcoming", "ongoing", "completed"
    private String location;

    // Date handling
    private long startDate;
    private long endDate;

    // Organization & Participants
    private String organizerId;
    private String organizerName;
    private List<String> gameIds = new ArrayList<>();
    private List<Team> teams = new ArrayList<>();
    private List<Referee> referees = new ArrayList<>();

    // Format and Structure
    private String tournamentFormat;
    private String tournamentType; // "single_elimination", "round_robin"
    private int numberOfTeams;
    private Map<String, Object> bracket = new HashMap<>();

    public Tournament() {
        super();
        this.gameIds = new ArrayList<>();
        this.teams = new ArrayList<>();
        this.referees = new ArrayList<>();
        this.bracket = new HashMap<>();
        this.status = "upcoming";
    }

    public Tournament(String name, String description, long startDate, long endDate, String organizerId, String organizerName, String tournamentType) {
        this.tournamentName = name;
        this.tournamentDescription = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.organizerId = organizerId;
        this.organizerName = organizerName;
        this.tournamentType = tournamentType;

    }

    public String getTournamentId() { return tournamentId; }
        public void setTournamentId(String tournamentId) { this.tournamentId = tournamentId; }

        public String getTournamentName() { return tournamentName; }
        public void setTournamentName(String tournamentName) { this.tournamentName = tournamentName; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public List<String> getGameIds() { return gameIds; }
        public void setGameIds(List<String> gameIds) { this.gameIds = gameIds; }

        public String getTournamentType() { return tournamentType; }
        public void setTournamentType(String tournamentType) { this.tournamentType = tournamentType; }

        public Map<String, Object> getBracket() { return bracket; }
        public void setBracket(Map<String, Object> bracket) { this.bracket = bracket; }

        public String getTournamentDescription() { return tournamentDescription; }
        public void setTournamentDescription(String tournamentDescription) { this.tournamentDescription = tournamentDescription; }

        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }

        // Helper method to add games without overwriting the whole list
        public void addGameId(String gameId) {
            if (this.gameIds == null) this.gameIds = new ArrayList<>();
            if (!this.gameIds.contains(gameId)) {
                this.gameIds.add(gameId);
            }
        }


    public void setNumberOfTeams(int numTeams) {
        numberOfTeams = numTeams;
    }
}
