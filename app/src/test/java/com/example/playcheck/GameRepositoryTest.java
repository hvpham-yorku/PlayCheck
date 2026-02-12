package com.example.playcheck;

import org.junit.Test;
import java.util.List;

import static org.junit.Assert.*;

public class GameRepositoryTest {

    @Test
    public void getGames_shouldReturnNonNullList() {
        List<Game> games = GameRepository.getGames();
        assertNotNull(games);
    }

    @Test
    public void getGames_shouldContainStubData() {
        List<Game> games = GameRepository.getGames();
        assertTrue(games.size() >= 1);
    }

    @Test
    public void firstGame_shouldMatchExpectedStubValues() {

        Game game = GameRepository.getGames().get(0);

        assertEquals("Sharks", game.getTeamA());
        assertEquals("Eagles", game.getTeamB());
        assertEquals("Tait Gym", game.getLocation());
    }

    @Test
    public void repository_shouldReturnMultipleGames() {
        List<Game> games = GameRepository.getGames();
        assertTrue(games.size() >= 2);
    }
}
