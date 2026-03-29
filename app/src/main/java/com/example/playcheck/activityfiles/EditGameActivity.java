package com.example.playcheck.activityfiles;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.playcheck.R;
import com.example.playcheck.database.GameLinkToDatabase;
import com.example.playcheck.database.NotificationLinkToDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class EditGameActivity extends AppCompatActivity {

    private EditText teamAEdit, teamBEdit, venueEdit, typeEdit, dateEdit;
    private Button saveButton, backButton;

    private String gameId;
    private NotificationLinkToDatabase notificationDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_game);

        teamAEdit = findViewById(R.id.teamAEdit);
        teamBEdit = findViewById(R.id.teamBEdit);
        venueEdit = findViewById(R.id.venueEdit);
        typeEdit = findViewById(R.id.typeEdit);
        dateEdit = findViewById(R.id.dateEdit);
        saveButton = findViewById(R.id.saveGameChangesBtn);
        backButton = findViewById(R.id.backBtnEditGame);

        notificationDb = new NotificationLinkToDatabase();

        gameId = getIntent().getStringExtra("gameId");
        String teamA = getIntent().getStringExtra("teamA");
        String teamB = getIntent().getStringExtra("teamB");
        String location = getIntent().getStringExtra("location");
        String gameType = getIntent().getStringExtra("gameType");
        long gameDateMillis = getIntent().getLongExtra("gameDateMillis", 0);

        teamAEdit.setText(teamA);
        teamBEdit.setText(teamB);
        venueEdit.setText(location);
        typeEdit.setText(gameType);
        dateEdit.setText(String.valueOf(gameDateMillis));

        backButton.setOnClickListener(v -> finish());
        saveButton.setOnClickListener(v -> saveChanges());
    }

    private void saveChanges() {
        String teamA = teamAEdit.getText().toString().trim();
        String teamB = teamBEdit.getText().toString().trim();
        String venue = venueEdit.getText().toString().trim();
        String type = typeEdit.getText().toString().trim();
        String dateString = dateEdit.getText().toString().trim();

        if (TextUtils.isEmpty(gameId)) {
            Toast.makeText(this, "Invalid game ID", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(teamA) || TextUtils.isEmpty(teamB)) {
            Toast.makeText(this, "Teams cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(venue)) {
            Toast.makeText(this, "Venue cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(type)) {
            Toast.makeText(this, "Game type cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        long gameDate;
        try {
            gameDate = Long.parseLong(dateString);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Date must be a valid timestamp", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("teamA", teamA);
        updates.put("teamB", teamB);
        updates.put("gameVenue", venue);
        updates.put("gameType", type);
        updates.put("gameDate", gameDate);

        GameLinkToDatabase.updateGameDetails(gameId, updates)
                .addOnSuccessListener(unused -> {
                    notifyParticipants(teamA, teamB, venue);
                    Toast.makeText(this, "Game updated successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Update failed: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }

    private void notifyParticipants(String teamA, String teamB, String venue) {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

        rootRef.child("games").child(gameId).get().addOnSuccessListener(snapshot -> {
            String title = "Game Updated";
            String message = "The game " + teamA + " vs " + teamB + " has been updated. Venue: " + venue;

            String refereeId = snapshot.child("refereeId").getValue(String.class);
            if (refereeId != null && !refereeId.isEmpty()) {
                notificationDb.sendNotificationToUser(
                        "Referee",
                        refereeId,
                        title,
                        message,
                        "schedule_update",
                        gameId
                );
            }

            notifyTeamPlayers(snapshot, "teamAId", title, message);
            notifyTeamPlayers(snapshot, "teamBId", title, message);
        });
    }

    private void notifyTeamPlayers(DataSnapshot gameSnapshot, String teamKeyField, String title, String message) {
        String teamId = gameSnapshot.child(teamKeyField).getValue(String.class);
        if (teamId == null || teamId.isEmpty()) return;

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.child("teams").child(teamId).child("players").get().addOnSuccessListener(playersSnapshot -> {
            for (DataSnapshot playerSnapshot : playersSnapshot.getChildren()) {
                String playerUid = playerSnapshot.getKey();
                if (playerUid != null) {
                    notificationDb.sendNotificationToUser(
                            "Player",
                            playerUid,
                            title,
                            message,
                            "schedule_update",
                            gameId
                    );
                }
            }
        });
    }
}