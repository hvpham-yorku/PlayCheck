package com.example.playcheck.activityfiles;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.playcheck.R;

public class RefereeHomeActivity extends AppCompatActivity {

    Button btnViewMyGames;
    Button btnSchedule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_referee_home);

        btnViewMyGames = findViewById(R.id.btnRefGames);
        btnSchedule = findViewById(R.id.btnRefSchedule);

        btnViewMyGames.setOnClickListener(v -> {
            startActivity(new Intent(this, RefereeGamesActivity.class));
        });

        btnSchedule.setOnClickListener(v -> {
            startActivity(new Intent(this, GameSchedule.class));
        });
    }
}