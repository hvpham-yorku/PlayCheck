package com.example.playcheck.activityfiles;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.playcheck.R;
import com.example.playcheck.activityfiles.CreateGameActivity;
import com.example.playcheck.activityfiles.GameListPlayer;

public class OrganizerDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_dashboard);

        Button createGame = findViewById(R.id.btnCreateGame);
        Button viewGames = findViewById(R.id.btnViewGames);

        createGame.setOnClickListener(v ->
                startActivity(new Intent(this, CreateGameActivity.class)));

        viewGames.setOnClickListener(v ->
                startActivity(new Intent(this, GameListPlayer.class)));
    }
}