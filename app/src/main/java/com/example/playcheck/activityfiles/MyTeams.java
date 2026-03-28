package com.example.playcheck.activityfiles;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.playcheck.Database.TeamLinkToDatabase;
import com.example.playcheck.R;
import com.example.playcheck.puremodel.Team;

import java.util.ArrayList;

public class MyTeams extends AppCompatActivity {

    RecyclerView allTeamsRecycleView;

    ArrayList<Team> teams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_teams);

        allTeamsRecycleView = findViewById(R.id.MyTeamsPageRecycleView);
        allTeamsRecycleView.setLayoutManager(new LinearLayoutManager(this));

        TeamLinkToDatabase teamDB = new TeamLinkToDatabase();
        teamDB.getAllTeamsForUser(new TeamLinkToDatabase.allTeamsCallback() {
            @Override
            public void onCallback(ArrayList<Team> teams) {
                MyTeamsAdapter adapter = new MyTeamsAdapter(teams);
                allTeamsRecycleView.setAdapter(adapter);
            }
        });



    }

}
