package com.example.playcheck;

import org.junit.Test;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class GameTest {

    @Test
    public void constructorAndGetters_workCorrectly() {

        List<String> teamAPlayers = Arrays.asList("A1", "A2");
        List<String> teamBPlayers = Arrays.asList("B1", "B2");

        Game game = new Game(
                "TeamA",
                "TeamB",
                "March 10",
                "Arena",
                "2 - 1",
                teamAPlayers,
                teamBPlayers
        );

        assertEquals("TeamA", game.getTeamA());
        assertEquals("TeamB", game.getTeamB());
        assertEquals("March 10", game.getDate());
        assertEquals("Arena", game.getLocation());
        assertEquals("2 - 1", game.getScore());
        assertEquals(teamAPlayers, game.getTeamAPlayers());
        assertEquals(teamBPlayers, game.getTeamBPlayers());
    }

    @Test
    public void emptyPlayerLists_shouldBeHandledCorrectly() {

        Game game = new Game(
                "TeamA",
                "TeamB",
                "Date",
                "Location",
                "0 - 0",
                Collections.emptyList(),
                Collections.emptyList()
        );

        assertTrue(game.getTeamAPlayers().isEmpty());
        assertTrue(game.getTeamBPlayers().isEmpty());
    }

    @Test
    public void largePlayerLists_shouldBeStoredCorrectly() {

        List<String> largeList = Arrays.asList(
                "P1","P2","P3","P4","P5","P6","P7","P8","P9","P10"
        );

        Game game = new Game(
                "TeamA",
                "TeamB",
                "Date",
                "Location",
                "1 - 1",
                largeList,
                largeList
        );

        assertEquals(10, game.getTeamAPlayers().size());
        assertEquals(10, game.getTeamBPlayers().size());
    }

    @Test
    public void nullFields_shouldBeStoredAsNull() {

        Game game = new Game(
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        assertNull(game.getTeamA());
        assertNull(game.getTeamB());
        assertNull(game.getDate());
        assertNull(game.getLocation());
        assertNull(game.getScore());
        assertNull(game.getTeamAPlayers());
        assertNull(game.getTeamBPlayers());
    }

    @Test
    public void differentGames_shouldRemainIndependent() {

        Game game1 = new Game(
                "A",
                "B",
                "Date1",
                "Loc1",
                "1 - 0",
                Arrays.asList("A1"),
                Arrays.asList("B1")
        );

        Game game2 = new Game(
                "C",
                "D",
                "Date2",
                "Loc2",
                "2 - 2",
                Arrays.asList("C1"),
                Arrays.asList("D1")
        );

        assertNotEquals(game1.getTeamA(), game2.getTeamA());
        assertNotEquals(game1.getLocation(), game2.getLocation());
    }
}
