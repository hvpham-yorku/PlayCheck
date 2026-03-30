package com.example.playcheck;

import static org.junit.Assert.*;
import org.junit.Test;
import com.example.playcheck.puremodel.FootballStats;
import com.example.playcheck.puremodel.BasketballStats;
import com.example.playcheck.puremodel.VolleyballStats;
/*These are unit tests relating to the Stats that are displayed on the Game Details page */

public class SportsStatsUnitTest {
    @Test
    public void footballStatsTest() {
        FootballStats stats = new FootballStats();
        stats.setShootingLeft(10);
        stats.setShootingRight(8);
        stats.setYellowCardsLeft(2);

        assertEquals(10, stats.getShootingLeft());
        assertEquals(8, stats.getShootingRight());
        assertEquals(2, stats.getYellowCardsLeft());
    }

    @Test
    public void basketballStatsTest() {
        BasketballStats stats = new BasketballStats();
        stats.setPointsLeft(102);
        stats.setPointsRight(98);
        stats.setAssistsLeft(25);
        stats.setReboundsLeft(2);
        stats.setReboundsRight(4);

        assertEquals(102, stats.getPointsLeft());
        assertEquals(25, stats.getAssistsLeft());
        assertEquals(2, stats.getReboundsLeft());
        assertEquals(4, stats.getReboundsRight());
    }

    @Test
    public void volleyballStatsTest(){
        VolleyballStats stats = new VolleyballStats();
        stats.setAcesLeft(8);
        stats.setAcesRight(2);
        stats.setKillsLeft(3);
        stats.setKillsRight(5);

        assertEquals(8, stats.getAcesLeft());
        assertEquals(2, stats.getAcesRight());
        assertEquals(3, stats.getKillsLeft());
        assertEquals(5, stats.getKillsRight());

    }
}
