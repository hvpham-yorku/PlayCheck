package com.example.playcheck;
import android.widget.TextView;
import android.content.Intent;



import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class GameDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game_details);

        TextView teamText = findViewById(R.id.teamText);
        TextView dateText = findViewById(R.id.dateText);
        TextView locationText = findViewById(R.id.locationText);
        TextView scoreText = findViewById(R.id.scoreText);
        TextView teamAPlayersText = findViewById(R.id.teamAPlayersText);
        TextView teamBPlayersText = findViewById(R.id.teamBPlayersText);

// Get data from Intent
        Intent intent = getIntent();

        String teamA = intent.getStringExtra("teamA");
        String teamB = intent.getStringExtra("teamB");
        String date = intent.getStringExtra("date");
        String location = intent.getStringExtra("location");
        String score = intent.getStringExtra("score");
        String teamAPlayers = intent.getStringExtra("teamAPlayers");
        String teamBPlayers = intent.getStringExtra("teamBPlayers");

// Set UI
        teamText.setText(teamA + " vs " + teamB);
        dateText.setText("Date: " + date);
        locationText.setText("Location: " + location);
        scoreText.setText("Score: " + score);
        teamAPlayersText.setText(teamAPlayers);
        teamBPlayersText.setText(teamBPlayers);

    }
}