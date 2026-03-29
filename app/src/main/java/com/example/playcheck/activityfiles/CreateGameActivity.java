package com.example.playcheck.activityfiles;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.playcheck.R;
import com.example.playcheck.puremodel.Game;

public class CreateGameActivity extends AppCompatActivity {

    EditText teamA, teamB, venue, type, date;
    Button saveGame, backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_game);

        backBtn = findViewById(R.id.backBtnCreateGame);
        backBtn.setOnClickListener(view -> finish());

        teamA = findViewById(R.id.teamA);
        teamB = findViewById(R.id.teamB);
        venue = findViewById(R.id.gameVenue);
        type = findViewById(R.id.gameType);
        date = findViewById(R.id.gameDate);
        saveGame = findViewById(R.id.saveGame);

        saveGame.setOnClickListener(v -> performSaveGame());
    }

    private void performSaveGame() {
        String teamAVal = teamA.getText().toString();
        String teamBVal = teamB.getText().toString();
        String venueVal = venue.getText().toString();
        String typeVal = type.getText().toString();
        String dateStr = date.getText().toString();

        if (teamAVal.isEmpty() || teamBVal.isEmpty() || venueVal.isEmpty() || typeVal.isEmpty() || dateStr.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        long dateVal;
        try {
            dateVal = Long.parseLong(dateStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid date format", Toast.LENGTH_SHORT).show();
            return;
        }

        Game game = new Game(teamAVal, teamBVal, dateVal, venueVal, typeVal);
        game.save().thenAccept(unused -> {
            runOnUiThread(() -> {
                Toast.makeText(this, "Game Created", Toast.LENGTH_SHORT).show();
                finish();
            });
        }).exceptionally(throwable -> {
            runOnUiThread(() -> Toast.makeText(this, "Failed to create game: " + throwable.getMessage(), Toast.LENGTH_SHORT).show());
            return null;
        });
    }
}
