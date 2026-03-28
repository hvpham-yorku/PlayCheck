package com.example.playcheck.activityfiles;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.playcheck.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class CreateGameActivity extends AppCompatActivity {

    EditText teamA, teamB, venue, type, date;
    Button saveGame, backBtn;

    DatabaseReference gamesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_game);

        //back to previous page button
        backBtn = findViewById(R.id.backBtnCreateGame);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        gamesRef = FirebaseDatabase.getInstance()
                .getReference("games");

        teamA = findViewById(R.id.teamA);
        teamB = findViewById(R.id.teamB);
        venue = findViewById(R.id.gameVenue);
        type = findViewById(R.id.gameType);
        date = findViewById(R.id.gameDate);
        saveGame = findViewById(R.id.saveGame);

        saveGame.setOnClickListener(v -> saveGame());
    }

    private void saveGame() {

        String teamAVal = teamA.getText().toString();
        String teamBVal = teamB.getText().toString();
        String venueVal = venue.getText().toString();
        String typeVal = type.getText().toString();
        long dateVal = Long.parseLong(date.getText().toString());

        String gameId = gamesRef.push().getKey();

        Map<String, Object> game = new HashMap<>();

        game.put("teamA", teamAVal);
        game.put("teamB", teamBVal);
        game.put("gameVenue", venueVal);
        game.put("gameType", typeVal);
        game.put("gameDate", dateVal);
        game.put("gameId", gameId);

        gamesRef.child(gameId).setValue(game)
                .addOnSuccessListener(unused -> {
                        Toast.makeText(this, "Game Created", Toast.LENGTH_SHORT).show();

                Intent resultIntent = new Intent();
                resultIntent.putExtra("gameId", gameId);
                setResult(RESULT_OK, resultIntent);
                });
    }
}