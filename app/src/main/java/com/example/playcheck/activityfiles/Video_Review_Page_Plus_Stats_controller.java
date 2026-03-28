package com.example.playcheck.activityfiles;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.playcheck.R;
import com.example.playcheck.puremodel.UserStatsSnapshot;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Video_Review_Page_Plus_Stats_controller extends AppCompatActivity {

    private AutoCompleteTextView sportTypeSpinner;
    private MaterialButtonToggleGroup toggleGroup;
    private TextView tvLikes, tvDislikes, tvSaved;
    private MaterialButton btnDaily, btnWeekly, btnMonthly, btnAllTime;
    
    private DatabaseReference mDatabase;
    private String currentUid;
    private String currentUserRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_video_review_page_plus_stats);
        
        mDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            currentUid = user.getUid();
            determineUserRoleAndLoadStats();
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupSportTypeSpinner();
        setupToggleGroup();
        setupClickListeners();
    }

    private void determineUserRoleAndLoadStats() {
        mDatabase.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("Player").hasChild(currentUid)) {
                    currentUserRole = "Player";
                } else if (snapshot.child("Organizer").hasChild(currentUid)) {
                    currentUserRole = "Organizer";
                } else if (snapshot.child("Referee").hasChild(currentUid)) {
                    currentUserRole = "Referee";
                }
                
                if (currentUserRole != null) {
                    updateStats(toggleGroup.getCheckedButtonId());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void initViews() {
        sportTypeSpinner = findViewById(R.id.sport_type_spinner);
        toggleGroup = findViewById(R.id.toggleGroup);
        tvLikes = findViewById(R.id.tv_score);
        tvDislikes = findViewById(R.id.tv_steps);
        tvSaved = findViewById(R.id.tv_calories);
        
        btnDaily = findViewById(R.id.btn_daily);
        btnWeekly = findViewById(R.id.btn_weekly);
        btnMonthly = findViewById(R.id.btn_monthly);
        btnAllTime = findViewById(R.id.btn_all_time);
    }

    private void setupSportTypeSpinner() {
        String[] sportsArray = {"All", "Football", "Basketball", "Tennis", "Volleyball", "Running"};
        List<String> sports = new ArrayList<>(Arrays.asList(sportsArray));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, sports);
        sportTypeSpinner.setAdapter(adapter);
        sportTypeSpinner.setText(sports.get(0), false);
        
        sportTypeSpinner.setOnItemClickListener((parent, view, position, id) -> {
            updateStats(toggleGroup.getCheckedButtonId());
        });
    }

    private void setupToggleGroup() {
        toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                btnDaily.setBackgroundColor(getResources().getColor(android.R.color.white, getTheme()));
                btnWeekly.setBackgroundColor(getResources().getColor(android.R.color.white, getTheme()));
                btnMonthly.setBackgroundColor(getResources().getColor(android.R.color.white, getTheme()));
                btnAllTime.setBackgroundColor(getResources().getColor(android.R.color.white, getTheme()));

                MaterialButton checkedButton = findViewById(checkedId);
                checkedButton.setBackgroundColor(0xFFD4E157);

                updateStats(checkedId);
            }
        });
    }

    private void updateStats(int checkedId) {
        if (currentUid == null || currentUserRole == null) return;

        String period;
        if (checkedId == R.id.btn_daily) period = "daily";
        else if (checkedId == R.id.btn_weekly) period = "weekly";
        else if (checkedId == R.id.btn_monthly) period = "monthly";
        else period = "allTime";

        String selectedSport = sportTypeSpinner.getText().toString();

        mDatabase.child("users").child(currentUserRole).child(currentUid).child("stats").child(period)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int totalLikes = 0;
                        int totalDislikes = 0;
                        int totalSaved = 0;

                        if (selectedSport.equals("All") || selectedSport.isEmpty()) {
                            // Pool all sports
                            for (DataSnapshot sportSnapshot : snapshot.getChildren()) {
                                UserStatsSnapshot stats = sportSnapshot.getValue(UserStatsSnapshot.class);
                                if (stats != null) {
                                    totalLikes += stats.getMaxLikes();
                                    totalDislikes += stats.getMaxDislikes();
                                    totalSaved += stats.getMaxSavings();
                                }
                            }
                        } else {
                            // Specific sport
                            DataSnapshot sportSnapshot = snapshot.child(selectedSport);
                            UserStatsSnapshot stats = sportSnapshot.getValue(UserStatsSnapshot.class);
                            if (stats != null) {
                                totalLikes = stats.getMaxLikes();
                                totalDislikes = stats.getMaxDislikes();
                                totalSaved = stats.getMaxSavings();
                            }
                        }

                        tvLikes.setText(String.valueOf(totalLikes));
                        tvDislikes.setText(String.valueOf(totalDislikes));
                        tvSaved.setText(String.valueOf(totalSaved));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    private void setupClickListeners() {
        findViewById(R.id.navHome).setOnClickListener(v -> navigateTo(Homepage_Controller.class));
        findViewById(R.id.navNews).setOnClickListener(v -> navigateTo(GameList.class));
        findViewById(R.id.navClipboard).setOnClickListener(v -> handleNavClick("Stats"));
        findViewById(R.id.navBack).setOnClickListener(v -> finish());
        findViewById(R.id.navProfile).setOnClickListener(v -> navigateToProfile());
    }

    private void navigateToProfile() {
        if ("Player".equals(currentUserRole)) navigateTo(Player_Profile_Page_Controller.class);
        else if ("Organizer".equals(currentUserRole)) navigateTo(Organizer_Profile_Page_Controller.class);
        else if ("Referee".equals(currentUserRole)) navigateTo(Referee_Profile_Page_Controller.class);
    }

    private void navigateTo(Class<?> targetActivity) {
        try {
            Intent intent = new Intent(this, targetActivity);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Activity not yet implemented", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleNavClick(String navItem) {
        Toast.makeText(this, "You are already on " + navItem, Toast.LENGTH_SHORT).show();
    }
}
