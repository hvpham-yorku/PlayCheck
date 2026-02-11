package com.example.playcheck;

public class GameListPlayerInfo {
    private String gameName;
    private String gameDate;
    private String gameVenue;

    public GameListPlayerInfo(){

    }

    public GameListPlayerInfo(String gameName, String gameDate, String gameVenue) {
        this.gameName = gameName;
        this.gameDate = gameDate;
        this.gameVenue = gameVenue;
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

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public void setGameDate(String gameDate) {
        this.gameDate = gameDate;
    }

    public void setGameVenue(String gameVenue) {
        this.gameVenue = gameVenue;
    }
}
