package com.example.playcheck.puremodel;

import java.util.List;


public class Running implements SportType{

    final String sportName = "Running";
    final String sportDescription = "";

    private List <Game> runningGames;

    private Running(){};

    static Running running = new Running();

    public  String getSportName(){
        return running.sportName;
    };

    public  String getSportDescription(){
        return running.sportDescription;
    };

    public List<Game> getRunningGames() {
        return running.runningGames;
    }

    public void setRunningGames(List<Game> runningGames) {
        running.runningGames = runningGames;
    }
    public void addRunningGame(Game game){
        running.runningGames.add(game);
    }
    public void removeRunningGame(Game game){
        running.runningGames.remove(game);
    }

}
