package puremodel;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/*
This class defines the information that each Game has.
 */
public class Game {
    private String gameName;
    private long gameDate;
    private String gameVenue;

    private String gameType;

    public Game(){

    }

    public Game(String gameName, long gameDate, String gameVenue, String gameType) {
        this.gameName = gameName;
        this.gameDate = gameDate;
        this.gameVenue = gameVenue;
        this.gameType = gameType;
    }

    public String getGameName() {
        return gameName;
    }

    public long getGameDate() { //return date as a long int
        return gameDate;
    }

    public String getGameDateLongtoString(long gameDate){ //converts game date from long (datetime in milliseconds)-> LocalDateTime -> String
        LocalDateTime dateAsString = LocalDateTime.ofInstant(Instant.ofEpochMilli(gameDate), ZoneId.systemDefault()); //convert to LocalDateTime (EST) by timestamping an instance on the timeline
        DateTimeFormatter gameDateFormat = DateTimeFormatter.ofPattern("h:mm a - MMM dd yyy");
        return dateAsString.format(gameDateFormat);
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
