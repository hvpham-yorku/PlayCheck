package com.example.playcheck.activityfiles;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EditGameActivity extends AppCompatActivity {

    private EditText teamAEdit, teamBEdit, venueEdit, typeEdit;
    private Button saveButton, gameDateBtn, gameTimeBtn;

    private String gameId;
    private NotificationLinkToDatabase notificationDb;
    private GameLinkToDatabase gameDb;

    private final Calendar selectedDateTime = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_game);

        teamAEdit = findViewById(R.id.teamAEdit);
        teamBEdit = findViewById(R.id.teamBEdit);
        venueEdit = findViewById(R.id.venueEdit);
        typeEdit = findViewById(R.id.typeEdit);
        saveButton = findViewById(R.id.saveGameChangesBtn);
        gameDateBtn = findViewById(R.id.gameDateBtn);
        gameTimeBtn = findViewById(R.id.gameTimeBtn);

        notificationDb = new NotificationLinkToDatabase();
        gameDb = new GameLinkToDatabase();

        // Get data from intent
        gameId = getIntent().getStringExtra("gameId");
        String teamA = getIntent().getStringExtra("teamA");
        String teamB = getIntent().getStringExtra("teamB");
        String location = getIntent().getStringExtra("location");
        String gameType = getIntent().getStringExtra("gameType");
        long gameDateMillis = getIntent().getLongExtra("gameDateMillis", 0);

        if (teamA != null) teamAEdit.setText(teamA);
        if (teamB != null) teamBEdit.setText(teamB);
        if (location != null) venueEdit.setText(location);
        if (gameType != null) typeEdit.setText(gameType);

        // Set existing date/time
        if (gameDateMillis > 0) {
            selectedDateTime.setTimeInMillis(gameDateMillis);
        }

        updateDateButtonText();
        updateTimeButtonText();

        saveButton.setOnClickListener(v -> saveChanges());

        gameDateBtn.setOnClickListener(v -> showDatePicker());
        gameTimeBtn.setOnClickListener(v -> showTimePicker());
    }

    private void showDatePicker() {
        int year = selectedDateTime.get(Calendar.YEAR);
        int month = selectedDateTime.get(Calendar.MONTH);
        int day = selectedDateTime.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    selectedDateTime.set(Calendar.YEAR, selectedYear);
                    selectedDateTime.set(Calendar.MONTH, selectedMonth);
                    selectedDateTime.set(Calendar.DAY_OF_MONTH, selectedDay);
                    updateDateButtonText();
                },
                year,
                month,
                day
        );

        dialog.show();
    }

    private void showTimePicker() {
        int hour = selectedDateTime.get(Calendar.HOUR_OF_DAY);
        int minute = selectedDateTime.get(Calendar.MINUTE);

        TimePickerDialog dialog = new TimePickerDialog(
                this,
                (view, selectedHour, selectedMinute) -> {
                    selectedDateTime.set(Calendar.HOUR_OF_DAY, selectedHour);
                    selectedDateTime.set(Calendar.MINUTE, selectedMinute);
                    selectedDateTime.set(Calendar.SECOND, 0);
                    selectedDateTime.set(Calendar.MILLISECOND, 0);
                    updateTimeButtonText();
                },
                hour,
                minute,
                false
        );

        dialog.show();
    }

    private void updateDateButtonText() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        gameDateBtn.setText(dateFormat.format(selectedDateTime.getTime()));
    }

    private void updateTimeButtonText() {
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
        gameTimeBtn.setText(timeFormat.format(selectedDateTime.getTime()));
    }

    private void saveChanges() {
        String teamA = teamAEdit.getText().toString().trim();
        String teamB = teamBEdit.getText().toString().trim();
        String venue = venueEdit.getText().toString().trim();
        String type = typeEdit.getText().toString().trim();

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

        long gameDate = selectedDateTime.getTimeInMillis();

        Map<String, Object> updates = new HashMap<>();
        updates.put("teamA", teamA);
        updates.put("teamB", teamB);
        updates.put("gameVenue", venue);
        updates.put("gameType", type);
        updates.put("gameDate", gameDate);

        gameDb.updateGameDetails(gameId, updates, task -> {
            if (task.isSuccessful()) {
                notifyParticipants(teamA, teamB, venue);
                Toast.makeText(this, "Game updated successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                String error = task.getException() != null
                        ? task.getException().getMessage()
                        : "Unknown error";
                Toast.makeText(this, "Update failed: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void notifyParticipants(String teamA, String teamB, String venue) {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

        rootRef.child("games").child(gameId).get().addOnSuccessListener(snapshot -> {
            String title = "Game Updated";
            String message = "The game " + teamA + " vs " + teamB + " has been updated. Venue: " + venue;

            DataSnapshot refereesSnapshot = snapshot.child("referees");
            if (refereesSnapshot.exists()) {
                for (DataSnapshot refSnap : refereesSnapshot.getChildren()) {
                    String refereeUid = refSnap.getKey();
                    if (refereeUid != null && !refereeUid.isEmpty()) {
                        notificationDb.sendNotificationToUser(
                                "Referee",
                                refereeUid,
                                title,
                                message,
                                "schedule_update",
                                gameId
                        );
                    }
                }
            }

            notifyTeamPlayers(snapshot, "teamAid", title, message);
            notifyTeamPlayers(snapshot, "teamBid", title, message);
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
