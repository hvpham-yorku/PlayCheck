package com.example.playcheck;
import android.widget.TextView;


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

        Game game = GameRepository.getGames().get(0);

        teamText.setText(game.getTeamA() + " vs " + game.getTeamB());
        dateText.setText("Date: " + game.getDate());
        locationText.setText("Location: " + game.getLocation());
        scoreText.setText("Score: " + game.getScore());

    }
}