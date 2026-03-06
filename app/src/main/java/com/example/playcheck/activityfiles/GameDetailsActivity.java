package com.example.playcheck.activityfiles;
import android.widget.TextView;
import android.content.Intent;



import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;
import com.example.playcheck.R;

import com.example.playcheck.activityfiles.RefereeReportActivity;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;

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

        refereeReportButton.setOnClickListener(v -> {

            Intent reportIntent = new Intent(this, RefereeReportActivity.class);

            reportIntent.putExtra("gameId", getIntent().getLongExtra("gameId",0));
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
