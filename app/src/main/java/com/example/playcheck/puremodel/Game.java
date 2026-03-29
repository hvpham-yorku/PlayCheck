package com.example.playcheck.puremodel;

import com.example.playcheck.database.GameLinkToDatabase;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/*
This class defines the information that each Game has.
 */
public class Game {

    private String teamA;
    private String teamB;
    private long gameDate;
    private String gameVenue;
    private String gameType;
    private String gameName; 
    private String gameId;

    private String date;
    private String location;
    private String score;
    private List<String> teamAPlayers;
    private List<String> teamBPlayers;

    private Event event;
    private Referee referee;
    
    private MatchReport matchReport;

    private static GameLinkToDatabase databaseService = new GameLinkToDatabase();

    public Game(){}

    public Game(String teamA, String teamB, long gameDate, String gameVenue, String gameType) {
        this.teamA = teamA;
        this.teamB = teamB;
        this.gameDate = gameDate;
        this.gameVenue = gameVenue;
        this.gameType = gameType;
        this.gameId = "";
        this.event = null;
        this.referee = null;
    }

    public Game(String teamA, String teamB, String date, String location, String score, List<String> teamAPlayers, List<String> teamBPlayers) {
        this.teamA = teamA;
        this.teamB = teamB;
        this.date = date;
        this.location = location;
        this.score = score;
        this.teamAPlayers = teamAPlayers;
        this.teamBPlayers = teamBPlayers;
    }

    //-------------------------------------------------------------------------------------------
    // Business Logic Methods
    //-------------------------------------------------------------------------------------------

    public CompletableFuture<Void> save() {
        return databaseService.saveGame(this);
    }

    public static CompletableFuture<List<Game>> fetchAll() {
        return databaseService.getAllGames();
    }

    public static CompletableFuture<Game> fetchById(String id) {
        return databaseService.getGameById(id);
    }

    //-------------------------------------------------------------------------------------------
    // Getters and Setters
    //-------------------------------------------------------------------------------------------

    public String getTeamA() {
        return teamA;
    }
    public String getTeamB() {
        return teamB;
    }
    public long getGameDate() { 
        return gameDate;
    }

    public String getGameDateLongtoString(long gameDate){
        try {
            LocalDateTime dateAsString = LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(gameDate),
                    ZoneId.systemDefault());

            DateTimeFormatter format =
                    DateTimeFormatter.ofPattern("h:mm a - MMM dd yyyy");

            return dateAsString.format(format);
        } catch (Exception e) {
            return "Invalid Date";
        }
    }
    public LocalDate getDateInLocalDate() {
        return Instant.ofEpochMilli(this.gameDate)
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    public String getGameVenue() {
        return gameVenue;
    }

    public String getGameType(){
        return gameType;
    }

    public void setTeamA(String teamA) {
        this.teamA = teamA;
    }
    public void setTeamB(String teamB) {
        this.teamB = teamB;
    }
    public void setGameDate(long gameDate) {
        this.gameDate = gameDate;
    }

    public void setGameVenue(String gameVenue) {
        this.gameVenue = gameVenue;
    }

    public void setGameType(String gameType){
        this.gameType = gameType;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public String getGameId() {
        return gameId != null ? gameId : "";
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getEventId() {
        return this.event != null ? this.event.getEventId() : null;
    }

    public void setEventId(String eventId) {
        if (this.event != null) {
            this.event.setEventId(eventId);
        }
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Referee getReferee() {
        return referee;
    }

    public void setReferee(Referee referee) {
        this.referee = referee;
    }

    public String getRefereeId() {
        return this.referee != null ? this.referee.getRefereeId() : null;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public List<String> getTeamAPlayers() {
        return teamAPlayers;
    }

    public void setTeamAPlayers(List<String> teamAPlayers) {
        this.teamAPlayers = teamAPlayers;
    }

    public List<String> getTeamBPlayers() {
        return teamBPlayers;
    }

    public void setTeamBPlayers(List<String> teamBPlayers) {
        this.teamBPlayers = teamBPlayers;
    }

    public MatchReport getMatchReport() {
        return matchReport;
    }

    public void setMatchReport(MatchReport matchReport) {
        this.matchReport = matchReport;
    }
}
