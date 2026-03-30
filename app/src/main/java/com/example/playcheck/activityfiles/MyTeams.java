package com.example.playcheck.activityfiles;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.playcheck.database.TeamLinkToDatabase;
import com.example.playcheck.R;
import com.example.playcheck.puremodel.Team;

import java.util.ArrayList;

public class MyTeams extends AppCompatActivity {

    RecyclerView allTeamsRecycleView;

    ArrayList<Team> teams = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_teams);

        allTeamsRecycleView = findViewById(R.id.MyTeamsPageRecycleView);
        allTeamsRecycleView.setLayoutManager(new LinearLayoutManager(this));
        MyTeamsAdapter adapter = new MyTeamsAdapter(teams);
        allTeamsRecycleView.setAdapter(adapter);

        TeamLinkToDatabase teamDB = new TeamLinkToDatabase();
        teamDB.getAllTeamsForUser(new TeamLinkToDatabase.allTeamsCallback() {
            @Override
            public void onCallback(ArrayList<Team> teamsList) {
                if (teamsList != null && !teamsList.isEmpty()) {
                    teams.clear();
                    teams.addAll(teamsList); // Add the data from DB to local list
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(MyTeams.this, "No teams found", Toast.LENGTH_SHORT).show();
                }
            }
        });



    }

}
