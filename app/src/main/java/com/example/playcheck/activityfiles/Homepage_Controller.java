package com.example.playcheck.activityfiles;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.playcheck.R;
import com.google.android.material.card.MaterialCardView;

public class Homepage_Controller extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_homepage);
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize and setup click listeners
        setupClickListeners();
    }

    private void setupClickListeners() {
        // Find views by ID
        MaterialCardView cardSuggestion = findViewById(R.id.cardSuggestion);
        MaterialCardView cardCreateEvent = findViewById(R.id.cardCreateEvent);
        MaterialCardView cardAllGames = findViewById(R.id.cardAllGames);
        MaterialCardView cardAllTeams = findViewById(R.id.cardAllTeams);
        MaterialCardView cardMySchedule = findViewById(R.id.cardMySchedule);
        MaterialCardView cardStatistics = findViewById(R.id.cardStatistics);
        View ivLogout = findViewById(R.id.ivLogout);

        // Set Click Listeners
        cardSuggestion.setOnClickListener(v -> handleCardClick("Suggestion"));
        cardCreateEvent.setOnClickListener(v -> handleCardClick("Create Event"));
        cardAllGames.setOnClickListener(v -> handleCardClick("All Games"));
        cardAllTeams.setOnClickListener(v -> handleCardClick("All Teams"));
        cardMySchedule.setOnClickListener(v -> handleCardClick("My Schedule"));
        cardStatistics.setOnClickListener(v -> handleCardClick("Statistics"));
        
        ivLogout.setOnClickListener(v -> {
            Toast.makeText(this, "Logging out...", Toast.LENGTH_SHORT).show();
            // Implement logout logic here (e.g., FirebaseAuth.getInstance().signOut())
        });

        // Bottom Navigation Listeners
        findViewById(R.id.navHome).setOnClickListener(v -> handleNavClick("Home"));
        findViewById(R.id.navNews).setOnClickListener(v -> handleNavClick("News"));
        findViewById(R.id.navClipboard).setOnClickListener(v -> handleNavClick("Clipboard"));
        findViewById(R.id.navBack).setOnClickListener(v -> finish());
        findViewById(R.id.navProfile).setOnClickListener(v -> handleNavClick("Profile"));
    }

    private void handleCardClick(String cardName) {
        Toast.makeText(this, "Clicked: " + cardName, Toast.LENGTH_SHORT).show();
        // Here you would normally start a new activity:
        // Intent intent = new Intent(this, TargetActivity.class);
        // startActivity(intent);
    }

    private void handleNavClick(String navItem) {
        Toast.makeText(this, "Navigating to: " + navItem, Toast.LENGTH_SHORT).show();
    }
}