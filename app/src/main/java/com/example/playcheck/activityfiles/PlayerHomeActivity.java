package com.example.playcheck.activityfiles;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.playcheck.GameDetailsActivity;
import com.example.playcheck.R;

public class PlayerHomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_home);

        Button btnViewGames = findViewById(R.id.btnViewGames);
        Button btnSchedule = findViewById(R.id.btnSchedule);
        Button btnStandings = findViewById(R.id.btnStandings);
        Button btnHistory = findViewById(R.id.btnHistory);

        btnViewGames.setOnClickListener(v ->
                startActivity(new Intent(this, GameDetailsActivity.class))
        );

    }
}