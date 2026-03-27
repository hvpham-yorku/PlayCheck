package com.example.playcheck.puremodel;

import java.util.List;

public class BasketBall implements SportType {
    final String sportName = "Basketball";
    final String sportDescription = "";

    private List<Game> basketballGames;

    static BasketBall basketball = new BasketBall();
    private BasketBall(){};
    @Override
    public String getSportName() {
        return sportName;
    }

    @Override
    public String getSportDescription() {
        return basketball.sportDescription;
    }
    public List<Game> getBasketballGames() {
        return basketball.basketballGames;
    }

    public void setBasketballGames(List<Game> basketballGames) {
        basketball.basketballGames = basketballGames;
    }

    public void addBasketballGame(Game game){
        basketball.basketballGames.add(game);
    }

    public void removeBasketballGame(Game game){
        basketball.basketballGames.remove(game);
    }

}
