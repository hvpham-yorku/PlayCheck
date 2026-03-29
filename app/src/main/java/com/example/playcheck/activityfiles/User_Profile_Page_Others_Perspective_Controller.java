package com.example.playcheck.activityfiles;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
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
import com.example.playcheck.puremodel.Organizer;
import com.example.playcheck.puremodel.Player;
import com.example.playcheck.puremodel.Referee;
import com.example.playcheck.puremodel.User;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class User_Profile_Page_Others_Perspective_Controller extends AppCompatActivity {

    private TextView tvUserName, tvUsername, tvUserRole, tvScore, tvSideFavourite, btnLike, btnDislike;
    private MaterialButton btnAddUser, btnFavourite;
    private LinearLayout statsContentPlaceholder;
    
    private DatabaseReference mDatabase;
    private String targetUid;
    private String currentUid;
    private String userType;

    private boolean isLiked = false;
    private boolean isDisliked = false;
    private boolean isFavourited = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_profile_page_others_perspective);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        targetUid = getIntent().getStringExtra("USER_ID");
        
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            currentUid = currentUser.getUid();
        } else {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            finish();
            return;
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
        checkUserInteractions();
    }

    private void initViews() {
        tvUserName = findViewById(R.id.tvUserName);
        tvUsername = findViewById(R.id.tvUsername);
        tvUserRole = findViewById(R.id.tvUserRole);
        tvScore = findViewById(R.id.tvScore);
        tvSideFavourite = findViewById(R.id.tv_favourite);
        btnAddUser = findViewById(R.id.btnAddUser);
        btnLike = findViewById(R.id.btnLike);
        btnDislike = findViewById(R.id.btnDislike);
        btnFavourite = findViewById(R.id.btnFavourite);
        statsContentPlaceholder = findViewById(R.id.statsContentPlaceholder);
    }

    private void setupClickListeners() {
        if (btnLike != null) btnLike.setOnClickListener(v -> handleInteraction("likes"));
        if (btnDislike != null) btnDislike.setOnClickListener(v -> handleInteraction("dislikes"));
        if (btnFavourite != null) btnFavourite.setOnClickListener(v -> handleInteraction("favourites"));
        if (tvSideFavourite != null) {
            tvSideFavourite.setOnClickListener(v -> handleInteraction("favourites"));
        }
        
        // Bottom Navigation
        findViewById(R.id.navHome).setOnClickListener(v -> navigateTo(Homepage_Controller.class));
        findViewById(R.id.navNews).setOnClickListener(v -> navigateTo(GameList.class));
        findViewById(R.id.navClipboard).setOnClickListener(v -> navigateTo(Video_Review_Page_Plus_Stats_controller.class));
        findViewById(R.id.navBack).setOnClickListener(v -> finish());
        findViewById(R.id.navProfile).setOnClickListener(v -> navigateToMyProfile());
    }

    private void checkUserInteractions() {
        if (targetUid == null || currentUid == null) return;

        mDatabase.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DataSnapshot userNode = null;
                if (snapshot.child("Organizer").hasChild(targetUid)) userNode = snapshot.child("Organizer").child(targetUid);
                else if (snapshot.child("Player").hasChild(targetUid)) userNode = snapshot.child("Player").child(targetUid);
                else if (snapshot.child("Referee").hasChild(targetUid)) userNode = snapshot.child("Referee").child(targetUid);

                if (userNode != null) {
                    isLiked = userNode.child("likes").hasChild(currentUid);
                    isDisliked = userNode.child("dislikes").hasChild(currentUid);
                    isFavourited = userNode.child("favourites").hasChild(currentUid);
                    updateInteractionUI();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void updateInteractionUI() {
        highlightTextView(btnLike, isLiked, Color.parseColor("#4CAF50")); // Green
        highlightTextView(btnDislike, isDisliked, Color.parseColor("#F44336")); // Red
        highlightMaterialButton(btnFavourite, isFavourited, Color.parseColor("#E91E63")); // Pink
        
        if (tvSideFavourite != null) {
            tvSideFavourite.setTextColor(isFavourited ? Color.parseColor("#E91E63") : Color.parseColor("#333333"));
        }
    }

    private void highlightTextView(TextView textView, boolean highlight, int activeColor) {
        if (textView == null) return;
        if (highlight) {
            textView.setTextColor(activeColor);
            textView.setAlpha(1.0f);
        } else {
            textView.setTextColor(Color.parseColor("#333333"));
            textView.setAlpha(0.6f);
        }
    }

    private void highlightMaterialButton(MaterialButton button, boolean highlight, int activeColor) {
        if (button == null) return;
        if (highlight) {
            button.setBackgroundTintList(ColorStateList.valueOf(activeColor));
        } else {
            button.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#212121")));
        }
    }

    private void handleInteraction(String type) {
        if (targetUid == null || userType == null || currentUid == null) return;

        if (type.equals("likes")) {
            if (isDisliked) {
                // Remove dislike first
                isDisliked = false;
                updateDatabaseState("dislikes", false, "totaldislikes", "maxDislikes");
            }
            isLiked = !isLiked;
            updateDatabaseState("likes", isLiked, "totallikes", "maxLikes");
        } else if (type.equals("dislikes")) {
            if (isLiked) {
                // Remove like first
                isLiked = false;
                updateDatabaseState("likes", false, "totallikes", "maxLikes");
            }
            isDisliked = !isDisliked;
            updateDatabaseState("dislikes", isDisliked, "totaldislikes", "maxDislikes");
        } else if (type.equals("favourites")) {
            isFavourited = !isFavourited;
            updateDatabaseState("favourites", isFavourited, "totalSavings", "maxSavings");
        }
        
        updateInteractionUI();
    }

    private void updateDatabaseState(String type, boolean newStatus, String countField, String statsField) {
        DatabaseReference userRef = mDatabase.child("users").child(userType).child(targetUid);
        final int increment = newStatus ? 1 : -1;

        // 1. Update interactions list
        userRef.child(type).child(currentUid).setValue(newStatus ? true : null);

        // 2. Update totals
        userRef.child(countField).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long currentCount = 0;
                if (snapshot.exists()) {
                    try {
                        Object val = snapshot.getValue();
                        if (val instanceof Long) currentCount = (Long) val;
                        else if (val instanceof Integer) currentCount = ((Integer) val).longValue();
                        else currentCount = Long.parseLong(String.valueOf(val));
                    } catch (Exception e) { currentCount = 0; }
                }
                userRef.child(countField).setValue(Math.max(0, currentCount + increment));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        // 3. Update stats for Video_Review_Page_Plus_Stats_controller (All Time / All sport)
        DatabaseReference statsRef = userRef.child("stats").child("allTime").child("All").child(statsField);
        statsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long currentStat = 0;
                if (snapshot.exists()) {
                    try {
                        Object val = snapshot.getValue();
                        if (val instanceof Long) currentStat = (Long) val;
                        else if (val instanceof Integer) currentStat = ((Integer) val).longValue();
                        else currentStat = Long.parseLong(String.valueOf(val));
                    } catch (Exception e) { currentStat = 0; }
                }
                statsRef.setValue(Math.max(0, currentStat + increment));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void loadUserProfile() {
        if (targetUid == null) return;

        mDatabase.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DataSnapshot userSnap = null;
                if (snapshot.child("Organizer").hasChild(targetUid)) {
                    userSnap = snapshot.child("Organizer").child(targetUid);
                    userType = "Organizer";
                } else if (snapshot.child("Player").hasChild(targetUid)) {
                    userSnap = snapshot.child("Player").child(targetUid);
                    userType = "Player";
                } else if (snapshot.child("Referee").hasChild(targetUid)) {
                    userSnap = snapshot.child("Referee").child(targetUid);
                    userType = "Referee";
                }

                if (userSnap != null) {
                    DataSnapshot profileSnap = userSnap.hasChild("profile") ? userSnap.child("profile") : userSnap;
                    String firstName = profileSnap.child("firstName").getValue(String.class);
                    String lastName = profileSnap.child("lastName").getValue(String.class);
                    String username = profileSnap.child("username").getValue(String.class);

                    if (tvUserName != null) tvUserName.setText(firstName + " " + lastName);
                    if (tvUsername != null) tvUsername.setText(username != null ? username : "N/A");
                    if (tvUserRole != null) tvUserRole.setText(userType);
                    if (btnAddUser != null) btnAddUser.setText("Add " + userType);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
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
                } else {
                    showTbdStats();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void showTbdStats() {
        if (tvScore != null) {
            tvScore.setText("TBD");
            tvScore.setTextColor(Color.RED);
        }
        if (statsContentPlaceholder != null) {
            statsContentPlaceholder.removeAllViews();
            View statsView = getLayoutInflater().inflate(R.layout.layout_stat_row_soccer, statsContentPlaceholder, false);
            setAllTextViewsToZero(statsView);
            statsContentPlaceholder.addView(statsView);
        }
    }

    private void setAllTextViewsToZero(View v) {
        int[] ids = {
            R.id.tvShootingLeft, R.id.tvShootingRight, R.id.tvAttacksLeft, R.id.tvAttacksRight,
            R.id.tvPossessionLeft, R.id.tvPossessionRight, R.id.tvCornersLeft, R.id.tvCornersRight,
            R.id.tvYellowCardsLeft, R.id.tvYellowCardsRight, R.id.tvRedCardsLeft, R.id.tvRedCardsRight
        };
        for (int id : ids) {
            TextView tv = v.findViewById(id);
            if (tv != null) tv.setText("0");
        }
    }

    private void displayGameStats(Game game, DataSnapshot gameSnapshot) {
        String sportType = game.getGameType();
        DataSnapshot reportSnapshot = gameSnapshot.child("matchReport");
        
        if (tvScore != null) {
            if (reportSnapshot.exists()) {
                String score = reportSnapshot.child("score").getValue(String.class);
                if (score != null && !score.isEmpty()) {
                    tvScore.setText(score);
                    tvScore.setTextColor(Color.WHITE);
                } else {
                    tvScore.setText("TBD");
                    tvScore.setTextColor(Color.RED);
                }
            } else {
                tvScore.setText("TBD");
                tvScore.setTextColor(Color.RED);
            }
        }

        if (statsContentPlaceholder == null) return;
        statsContentPlaceholder.removeAllViews();
        
        View statsView = null;
        DataSnapshot detailedStats = reportSnapshot.child("detailedStats");
        
        if ("Soccer".equalsIgnoreCase(sportType) || "Football".equalsIgnoreCase(sportType)) {
            statsView = getLayoutInflater().inflate(R.layout.layout_stat_row_soccer, statsContentPlaceholder, false);
            if (detailedStats.exists()) {
                populateFootballStats(statsView, detailedStats);
            } else {
                setAllTextViewsToZero(statsView);
            }
        }
        
        if (statsView != null) {
            statsContentPlaceholder.addView(statsView);
        }
    }

    private void populateFootballStats(View v, DataSnapshot stats) {
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

    private void setTextFromSnapshot(View v, int resId, DataSnapshot snapshot) {
        TextView tv = v.findViewById(resId);
        if (tv != null && snapshot.exists()) {
            tv.setText(String.valueOf(snapshot.getValue()));
        } else if (tv != null) {
            tv.setText("0");
        }
    }

    private void navigateToMyProfile() {
        User.getCurrentUser().thenAccept(user -> {
            runOnUiThread(() -> {
                if (user instanceof Organizer) navigateTo(General_Profile_Page_Controller.class);
                else if (user instanceof Player) navigateTo(Player_Profile_Page_Controller.class);
                else if (user instanceof Referee) navigateTo(Referee_Profile_Page_Controller.class);
            });
        });
    }

    private void navigateTo(Class<?> targetActivity) {
        try {
            Intent intent = new Intent(this, targetActivity);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Activity not yet implemented", Toast.LENGTH_SHORT).show();
        }
    }
}
