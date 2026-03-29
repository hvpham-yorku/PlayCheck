package com.example.playcheck.activityfiles;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.playcheck.Database.TeamLinkToDatabase;
import com.example.playcheck.R;
import com.example.playcheck.puremodel.Team;
import java.util.ArrayList;
import java.util.Collections;

public class TeamStandings extends AppCompatActivity {
    private RecyclerView recyclerViewTeamStandings;
    private AdapterTeamStandings adapter;
    private TeamLinkToDatabase teamDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_standings);

        recyclerViewTeamStandings = findViewById(R.id.recycleViewTeamStandings);
        recyclerViewTeamStandings.setLayoutManager(new LinearLayoutManager(this));

        teamDB = new TeamLinkToDatabase();
        loadTeamStandings();
    }

    /* Method sorts teams from the most wins to the least losses */
    private void loadTeamStandings(){
        teamDB.getAllTeamsForStandings(teams -> {

            Collections.sort(teams, (t1, t2) -> {
                if (t2.getTeamWins() != t1.getTeamWins()) {
                    return t2.getTeamWins() - t1.getTeamWins(); // it this comes out as negative, t1 comes before t2. Opposite if its positive.
                }
                return t1.getTeamLosses() - t2.getTeamLosses(); // If wins equal, lower losses come first
            });

            adapter = new AdapterTeamStandings(teams);
            recyclerViewTeamStandings.setAdapter(adapter);
        });
    }

}
