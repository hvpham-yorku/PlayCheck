package com.example.playcheck;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.*;

import com.example.playcheck.puremodel.Game;
import com.example.playcheck.puremodel.Referee;

public class RefereeTest {

    private Referee referee;
    private Game game1;
    private Game game2;

    @Before
    public void setUp() {
        referee = new Referee();
        game1 = new Game();   // assumes default constructor exists
        game2 = new Game();
    }

    // ---------- Availability Tests ----------

    @Test
    public void testAddGameDateAvailability() {
        LocalDate date = LocalDate.of(2026, 3, 10);
        referee.addGameDateAvailability(date);

        assertTrue(referee.getGameDatesAvailability().contains(date));
    }

    @Test
    public void testDeleteGameDate() {
        LocalDate date = LocalDate.of(2026, 3, 10);
        referee.addGameDateAvailability(date);
        referee.deleteGameDate(date);

        assertFalse(referee.getGameDatesAvailability().contains(date));
    }

    @Test
    public void testIsAvailableOnDate_True() {
        LocalDate date = LocalDate.of(2026, 3, 10);
        referee.addGameDateAvailability(date);

        assertTrue(referee.isAvailableOnDate(date));
    }

    @Test
    public void testIsAvailableOnDate_False() {
        LocalDate date = LocalDate.of(2026, 3, 10);

        assertFalse(referee.isAvailableOnDate(date));
    }

    // ---------- Schedule Tests ----------

    @Test
    public void testAddToSchedule() {
        referee.addToSchedule(game1);

        assertTrue(referee.getSchedule().contains(game1));
    }

    @Test
    public void testDeleteGameFromSchedule() {
        referee.addToSchedule(game1);
        referee.deleteGameFromSchedule(game1);

        assertFalse(referee.getSchedule().contains(game1));
    }

    @Test
    public void testMultipleGamesInSchedule() {
        referee.addToSchedule(game1);
        referee.addToSchedule(game2);

        assertEquals(2, referee.getSchedule().size());
    }

    // ---------- Constructor Test ----------

    @Test
    public void testParameterizedConstructorInitializesLists() {
        Referee ref = new Referee(
                "1",
                "John",
                "Doe",
                "Male",
                LocalDate.of(1990, 1, 1)
        );

        assertNotNull(ref.getGameDatesAvailability());
        assertNotNull(ref.getSchedule());
    }
}
