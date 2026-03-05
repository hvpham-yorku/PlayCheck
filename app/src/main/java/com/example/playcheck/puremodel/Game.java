package com.example.playcheck.puremodel;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/*
This class defines the information that each Game has.
 */
public class Game {
    private String teamA;
    private String teamB;
    private long gameDate;
    private String gameVenue;

    private String gameType;

    public Game(){}




    public Game(String teamA, String teamB, long gameDate, String gameVenue, String gameType) {
        this.teamA = teamA;
        this.teamB = teamB;
        this.gameDate = gameDate;
        this.gameVenue = gameVenue;
        this.gameType = gameType;
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
}
