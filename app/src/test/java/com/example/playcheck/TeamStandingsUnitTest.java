package com.example.playcheck;

import static org.junit.Assert.assertEquals;
import com.example.playcheck.puremodel.Team;
import org.junit.Test;
import java.util.ArrayList;
import java.util.Collections;

/*This tests the logic used to sort through teams for team standings */
public class TeamStandingsUnitTest {
    @Test
    public void testTeamStandingsLogic() {
        ArrayList<Team> teams = new ArrayList<>();

        Team team1 = new Team("Team A"); //this team is last
        team1.setTeamWins(5);
        team1.setTeamLosses(2);

        Team team2 = new Team("Team B");
        team2.setTeamWins(8); // This team should have a higher ranking so is first
        team2.setTeamLosses(1);

        Team team3 = new Team("Team C");
        team3.setTeamWins(5);
        team3.setTeamLosses(1); // should be the 2nd in the list

        teams.add(team1);
        teams.add(team2);
        teams.add(team3);

        Collections.sort(teams, (t1, t2) -> {
            if (t2.getTeamWins() != t1.getTeamWins()) {
                return t2.getTeamWins() - t1.getTeamWins();
            }
            return t1.getTeamLosses() - t2.getTeamLosses();
        });

        assertEquals("Team B", teams.get(0).getTeamName());
        assertEquals("Team C", teams.get(1).getTeamName());
        assertEquals("Team A", teams.get(2).getTeamName());
    }
}
