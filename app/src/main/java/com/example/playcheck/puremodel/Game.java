package com.example.playcheck.puremodel;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/*
This class defines the information that each Game has.
 */
public class Game {

    private String teamA;
    private String teamB;
    private long gameDate;
    private String gameVenue;
    private String gameType;
    private String gameName; // Added to match Firebase data if present
    private String gameId;
    private Map<String, String> players;
    private String gameCreator;

    private String date;
    private String location;
    private String score;
    private List<String> teamAPlayers; //used for tests
    private List<String> teamBPlayers; //used for tests
    private String teamAid;
    private String teamBid;
    private Event event;
    private Referee referee;



    public Game(){}

    public Game(String teamA, String teamB, long gameDate, String gameVenue, String gameType, Map<String, String> players, String gameCreator, String teamAid, String teamBid) {
        this.teamA = teamA;
        this.teamB = teamB;
        this.gameDate = gameDate;
        this.gameVenue = gameVenue;
        this.gameType = gameType;
        this.players = players;
        this.gameCreator = gameCreator;
        this.teamAid = teamAid;
        this.teamBid = teamBid;

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

    public String getTeamA() {
        return teamA;
    }
    public String getTeamB() {
        return teamB;
    }
    public long getGameDate() { //return date as a long int
        return gameDate;
    }

    public Map<String, String> getPlayers() {
        return players;
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

    /*returns the date + time as an int*/
    public long getEpochTime(int year, int month, int day, int hour, int minute) {
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTimeInMillis();
    }

    public LocalDate getDateInLocalDate() {

        LocalDate localDateSystemDefault = Instant.ofEpochMilli(this.gameDate)
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        return localDateSystemDefault;

    }

    public String getGameVenue() {
        return gameVenue;
    }

    public String getGameType(){
        return gameType;
    }

    public void setPlayers(Map<String, String> players) {
        this.players = players;
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

    public void setGameCreator(String gameCreator) {
        this.gameCreator = gameCreator;
    }

    public String getTeamAid() {
        return teamAid;
    }

    public void setTeamAid(String teamAid) {
        this.teamAid = teamAid;
    }

    public String getTeamBid() {
        return teamBid;
    }

    public void setTeamBid(String teamBid) {
        this.teamBid = teamBid;
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

    public String getGameCreator() {
        return gameCreator;
    }
}
