package com.example.playcheck.activityfiles;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.playcheck.R;

public class OrganizerHomeActivity extends AppCompatActivity implements View.OnClickListener {

    // UI Components
    Button manageLeaguesButton, manageTeamsButton, assignRefereesButton, logoutButton;
    TextView welcomeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_home);

        // Initialize UI components
        welcomeText = findViewById(R.id.welcomeText);
        manageLeaguesButton = findViewById(R.id.manageLeaguesButton);
        manageTeamsButton = findViewById(R.id.manageTeamsButton);
        assignRefereesButton = findViewById(R.id.assignRefereesButton);
        logoutButton = findViewById(R.id.logoutButton);

        // Set up listeners
        manageLeaguesButton.setOnClickListener(this);
        manageTeamsButton.setOnClickListener(this);
        assignRefereesButton.setOnClickListener(this);
        logoutButton.setOnClickListener(this);

        // Display welcome message
        String email = getIntent().getStringExtra("userEmail");
        welcomeText.setText("Welcome, " + email);
    }

    @Override
    public void onClick(View view) {
        // Handle button clicks
        int id = view.getId();

        if (id == R.id.manageLeaguesButton) {
            // Navigate to manage leagues activity
            // startActivity(new Intent(this, ManageLeaguesActivity.class));

        } else if (id == R.id.manageTeamsButton) {
            // Navigate to manage teams activity
            // startActivity(new Intent(this, ManageTeamsActivity.class));

        } else if (id == R.id.assignRefereesButton) {
            // Navigate to assign referees activity
            // startActivity(new Intent(this, AssignRefereesActivity.class));

        } else if (id == R.id.logoutButton) {
            // Handle logout
            // FirebaseAuth.getInstance().signOut();
            // startActivity(new Intent(this, LoginActivity.class));
            // finish();
        }
    }
}
