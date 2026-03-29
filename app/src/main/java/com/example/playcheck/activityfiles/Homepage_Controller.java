package com.example.playcheck.activityfiles;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.playcheck.R;
import com.example.playcheck.puremodel.Organizer;
import com.example.playcheck.puremodel.Player;
import com.example.playcheck.puremodel.Referee;
import com.example.playcheck.puremodel.User;

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
        findViewById(R.id.cardCreateEvent).setOnClickListener(v ->
                navigateTo(Event_Option_Page_Contoroller.class));

        findViewById(R.id.cardAllGames).setOnClickListener(v ->
                navigateTo(GameList.class));

        findViewById(R.id.cardAllTeams).setOnClickListener(v ->
                navigateTo(MyTeams.class));

        findViewById(R.id.cardMySchedule).setOnClickListener(v ->
                navigateTo(GameSchedule.class));

        findViewById(R.id.cardStatistics).setOnClickListener(v ->
                navigateTo(Video_Review_Page_Plus_Stats_controller.class));

        findViewById(R.id.cardCalistenia).setOnClickListener(v ->
                navigateTo(AllUsers_Controller.class));

        findViewById(R.id.cardPingPong).setOnClickListener(v ->
                navigateTo(CreateTeam.class));

        findViewById(R.id.ivLogout).setOnClickListener(v -> {
            User.logout();
            Intent intent = new Intent(this, Login_Controller.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        findViewById(R.id.navHome).setOnClickListener(v -> handleNavClick("Home"));
        findViewById(R.id.navNews).setOnClickListener(v -> handleNavClick("Notifications"));
        findViewById(R.id.navClipboard).setOnClickListener(v -> navigateTo(Video_Review_Page_Plus_Stats_controller.class));
        findViewById(R.id.navBack).setOnClickListener(v -> finish());
        findViewById(R.id.navProfile).setOnClickListener(v -> navigateToUserProfile());
    }

    private void navigateToUserProfile() {
        User.getCurrentUser().thenAccept(user -> {
            if (user == null) {
                runOnUiThread(() -> Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show());
                return;
            }
            runOnUiThread(() -> {
                if (user instanceof Organizer) {
                    navigateTo(General_Profile_Page_Controller.class);
                } else if (user instanceof Player) {
                    navigateTo(Player_Profile_Page_Controller.class);
                } else if (user instanceof Referee) {
                    navigateTo(Referee_Profile_Page_Controller.class);
                }
            });
        }).exceptionally(throwable -> {
            runOnUiThread(() -> Toast.makeText(this, "Error fetching profile", Toast.LENGTH_SHORT).show());
            return null;
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

    private void handleNavClick(String navItem) {
        Toast.makeText(this, "You are already on " + navItem, Toast.LENGTH_SHORT).show();
    }
}
