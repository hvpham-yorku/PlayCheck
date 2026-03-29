package com.example.playcheck.activityfiles;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.playcheck.R;

public class GameDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        Button backButton = findViewById(R.id.backBtnGameDetails);
        Button editGameButton = findViewById(R.id.editGameButton); // add in XML

        Intent intent = getIntent();

        String teamA = intent.getStringExtra("teamA");
        String teamB = intent.getStringExtra("teamB");
        String date = intent.getStringExtra("date");
        long gameDateMillis = intent.getLongExtra("gameDateMillis", 0);
        String location = intent.getStringExtra("location");
        String score = intent.getStringExtra("score");
        String teamAPlayers = intent.getStringExtra("teamAPlayers");
        String teamBPlayers = intent.getStringExtra("teamBPlayers");
        String referee = intent.getStringExtra("referee");
        String gameType = intent.getStringExtra("gameType");
        String gameId = intent.getStringExtra("gameId");

        refereeReportButton.setOnClickListener(v -> {
            Intent reportIntent = new Intent(this, RefereeReportActivity.class);
            reportIntent.putExtra("gameId", gameId);
            reportIntent.putExtra("gameType", gameType);
            reportIntent.putExtra("teamA", teamA);
            reportIntent.putExtra("teamB", teamB);
            startActivity(reportIntent);
        });

        editGameButton.setOnClickListener(v -> {
            Intent editIntent = new Intent(this, EditGameActivity.class);
            editIntent.putExtra("gameId", gameId);
            editIntent.putExtra("teamA", teamA);
            editIntent.putExtra("teamB", teamB);
            editIntent.putExtra("gameDateMillis", gameDateMillis);
            editIntent.putExtra("location", location);
            editIntent.putExtra("gameType", gameType);
            startActivity(editIntent);
        });

        backButton.setOnClickListener(view -> finish());

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