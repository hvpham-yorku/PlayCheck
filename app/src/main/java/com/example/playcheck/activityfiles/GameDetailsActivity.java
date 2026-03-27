package com.example.playcheck.activityfiles;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.playcheck.Database.UserLinkToDatabase;
import com.example.playcheck.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class GameDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game_details);

        TextView teamAText = findViewById(R.id.teamA);
        TextView teamBText = findViewById(R.id.teamB);
        TextView dateText = findViewById(R.id.dateText);
        TextView locationText = findViewById(R.id.locationText);
        TextView scoreText = findViewById(R.id.scoreText);
        TextView teamAPlayersText = findViewById(R.id.teamAPlayersText);
        TextView teamBPlayersText = findViewById(R.id.teamBPlayersText);
        TextView refereeText = findViewById(R.id.refereeText);
        
        Button refereeReportButton = findViewById(R.id.refereeReportButton);
        Button btnViewClips = findViewById(R.id.btnViewClips);

        // Hide referee buttons by default
        refereeReportButton.setVisibility(View.GONE);
        btnViewClips.setVisibility(View.GONE);

        // Only show buttons if the user is a Referee
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            UserLinkToDatabase.getUserAccountType(currentUser).addOnCompleteListener(task -> {
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
        String teamAPlayers = intent.getStringExtra("teamAPlayers");
        String teamBPlayers = intent.getStringExtra("teamBPlayers");
        String referee = intent.getStringExtra("referee");

        // Set UI
        teamAText.setText(teamA);
        teamBText.setText(teamB);
        dateText.setText("Date: " + date);
        locationText.setText("Location: " + location);
        scoreText.setText("Score: " + score);
        teamAPlayersText.setText(teamAPlayers);
        teamBPlayersText.setText(teamBPlayers);
        refereeText.setText("Referee: " + referee);
    }
}
