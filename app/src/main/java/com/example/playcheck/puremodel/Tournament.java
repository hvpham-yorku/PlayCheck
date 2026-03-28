package com.example.playcheck.puremodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Tournament model - represents a multi-game tournament event
 */
public class Tournament {
    private String tournamentId;
public class Tournament extends Event{

    private int tournamentID;
    private String tournamentName;
    private String description;
    private long startDate;
    private long endDate;
    private String organizerId;
    private String organizerName;
    private String status; // "upcoming", "ongoing", "completed"
    private List<String> gameIds; // List of game IDs in this tournament
    private Map<String, Object> bracket; // Bracket structure
    private String tournamentDescription;
    private List<Game> games;
    private List<Team> teams;
    private List<Referee> referees;
    private Organizer organizer;

    private LocalDate startDate;
    private LocalDate endDate;

    private String location;

    private String tournamentFormat;
    private SportType sportType;

    private int numberOfTeams;
    private String tournamentType; // "single_elimination", "double_elimination", "round_robin"

    public Tournament() {
        // Required for Firebase
        this.gameIds = new ArrayList<>();
        this.bracket = new HashMap<>();
        this.status = "upcoming";
    private int numberOfGames;



    Tournament(){

    }

    public int getTournamentID() {
        return tournamentID;
    }

    public String getTournamentName() {

        return tournamentName;
    }

    public List<Game> getGames() {
        return games;
    }

    public List<Team> getTeams() {
        return teams;
    }

    public List<Referee> getReferees() {
        return referees;
    }

    public Organizer getOrganizer() {
        return this.organizer;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public String getLocation() {
        return location;
    }
    public String getTournamentFormat() {
        return tournamentFormat;
    }
    public SportType getSportType() {
        return sportType;
    }
    public int getNumberOfTeams() {
        return numberOfTeams;
    }
    public int getNumberOfGames() {
        return numberOfGames;
    }
    public String getTournamentDescription() {
        return tournamentDescription;
    }

    public Tournament(String tournamentName, String description, long startDate, long endDate,
                      String organizerId, String organizerName, String tournamentType) {

    public void setTournamentID(int tournamentID) {
        this.tournamentID = tournamentID;
    }
    public void setTournamentName(String tournamentName) {
        this.tournamentName = tournamentName;
        this.description = description;
    }
    public void setGames(List<Game> games) {
        this.games = games;
    }
    public void setTeams(List<Team> teams) {
        this.teams = teams;
    }
    public void setReferees(List<Referee> referees) {
        this.referees = referees;
    }
    public void setOrganizers(Organizer organizer) {
        this.organizer = organizer;
    }
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    public void setEndDate(LocalDate endDate) {
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
    public void setLocation(String location) {
        this.location = location;
    }
    public void setTournamentFormat(String tournamentFormat) {
        this.tournamentFormat = tournamentFormat;
    }
    public void setSportType(SportType sportType) {
        this.sportType = sportType;
    }

    public void removeGame(String gameId) {
        if (this.gameIds != null) {
            this.gameIds.remove(gameId);
        }
    public void setNumberOfTeams(int numberOfTeams) {
        this.numberOfTeams = numberOfTeams;
    }
    public void setNumberOfGames(int numberOfGames) {
        this.numberOfGames = numberOfGames;
    }
    public void setTournamentDescription(String tournamentDescription) {
        this.tournamentDescription = tournamentDescription;
    }






}
