package com.example.playcheck.puremodel;

import java.util.List;

public class VolleyBall implements SportType{


    final String sportName = "Volleyball";
    final String sportDescription = "";

    static VolleyBall volleyball = new VolleyBall();

    private VolleyBall(){};
    private List<Game> volleyballGames;

    @Override
    public String getSportName() {
        return volleyball.sportName;
    }

    @Override
    public String getSportDescription() {
        return volleyball.sportDescription;
    }

    public List<Game> getVolleyballGames() {
        return volleyballGames;
    }

    public void setVolleyballGames(List<Game> volleyballGames) {
        this.volleyballGames = volleyballGames;
    }

    public void addVolleyballGame(Game game){
        this.volleyballGames.add(game);
    }

    public void removeVolleyballGame(Game game){
        this.volleyballGames.remove(game);
    }
}
