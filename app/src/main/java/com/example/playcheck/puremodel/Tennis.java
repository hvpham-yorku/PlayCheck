package com.example.playcheck.puremodel;

import java.util.List;

public class Tennis implements SportType{
    final  String sportName = "Tennis";
    final  String sportDescription = "";

    private Tennis(){};

    static Tennis tennis = new Tennis();
    private List<Game> tennisGames;


    @Override
    public String getSportName() {
        return tennis.sportName;
    }
    @Override
    public String getSportDescription() {
        return tennis.sportDescription;
    }
    public static void addTennisGame(Game game){
        tennis.tennisGames.add(game);
    }
    public static void removeTennisGame(Game game){
        tennis.tennisGames.remove(game);
    }
    public static List<Game> getTennisGames() {
        return tennis.tennisGames;
    }
    public static void setTennisGames(List<Game> tennisGames) {
        tennis.tennisGames = tennisGames;
    }
}
