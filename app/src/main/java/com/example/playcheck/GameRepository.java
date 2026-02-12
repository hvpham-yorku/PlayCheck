package com.example.playcheck;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GameRepository {

    public static List<Game> getGames() {

        List<Game> games = new ArrayList<>();

        games.add(new Game(
                "Sharks",
                "Eagles",
                "March 2, 6 PM",
                "Tait Gym",
                "3 - 2",
                Arrays.asList("John Smith", "Alex Brown", "Mike Lee"),
                Arrays.asList("David Kim", "Chris Hall", "Ryan Chen")
        ));

        games.add(new Game(
                "Lions",
                "Tigers",
                "March 5, 8 PM",
                "Main Arena",
                "1 - 1",
                Arrays.asList("Tom White", "Sam Green", "Luke Adams"),
                Arrays.asList("Ryan Scott", "Daniel Park", "Kevin Young")
        ));

        return games;
    }
}
