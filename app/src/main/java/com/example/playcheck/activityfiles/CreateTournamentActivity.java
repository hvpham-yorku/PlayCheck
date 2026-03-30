package com.example.playcheck.activityfiles;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.playcheck.R;
import com.example.playcheck.database.TournamentDatabase;
import com.example.playcheck.puremodel.Tournament;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CreateTournamentActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_CREATE_GAME = 1001;

    private EditText edtTournamentName, edtDescription, edtNumTeams;
    private Spinner spinnerTournamentType;
    private TextView txtStartDate, txtEndDate, txtGamesAdded;
    private Button btnSelectStartDate, btnSelectEndDate, btnCreateTournament, btnAddGame, btnFinish;
    private View tournamentFormContainer, gamesContainer;
    private ProgressBar progressBar;

    private long startDateTime = 0, endDateTime = 0;
    private String tournamentId;
    private ArrayList<String> addedGameIds = new ArrayList<>();
    private TournamentDatabase tournamentDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_tournament);

        tournamentDB = new TournamentDatabase();

        edtTournamentName = findViewById(R.id.edtTournamentName);
        edtDescription = findViewById(R.id.edtDescription);
        edtNumTeams = findViewById(R.id.edtNumTeams);
        spinnerTournamentType = findViewById(R.id.spinnerTournamentType);
        txtStartDate = findViewById(R.id.txtStartDate);
        txtEndDate = findViewById(R.id.txtEndDate);
        txtGamesAdded = findViewById(R.id.txtGamesAdded);
        btnSelectStartDate = findViewById(R.id.btnSelectStartDate);
        btnSelectEndDate = findViewById(R.id.btnSelectEndDate);
        btnCreateTournament = findViewById(R.id.btnCreateTournament);
        btnAddGame = findViewById(R.id.btnAddGame);
        btnFinish = findViewById(R.id.btnFinish);
        tournamentFormContainer = findViewById(R.id.tournamentFormContainer);
        gamesContainer = findViewById(R.id.gamesContainer);
        progressBar = findViewById(R.id.progressBar);

        // Setup spinner
        String[] tournamentTypes = getResources().getStringArray(R.array.tournament_types);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, tournamentTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTournamentType.setAdapter(adapter);

        btnSelectStartDate.setOnClickListener(v -> showDateTimePicker(true));
        btnSelectEndDate.setOnClickListener(v -> showDateTimePicker(false));
        btnCreateTournament.setOnClickListener(v -> createTournament());
        btnAddGame.setOnClickListener(v -> launchCreateGame());
        btnFinish.setOnClickListener(v -> finish());
    }

    private void showDateTimePicker(boolean isStart) {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            new TimePickerDialog(this, (view1, hourOfDay, minute) -> {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);

                long dateTime = calendar.getTimeInMillis();
                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
                String formattedDate = sdf.format(new Date(dateTime));

                if (isStart) {
                    startDateTime = dateTime;
                    txtStartDate.setText(formattedDate);
                } else {
                    endDateTime = dateTime;
                    txtEndDate.setText(formattedDate);
                }
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void createTournament() {
        String name = edtTournamentName.getText().toString().trim();
        String desc = edtDescription.getText().toString().trim();
        String type = spinnerTournamentType.getSelectedItem().toString();
        String numTeamsStr = edtNumTeams.getText().toString().trim();

        if (name.isEmpty() || desc.isEmpty() || startDateTime == 0 || endDateTime == 0) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        String creatorId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Tournament tournament = new Tournament(name, desc, startDateTime, endDateTime, creatorId, "Organizer", type);
        if (!numTeamsStr.isEmpty()) {
            tournament.setNumberOfTeams(Integer.parseInt(numTeamsStr));
        }

        tournamentDB.createTournament(tournament)
                .thenAccept(id -> {
                    runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        this.tournamentId = id;
                        tournamentFormContainer.setVisibility(View.GONE);
                        gamesContainer.setVisibility(View.VISIBLE);
                        Toast.makeText(this, "Tournament Details Saved!", Toast.LENGTH_SHORT).show();
                    });
                })
                .exceptionally(throwable -> {
                    runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(this, "Error: " + throwable.getMessage(), Toast.LENGTH_LONG).show();
                    });
                    return null;
                });
    }

    private void launchCreateGame() {
        Intent intent = new Intent(this, CreateGameActivity.class);
        intent.putExtra("tournamentId", tournamentId);
        startActivityForResult(intent, REQUEST_CODE_CREATE_GAME);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CREATE_GAME && resultCode == RESULT_OK && data != null) {
            String gameId = data.getStringExtra("gameId");
            if (gameId != null) {
                addedGameIds.add(gameId);
                txtGamesAdded.setText("Games Added: " + addedGameIds.size());
                
                // Also link game to tournament in DB
                tournamentDB.addGameToTournament(tournamentId, gameId);

                new AlertDialog.Builder(this)
                        .setTitle("Game Added")
                        .setMessage("Do you want to add another game?")
                        .setPositiveButton("Yes", (dialog, which) -> launchCreateGame())
                        .setNegativeButton("No", null)
                        .show();
            }
        }
    }
}
