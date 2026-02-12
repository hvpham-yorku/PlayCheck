package com.example.playcheck;
/*
This class defines the information that each Game has.
 */
public class Games {
    private String gameName;
    private String gameDate;
    private String gameVenue;

    private String gameType;

    public Games(){

    }

    public Games(String gameName, String gameDate, String gameVenue, String gameType) {
        this.gameName = gameName;
        this.gameDate = gameDate;
        this.gameVenue = gameVenue;
        this.gameType = gameType;
    }

    public String getGameName() {
        return gameName;
    }

    public String getGameDate() {
        return gameDate;
    }

    public String getGameVenue() {
        return gameVenue;
    }

    public String getGameType(){
        return gameType;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public void setGameDate(String gameDate) {
        this.gameDate = gameDate;
    }

    public void setGameVenue(String gameVenue) {
        this.gameVenue = gameVenue;
    }

    public void setGameType(String gameType){
        this.gameType = gameType;
    }
}
