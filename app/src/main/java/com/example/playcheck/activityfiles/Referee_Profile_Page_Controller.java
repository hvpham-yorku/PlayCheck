package com.example.playcheck.activityfiles;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.playcheck.R;
import com.example.playcheck.puremodel.Game;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class Referee_Profile_Page_Controller extends AppCompatActivity {

    private boolean isEditMode = false;
    private EditText etUsername;
    private TextView tvUsername, tvRefereeName, tvScore;
    private View editActionButtons, statsSection, statsCard;
    private LinearLayout statsContentPlaceholder;
    
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private String currentUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_referee_profile_page);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            currentUid = user.getUid();
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupClickListeners();
        loadUserProfile();
        loadMostRecentGameStats();
    }

    private void initViews() {
        etUsername = findViewById(R.id.etUsername);
        tvUsername = findViewById(R.id.tvUsername);
        tvRefereeName = findViewById(R.id.tvRefereeName);
        tvScore = findViewById(R.id.tvScore);
        editActionButtons = findViewById(R.id.editActionButtons);
        statsSection = findViewById(R.id.statsSection);
        statsCard = findViewById(R.id.statsCard);
        statsContentPlaceholder = findViewById(R.id.statsContentPlaceholder);
    }

    private void setupClickListeners() {
        View btnEdit = findViewById(R.id.btnEditProfile);
        if (btnEdit != null) btnEdit.setOnClickListener(v -> toggleEditMode(true));
        
        View btnCancel = findViewById(R.id.btnCancel);
        if (btnCancel != null) btnCancel.setOnClickListener(v -> toggleEditMode(false));
        
        View btnSave = findViewById(R.id.btnSave);
        if (btnSave != null) btnSave.setOnClickListener(v -> saveProfileChanges());

        findViewById(R.id.navHome).setOnClickListener(v -> navigateTo(Homepage_Controller.class));
        findViewById(R.id.navBack).setOnClickListener(v -> finish());
    }

    private void loadUserProfile() {
        if (currentUid == null) return;

        mDatabase.child("users").child("Referee").child(currentUid).child("profile")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String firstName = snapshot.child("firstName").getValue(String.class);
                            String lastName = snapshot.child("lastName").getValue(String.class);
                            String username = snapshot.child("username").getValue(String.class);

                            if (tvRefereeName != null) tvRefereeName.setText(firstName + " " + lastName);
                            if (tvUsername != null) tvUsername.setText(username != null ? username : "N/A");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(Referee_Profile_Page_Controller.this, "Failed to load profile", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadMostRecentGameStats() {
        Query recentGameQuery = mDatabase.child("games").orderByChild("gameDate").limitToLast(1);
        recentGameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot gameSnapshot : snapshot.getChildren()) {
                        Game game = gameSnapshot.getValue(Game.class);
                        if (game != null) {
                            displayGameStats(game, gameSnapshot);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Referee_Profile_Page_Controller.this, "Failed to load recent game", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayGameStats(Game game, DataSnapshot gameSnapshot) {
        String sportType = game.getGameType();
        if (tvScore != null) {
            DataSnapshot reportSnapshot = gameSnapshot.child("matchReport");
            if (reportSnapshot.exists()) {
                String score = reportSnapshot.child("score").getValue(String.class);
                tvScore.setText(score != null ? score : "N/A");
            } else {
                tvScore.setText("N/A");
            }
        }

        if (statsContentPlaceholder == null) return;
        statsContentPlaceholder.removeAllViews();
        
        View statsView = null;
        if ("Soccer".equalsIgnoreCase(sportType) || "Football".equalsIgnoreCase(sportType)) {
            statsView = getLayoutInflater().inflate(R.layout.layout_stat_row_soccer, statsContentPlaceholder, false);
            populateSoccerStats(statsView, gameSnapshot.child("matchReport").child("detailedStats"));
        } else if ("Basketball".equalsIgnoreCase(sportType)) {
            statsView = getLayoutInflater().inflate(R.layout.layout_stat_row_basketball, statsContentPlaceholder, false);
            populateBasketballStats(statsView, gameSnapshot.child("matchReport").child("detailedStats"));
        }
        
        if (statsView != null) {
            statsContentPlaceholder.addView(statsView);
        }
    }

    private void populateSoccerStats(View v, DataSnapshot stats) {
        if (!stats.exists()) return;
        setTextFromSnapshot(v, R.id.tvShootingLeft, stats.child("shootingLeft"));
        setTextFromSnapshot(v, R.id.tvShootingRight, stats.child("shootingRight"));
        setTextFromSnapshot(v, R.id.tvAttacksLeft, stats.child("attacksLeft"));
        setTextFromSnapshot(v, R.id.tvAttacksRight, stats.child("attacksRight"));
        setTextFromSnapshot(v, R.id.tvPossessionLeft, stats.child("possessionLeft"));
        setTextFromSnapshot(v, R.id.tvPossessionRight, stats.child("possessionRight"));
        setTextFromSnapshot(v, R.id.tvCornersLeft, stats.child("cornersLeft"));
        setTextFromSnapshot(v, R.id.tvCornersRight, stats.child("cornersRight"));
        setTextFromSnapshot(v, R.id.tvYellowCardsLeft, stats.child("yellowCardsLeft"));
        setTextFromSnapshot(v, R.id.tvYellowCardsRight, stats.child("yellowCardsRight"));
        setTextFromSnapshot(v, R.id.tvRedCardsLeft, stats.child("redCardsLeft"));
        setTextFromSnapshot(v, R.id.tvRedCardsRight, stats.child("redCardsRight"));
    }

    private void populateBasketballStats(View v, DataSnapshot stats) {
        if (!stats.exists()) return;
        setTextFromSnapshot(v, R.id.tvPointsLeft, stats.child("pointsLeft"));
        setTextFromSnapshot(v, R.id.tvPointsRight, stats.child("pointsRight"));
        setTextFromSnapshot(v, R.id.tvReboundsLeft, stats.child("reboundsLeft"));
        setTextFromSnapshot(v, R.id.tvReboundsRight, stats.child("reboundsRight"));
        setTextFromSnapshot(v, R.id.tvAssistsLeft, stats.child("assistsLeft"));
        setTextFromSnapshot(v, R.id.tvAssistsRight, stats.child("assistsRight"));
        setTextFromSnapshot(v, R.id.tvStealsLeft, stats.child("stealsLeft"));
        setTextFromSnapshot(v, R.id.tvStealsRight, stats.child("stealsRight"));
        setTextFromSnapshot(v, R.id.tvBlocksLeft, stats.child("blocksLeft"));
        setTextFromSnapshot(v, R.id.tvBlocksRight, stats.child("blocksRight"));
    }

    private void setTextFromSnapshot(View v, int resId, DataSnapshot snapshot) {
        TextView tv = v.findViewById(resId);
        if (tv != null && snapshot.exists()) {
            Object val = snapshot.getValue();
            tv.setText(String.valueOf(val));
        }
    }

    private void saveProfileChanges() {
        String newUsername = etUsername.getText().toString().trim();

        if (newUsername.isEmpty()) {
            Toast.makeText(this, "Username cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("username", newUsername);

        mDatabase.child("users").child("Referee").child(currentUid).child("profile")
                .updateChildren(updates)
                .addOnSuccessListener(aVoid -> {
                    tvUsername.setText(newUsername);
                    toggleEditMode(false);
                    Toast.makeText(Referee_Profile_Page_Controller.this, "Profile updated", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(Referee_Profile_Page_Controller.this, "Update failed", Toast.LENGTH_SHORT).show());
    }

    private void toggleEditMode(boolean enable) {
        isEditMode = enable;
        if (editActionButtons != null) editActionButtons.setVisibility(enable ? View.VISIBLE : View.GONE);
        if (etUsername != null) etUsername.setVisibility(enable ? View.VISIBLE : View.GONE);
        if (tvUsername != null) tvUsername.setVisibility(enable ? View.GONE : View.VISIBLE);
        
        if (statsSection != null) statsSection.setVisibility(enable ? View.GONE : View.VISIBLE);
        if (statsCard != null) statsCard.setVisibility(enable ? View.GONE : View.VISIBLE);

        if (enable && etUsername != null && tvUsername != null) {
            etUsername.setText(tvUsername.getText());
        }
    }

    private void navigateTo(Class<?> targetActivity) {
        startActivity(new Intent(this, targetActivity));
    }
}