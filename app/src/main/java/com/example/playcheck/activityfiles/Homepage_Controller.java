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
import com.google.firebase.auth.FirebaseAuth;

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

        setupClickListeners();
    }

    private void setupClickListeners() {
        // 1. Suggestion & Create Event
//        findViewById(R.id.cardSuggestion).setOnClickListener(v ->
//                navigateTo(SuggestionActivity.class)); // Replace with your actual Activity class
//
//        findViewById(R.id.cardCreateEvent).setOnClickListener(v ->
//                navigateTo(CreateEventActivity.class));

        // 2. Main Grid Buttons
        findViewById(R.id.cardAllGames).setOnClickListener(v ->
                navigateTo(GameList.class));

        findViewById(R.id.cardAllTeams).setOnClickListener(v ->
                navigateTo(MyTeams.class));

        findViewById(R.id.cardMySchedule).setOnClickListener(v ->
                navigateTo(GameSchedule.class));

        findViewById(R.id.cardStatistics).setOnClickListener(v ->
                navigateTo(Video_Review_Page_Plus_Stats_controller.class));

        // 3. New Buttons (Create Team & All Users)
        findViewById(R.id.cardCalistenia).setOnClickListener(v ->
                navigateTo(AllUsers_Controller.class));

        findViewById(R.id.cardPingPong).setOnClickListener(v ->
                navigateTo(CreateTeam.class));

        // 4. Logout Logic
        findViewById(R.id.ivLogout).setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, Login_Controller.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });



        // 5. Bottom Navigation
        findViewById(R.id.navHome).setOnClickListener(v -> handleNavClick("Home"));
        findViewById(R.id.navNews).setOnClickListener(v -> handleNavClick("Notifications"));
        findViewById(R.id.navClipboard).setOnClickListener(v -> navigateTo(Video_Review_Page_Plus_Stats_controller.class));
        findViewById(R.id.navBack).setOnClickListener(v -> finish());
        findViewById(R.id.navProfile).setOnClickListener(v -> handleNavClick("Profile"));

    }

    /**
     * Helper method to handle standard navigation intents
     */
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