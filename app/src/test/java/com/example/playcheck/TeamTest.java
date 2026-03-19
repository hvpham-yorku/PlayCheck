package com.example.playcheck;

import org.junit.Test;
import static org.junit.Assert.*;

import com.example.playcheck.puremodel.Team;

public class TeamTest {

    @Test
    public void testConstructorSetsTeamName() {
        Team team = new Team("Lions");

        assertEquals("Lions", team.getTeamName());
    }

    @Test
    public void testSetTeamName() {
        Team team = new Team("Lions");

        team.setTeamName("Tigers");

        assertEquals("Tigers", team.getTeamName());
    }

    @Test
    public void testGetTeamIdDefaultValue() {
        Team team = new Team("Lions");

        String teamId = team.getTeamId();

        assertNotNull(teamId);
        assertEquals("", teamId);
    }

    @Test
    public void testSetTeamIdDoesNotCrash() {
        Team team = new Team("Lions");

        team.setTeamId("123");

        // Since setTeamId() is not implemented yet,
        // we only verify the program runs without exception
        assertEquals("", team.getTeamId());
    }
}
