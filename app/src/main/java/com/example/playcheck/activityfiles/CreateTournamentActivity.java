package com.example.playcheck.activityfiles;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.playcheck.Database.TournamentDatabase;
import com.example.playcheck.R;
import com.example.playcheck.puremodel.Tournament;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Activity for creating tournaments with multiple games
 * Uses CreateGameActivity repeatedly - ALL tournament logic contained here
 */
public class CreateTournamentActivity extends AppCompatActivity {

    // UI Elements
    private EditText edtTournamentName, edtDescription, edtNumTeams;
    private TextView txtStartDate, txtEndDate;
    private Spinner spinnerTournamentType;
    private Button btnSelectStartDate, btnSelectEndDate, btnCreateTournament;
    private LinearLayout gamesContainer, tournamentFormContainer;
    private TextView txtGamesAdded;
    private Button btnAddGame, btnFinish;
    private ProgressBar progressBar;

    // Data
    private long startDate = 0;
    private long endDate = 0;
    private String tournamentId;
    private String tournamentName;
    private List<String> addedGameIds;
    private TournamentDatabase tournamentDb;
    private FirebaseAuth mAuth;
    
    // Tournament creation state
    private boolean tournamentCreated = false;
    private String organizerId;
    private String organizerName;

    private static final int REQUEST_CREATE_GAME = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_tournament);

        // Initialize
        mAuth = FirebaseAuth.getInstance();
        tournamentDb = new TournamentDatabase();
        addedGameIds = new ArrayList<>();

        // Get current user info
        if (mAuth.getCurrentUser() != null) {
            organizerId = mAuth.getCurrentUser().getUid();
            loadOrganizerName();
        } else {
            organizerId = "unknown";
            organizerName = "Organizer";
        }

        initializeViews();
        setupListeners();
        setupBackPressHandler();
    }

    private void initializeViews() {
        edtTournamentName = findViewById(R.id.edtTournamentName);
        edtDescription = findViewById(R.id.edtDescription);
        edtNumTeams = findViewById(R.id.edtNumTeams);
        txtStartDate = findViewById(R.id.txtStartDate);
        txtEndDate = findViewById(R.id.txtEndDate);
        btnSelectStartDate = findViewById(R.id.btnSelectStartDate);
        btnSelectEndDate = findViewById(R.id.btnSelectEndDate);
        spinnerTournamentType = findViewById(R.id.spinnerTournamentType);
        btnCreateTournament = findViewById(R.id.btnCreateTournament);
        progressBar = findViewById(R.id.progressBar);
        
        // Containers
        tournamentFormContainer = findViewById(R.id.tournamentFormContainer);
        gamesContainer = findViewById(R.id.gamesContainer);
        
        // Games section
        txtGamesAdded = findViewById(R.id.txtGamesAdded);
        btnAddGame = findViewById(R.id.btnAddGame);
        btnFinish = findViewById(R.id.btnFinish);

        // Setup tournament type spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.tournament_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTournamentType.setAdapter(adapter);

        // Hide games section initially
        gamesContainer.setVisibility(View.GONE);
    }

    private void setupListeners() {
        btnSelectStartDate.setOnClickListener(v -> showDateTimePicker(true));
        btnSelectEndDate.setOnClickListener(v -> showDateTimePicker(false));
        btnCreateTournament.setOnClickListener(v -> createTournament());
        btnAddGame.setOnClickListener(v -> addGameToTournament());
        btnFinish.setOnClickListener(v -> finishTournament());
    }

    /**
     * Setup modern back press handling (replaces deprecated onBackPressed)
     */
    private void setupBackPressHandler() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (tournamentCreated) {
                    new AlertDialog.Builder(CreateTournamentActivity.this)
                            .setTitle("Leave Tournament Setup?")
                            .setMessage("Your tournament has been created with " + addedGameIds.size() + 
                                    " game(s). You can add more games later.")
                            .setPositiveButton("Leave", (dialog, which) -> {
                                setEnabled(false);
                                getOnBackPressedDispatcher().onBackPressed();
                            })
                            .setNegativeButton("Stay", null)
                            .show();
                } else {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });
    }

    private void loadOrganizerName() {
        DatabaseReference userRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(organizerId);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String firstName = snapshot.child("firstName").getValue(String.class);
                String lastName = snapshot.child("lastName").getValue(String.class);
                if (firstName != null && lastName != null) {
                    organizerName = firstName + " " + lastName;
                } else {
                    organizerName = "Organizer";
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                organizerName = "Organizer";
            }
        });
    }

    private void showDateTimePicker(boolean isStartDate) {
        Calendar calendar = Calendar.getInstance();

        // Date Picker
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    // Time Picker
                    TimePickerDialog timePickerDialog = new TimePickerDialog(
                            this,
                            (timeView, hourOfDay, minute) -> {
                                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                calendar.set(Calendar.MINUTE, minute);

                                long timestamp = calendar.getTimeInMillis();
                                SimpleDateFormat sdf = new SimpleDateFormat(
                                        "MMM dd, yyyy HH:mm", Locale.getDefault());

                                if (isStartDate) {
                                    startDate = timestamp;
                                    txtStartDate.setText(sdf.format(calendar.getTime()));
                                } else {
                                    endDate = timestamp;
                                    txtEndDate.setText(sdf.format(calendar.getTime()));
                                }
                            },
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            true
                    );
                    timePickerDialog.show();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    /**
     * Step 1: Create the tournament in Firebase
     */
    private void createTournament() {
        // Validate inputs
        String name = edtTournamentName.getText().toString().trim();
        String description = edtDescription.getText().toString().trim();
        String numTeamsStr = edtNumTeams.getText().toString().trim();

        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter tournament name", Toast.LENGTH_SHORT).show();
            return;
        }

        if (startDate == 0 || endDate == 0) {
            Toast.makeText(this, "Please select start and end dates", Toast.LENGTH_SHORT).show();
            return;
        }

        if (endDate <= startDate) {
            Toast.makeText(this, "End date must be after start date", Toast.LENGTH_SHORT).show();
            return;
        }

        int numTeams = 0;
        if (!numTeamsStr.isEmpty()) {
            try {
                numTeams = Integer.parseInt(numTeamsStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid number of teams", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        String tournamentType = spinnerTournamentType.getSelectedItem().toString();

        // Save tournament name for later use
        this.tournamentName = name;

        // Create tournament object
        Tournament tournament = new Tournament(
                name,
                description,
                startDate,
                endDate,
                organizerId,
                organizerName != null ? organizerName : "Organizer",
                tournamentType
        );
        tournament.setNumberOfTeams(numTeams);

        // Save to Firebase
        progressBar.setVisibility(View.VISIBLE);
        btnCreateTournament.setEnabled(false);

        tournamentDb.createTournament(tournament)
                .thenAccept(id -> {
                    runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        tournamentId = id;
                        tournamentCreated = true;

                        Toast.makeText(this, "Tournament created! Now add games.", 
                                Toast.LENGTH_LONG).show();

                        // Show games section, hide tournament form
                        showGamesSection();
                    });
                })
                .exceptionally(throwable -> {
                    runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        btnCreateTournament.setEnabled(true);
                        Toast.makeText(this, "Error: " + throwable.getMessage(), 
                                Toast.LENGTH_SHORT).show();
                    });
                    return null;
                });
    }

    /**
     * Show the games section after tournament is created
     */
    private void showGamesSection() {
        // Hide tournament form
        tournamentFormContainer.setVisibility(View.GONE);

        // Show games section
        gamesContainer.setVisibility(View.VISIBLE);
        updateGamesCount();

        // Show helpful message
        new AlertDialog.Builder(this)
                .setTitle("Tournament Created!")
                .setMessage("Your tournament '" + tournamentName + 
                        "' has been created.\n\nNow add games using the 'Add Game' button. " +
                        "Each game you create will be automatically added to this tournament.")
                .setPositiveButton("OK", null)
                .show();
    }

    /**
     * Step 2: Launch CreateGameActivity to create a game
     * When game is created, we'll receive the gameId and add it to tournament
     */
    private void addGameToTournament() {
        if (!tournamentCreated || tournamentId == null) {
            Toast.makeText(this, "Please create the tournament first", 
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Launch CreateGameActivity normally
        // It doesn't know about tournaments - we handle that here
        Intent intent = new Intent(this, CreateGameActivity.class);
        startActivityForResult(intent, REQUEST_CREATE_GAME);
        
        Toast.makeText(this, "Creating game for: " + tournamentName, 
                Toast.LENGTH_SHORT).show();
    }

    /**
     * Receive the gameId from CreateGameActivity and add to tournament
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CREATE_GAME && resultCode == RESULT_OK && data != null) {
            String gameId = data.getStringExtra("gameId");
            
            if (gameId != null && !addedGameIds.contains(gameId)) {
                // Add this game to the tournament
                linkGameToTournament(gameId);
            }
        }
    }

    /**
     * Link a created game to this tournament
     */
    private void linkGameToTournament(String gameId) {
        progressBar.setVisibility(View.VISIBLE);
        
        // Use TournamentDatabase to link the game
        tournamentDb.addGameToTournament(tournamentId, gameId)
                .thenAccept(aVoid -> {
                    runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        
                        // Track this game
                        addedGameIds.add(gameId);
                        updateGamesCount();
                        
                        Toast.makeText(this, "Game added to tournament!", 
                                Toast.LENGTH_SHORT).show();

                        // Ask if they want to add another game
                        showAddAnotherGameDialog();
                    });
                })
                .exceptionally(throwable -> {
                    runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(this, "Error adding game to tournament: " + 
                                throwable.getMessage(), Toast.LENGTH_LONG).show();
                    });
                    return null;
                });
    }

    /**
     * Update the count of games added
     */
    private void updateGamesCount() {
        txtGamesAdded.setText("Games Added: " + addedGameIds.size());
    }

    /**
     * Ask if user wants to add another game
     */
    private void showAddAnotherGameDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Add Another Game?")
                .setMessage("Would you like to add another game to this tournament?")
                .setPositiveButton("Yes", (dialog, which) -> addGameToTournament())
                .setNegativeButton("No", null)
                .show();
    }

    /**
     * Finish tournament creation
     */
    private void finishTournament() {
        if (addedGameIds.isEmpty()) {
            new AlertDialog.Builder(this)
                    .setTitle("No Games Added")
                    .setMessage("You haven't added any games to this tournament. " +
                            "Are you sure you want to finish?")
                    .setPositiveButton("Yes, Finish", (dialog, which) -> {
                        Toast.makeText(this, "Tournament created with 0 games", 
                                Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .setNegativeButton("Add Games", null)
                    .show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Finish Tournament Setup")
                .setMessage("You've added " + addedGameIds.size() + " game(s) to this tournament. " +
                        "Tournament is ready!")
                .setPositiveButton("Done", (dialog, which) -> {
                    Toast.makeText(this, "Tournament created successfully!", 
                            Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton("Add More Games", null)
                .show();
    }
}
