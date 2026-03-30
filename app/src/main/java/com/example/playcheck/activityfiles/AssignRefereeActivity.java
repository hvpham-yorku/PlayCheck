package com.example.playcheck.activityfiles;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.playcheck.database.RefereeAssignmentDatabase;
import com.example.playcheck.R;
import com.example.playcheck.puremodel.Referee;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Activity for assigning referees to games
 * Can be launched from CreateGameActivity or standalone
 */
public class AssignRefereeActivity extends AppCompatActivity {
    
    private String gameId;
    private String gameName;
    private long gameDate;
    private String currentRefereeId;
    private String currentRefereeName;
    
    private TextView txtGameName, txtCurrentReferee;
    private LinearLayout refereesContainer;
    private ProgressBar progressBar;
    private Button btnBack, btnRemoveReferee;
    
    private RefereeAssignmentDatabase refereeDb;
    private DatabaseReference gamesRef;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_referee);
        
        // Get game info from intent
        gameId = getIntent().getStringExtra("gameId");
        gameName = getIntent().getStringExtra("gameName");
        gameDate = getIntent().getLongExtra("gameDate", 0);
        
        if (gameId == null) {
            Toast.makeText(this, "Error: No game specified", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        // Initialize views
        txtGameName = findViewById(R.id.txtGameName);
        txtCurrentReferee = findViewById(R.id.txtCurrentReferee);
        refereesContainer = findViewById(R.id.refereesContainer);
        progressBar = findViewById(R.id.progressBar);
        btnBack = findViewById(R.id.btnBack);
        btnRemoveReferee = findViewById(R.id.btnRemoveReferee);
        
        // Initialize database
        refereeDb = new RefereeAssignmentDatabase();
        gamesRef = FirebaseDatabase.getInstance().getReference("games");
        
        // Set game name
        txtGameName.setText(gameName != null ? gameName : "Game " + gameId);
        
        // Setup listeners
        btnBack.setOnClickListener(v -> finish());
        btnRemoveReferee.setOnClickListener(v -> removeReferee());
        
        // Load current referee and available referees
        loadCurrentReferee();
        loadAvailableReferees();
    }
    
    private void loadCurrentReferee() {
        gamesRef.child(gameId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                currentRefereeId = snapshot.child("refereeId").getValue(String.class);
                currentRefereeName = snapshot.child("refereeName").getValue(String.class);
                
                if (currentRefereeName != null) {
                    txtCurrentReferee.setText("Current Referee: " + currentRefereeName);
                    btnRemoveReferee.setVisibility(View.VISIBLE);
                } else {
                    txtCurrentReferee.setText("No referee assigned");
                    btnRemoveReferee.setVisibility(View.GONE);
                }
            }
            
            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(AssignRefereeActivity.this, 
                    "Error loading referee: " + error.getMessage(), 
                    Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void loadAvailableReferees() {
        progressBar.setVisibility(View.VISIBLE);
        refereesContainer.removeAllViews();
        
        refereeDb.getAllReferees().thenAccept(referees -> {
            runOnUiThread(() -> {
                progressBar.setVisibility(View.GONE);
                
                if (referees.isEmpty()) {
                    TextView emptyView = new TextView(this);
                    emptyView.setText("No referees available");
                    emptyView.setPadding(32, 32, 32, 32);
                    refereesContainer.addView(emptyView);
                    return;
                }
                
                for (Referee referee : referees) {
                    addRefereeCard(referee);
                }
            });
        }).exceptionally(throwable -> {
            runOnUiThread(() -> {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(this, "Error: " + throwable.getMessage(), 
                    Toast.LENGTH_SHORT).show();
            });
            return null;
        });
    }
    
    private void addRefereeCard(Referee referee) {
        View cardView = LayoutInflater.from(this)
            .inflate(R.layout.item_referee_card, refereesContainer, false);
        
        TextView txtName = cardView.findViewById(R.id.txtRefereeName);
        TextView txtEmail = cardView.findViewById(R.id.txtRefereeEmail);
        TextView txtStatus = cardView.findViewById(R.id.txtRefereeStatus);
        Button btnAssign = cardView.findViewById(R.id.btnAssignReferee);
        
        String fullName = referee.getFirstName() + " " + referee.getLastName();
        txtName.setText(fullName);
        txtEmail.setText(referee.getEmail());
        
        // Check if this referee is already assigned
        if (referee.getUid().equals(currentRefereeId)) {
            txtStatus.setText("Currently Assigned");
            txtStatus.setVisibility(View.VISIBLE);
            btnAssign.setEnabled(false);
            btnAssign.setText("Assigned");
        } else {
            txtStatus.setVisibility(View.GONE);
            btnAssign.setEnabled(true);
            btnAssign.setText("Assign");
        }
        
        btnAssign.setOnClickListener(v -> {
            // Check availability first
            checkAndAssignReferee(referee, fullName);
        });
        
        refereesContainer.addView(cardView);
    }
    
    private void checkAndAssignReferee(Referee referee, String refereeName) {
        if (gameDate == 0) {
            // No game date, can't check availability
            confirmAssignment(referee.getUid(), refereeName);
            return;
        }
        
        progressBar.setVisibility(View.VISIBLE);
        
        refereeDb.isRefereeAvailable(referee.getUid(), gameDate)
            .thenAccept(isAvailable -> {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    
                    if (isAvailable) {
                        confirmAssignment(referee.getUid(), refereeName);
                    } else {
                        showConflictDialog(referee.getUid(), refereeName);
                    }
                });
            })
            .exceptionally(throwable -> {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    // If availability check fails, just allow assignment
                    confirmAssignment(referee.getUid(), refereeName);
                });
                return null;
            });
    }
    
    private void showConflictDialog(String refereeId, String refereeName) {
        new AlertDialog.Builder(this)
            .setTitle("Schedule Conflict")
            .setMessage(refereeName + " may have another game around this time. Assign anyway?")
            .setPositiveButton("Assign Anyway", (dialog, which) -> {
                confirmAssignment(refereeId, refereeName);
            })
            .setNegativeButton("Cancel", null)
            .show();
    }
    
    private void confirmAssignment(String refereeId, String refereeName) {
        new AlertDialog.Builder(this)
            .setTitle("Confirm Assignment")
            .setMessage("Assign " + refereeName + " to this game?")
            .setPositiveButton("Confirm", (dialog, which) -> {
                assignReferee(refereeId, refereeName);
            })
            .setNegativeButton("Cancel", null)
            .show();
    }
    
    private void assignReferee(String refereeId, String refereeName) {
        progressBar.setVisibility(View.VISIBLE);
        
        refereeDb.assignRefereeToGame(gameId, refereeId, refereeName)
            .thenAccept(aVoid -> {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Referee assigned successfully", 
                        Toast.LENGTH_SHORT).show();
                    
                    // Reload
                    loadCurrentReferee();
                    loadAvailableReferees();
                });
            })
            .exceptionally(throwable -> {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Error: " + throwable.getMessage(), 
                        Toast.LENGTH_SHORT).show();
                });
                return null;
            });
    }
    
    private void removeReferee() {
        new AlertDialog.Builder(this)
            .setTitle("Remove Referee")
            .setMessage("Remove " + currentRefereeName + " from this game?")
            .setPositiveButton("Remove", (dialog, which) -> {
                progressBar.setVisibility(View.VISIBLE);
                
                refereeDb.removeRefereeFromGame(gameId, currentRefereeId)
                    .thenAccept(aVoid -> {
                        runOnUiThread(() -> {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(this, "Referee removed", 
                                Toast.LENGTH_SHORT).show();
                            
                            loadCurrentReferee();
                            loadAvailableReferees();
                        });
                    })
                    .exceptionally(throwable -> {
                        runOnUiThread(() -> {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(this, "Error: " + throwable.getMessage(), 
                                Toast.LENGTH_SHORT).show();
                        });
                        return null;
                    });
            })
            .setNegativeButton("Cancel", null)
            .show();
    }
}
