package com.example.playcheck.activityfiles;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.playcheck.R;
import com.google.firebase.auth.FirebaseAuth;

public class RefereeHomeActivity extends AppCompatActivity {

    Button btnViewMyGames;
    Button btnSchedule;
    Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_referee_home);

        btnViewMyGames = findViewById(R.id.btnRefGames);
        btnSchedule = findViewById(R.id.btnRefSchedule);
        btnLogout = findViewById(R.id.btnLogoutRef);

        btnViewMyGames.setOnClickListener(v -> {
            startActivity(new Intent(this, RefereeGamesActivity.class));
        });

        btnSchedule.setOnClickListener(v -> {
            startActivity(new Intent(this, GameSchedule.class));
        });

        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(this, Login.class));
                finish();
            });
        }
    }
}