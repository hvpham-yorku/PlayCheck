package com.example.playcheck.activityfiles;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.playcheck.Database.UserLinkToDatabase;
import com.example.playcheck.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class GameDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game_details);

        TextView teamAText = findViewById(R.id.teamA);
        TextView teamBText = findViewById(R.id.teamB);
        TextView teamBNameText = findViewById(R.id.teamBTeamName);
        TextView teamANameText = findViewById(R.id.teamATeamName);
        TextView dateText = findViewById(R.id.dateText);
        TextView locationText = findViewById(R.id.locationText);
        TextView scoreText = findViewById(R.id.scoreText);
        //recycleviews for team players and referees
        RecyclerView rvTeamA = findViewById(R.id.recycleviewTeamAPlayers);
        RecyclerView rvTeamB = findViewById(R.id.recycleViewTeamBPlayers);
        RecyclerView rvRefs = findViewById(R.id.recycleViewReferees);

        TextView refereeText = findViewById(R.id.refereeText);
        
        Button refereeReportButton = findViewById(R.id.refereeReportButton);
        Button btnViewClips = findViewById(R.id.btnViewClips);

        //layout manager for recycleviews
        rvTeamA.setLayoutManager(new LinearLayoutManager(this));
        rvTeamB.setLayoutManager(new LinearLayoutManager(this));
        rvRefs.setLayoutManager(new LinearLayoutManager(this));

        // Hide referee buttons by default
        refereeReportButton.setVisibility(View.GONE);
        btnViewClips.setVisibility(View.GONE);

        // Only show buttons if the user is a Referee
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        UserLinkToDatabase user = new UserLinkToDatabase();
        if (currentUser != null) {
            user.getUserAccountType(currentUser).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String accountType = task.getResult();
                    if ("Referee".equals(accountType)) {
                        refereeReportButton.setVisibility(View.VISIBLE);
                        btnViewClips.setVisibility(View.VISIBLE);
                    }
                }
            });
        }

        btnViewClips.setOnClickListener(v -> {
            Intent intent = new Intent(this, MatchClipListActivity.class);
            intent.putExtra("gameId", getIntent().getStringExtra("gameId"));
            intent.putExtra("gameName", getIntent().getStringExtra("gameName"));
            startActivity(intent);
        });

        refereeReportButton.setOnClickListener(v -> {
            Intent reportIntent = new Intent(this, RefereeReportActivity.class);
            reportIntent.putExtra("gameId", getIntent().getStringExtra("gameId"));
            reportIntent.putExtra("gameName", getIntent().getStringExtra("gameName"));
            startActivity(reportIntent);
        });


        // Get data from Intent
        Intent intent = getIntent();
        String teamA = intent.getStringExtra("teamA");
        String teamB = intent.getStringExtra("teamB");
        String date = intent.getStringExtra("date");
        String location = intent.getStringExtra("location");
        String score = intent.getStringExtra("score");
        ArrayList<String> teamAPlayers = intent.getStringArrayListExtra("teamAPlayers");
        ArrayList<String> teamBPlayers = intent.getStringArrayListExtra("teamBPlayers");
        ArrayList<String> referees = intent.getStringArrayListExtra("referees");

        // Set UI
        teamAText.setText(teamA);
        teamBText.setText(teamB);
        dateText.setText("Date: " + date);
        locationText.setText("Location: " + location);
        scoreText.setText("Score: " + score);
        refereeText.setText("Referees");
        teamANameText.setText(teamA);
        teamBNameText.setText(teamB);


        //using the PlayerAdapter to display players
        rvTeamA.setAdapter(new AddedPlayersAdapter(teamAPlayers, false));
        rvTeamB.setAdapter(new AddedPlayersAdapter(teamBPlayers, false));
        rvRefs.setAdapter(new AddedPlayersAdapter(referees, false));

    }
}
