package com.example.playcheck.puremodel;

import java.util.List;

public class Football implements SportType{
    final String sportName = "Football";
    final String sportDescription = "";

    private Football(){};

    static Football football = new Football();

    private List<Game> footballGames;

    @Override
    public String getSportName() {
        return football.sportName;
    }

    @Override
    public String getSportDescription() {
        return football.sportDescription;
    }

    public static List<Game> getFootballGames() {
        return football.footballGames;
    };
    public static void setFootballGames(List<Game> footballGames) {

        football.footballGames = footballGames;
    }
    public static void addFootballGame(Game game){
        football.footballGames.add(game);
    }
    public static void removeFootballGame(Game game){
        football.footballGames.remove(game);
    }

}
