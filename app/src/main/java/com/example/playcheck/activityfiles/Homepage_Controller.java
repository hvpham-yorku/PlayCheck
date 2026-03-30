package com.example.playcheck.activityfiles;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.playcheck.database.UserLinkToDatabase;
import com.example.playcheck.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

        // suggestion is customized according to user
        findViewById(R.id.cardSuggestion).setOnClickListener(v -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                UserLinkToDatabase.getUserAccountType(user).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String accountType = task.getResult();
                        if ("Organizer".equalsIgnoreCase(accountType)) {
                            navigateTo(CreateGameActivity.class);
                        } else if ("Player".equalsIgnoreCase(accountType) || "Referee".equalsIgnoreCase(accountType)) {
                            navigateTo(TeamStandings.class);
                        } else {
                            Toast.makeText(this, "Account type unknown", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Try another feature", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        findViewById(R.id.cardCreateEvent).setOnClickListener(v ->
                navigateTo(Event_Option_Page_Contoroller.class));

        // 2. Main Grid Buttons
        findViewById(R.id.cardAllGames).setOnClickListener(v ->
                navigateTo(GameList.class));

        findViewById(R.id.cardAllTeams).setOnClickListener(v ->{
                android.util.Log.d("NAV_TEST", "All Teams Clicked!");
                navigateTo(MyTeams.class);});

        findViewById(R.id.cardMySchedule).setOnClickListener(v ->
                navigateTo(GameSchedule.class));

        findViewById(R.id.cardStatistics).setOnClickListener(v ->
                navigateTo(Video_Review_Page_Plus_Stats_controller.class));

        // 3. New Buttons (Create Team & All Users)
        findViewById(R.id.cardCalistenia).setOnClickListener(v ->
                navigateTo(AllUsers_Controller.class));

        findViewById(R.id.cardPingPong).setOnClickListener(v -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                UserLinkToDatabase.getUserAccountType(user).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String accountType = task.getResult();
                        if ("Organizer".equalsIgnoreCase(accountType)) {
                            // Organizers create teams via the Organizer view
                            navigateTo(CreateTeamOrganizer.class);
                        } else if ("Player".equalsIgnoreCase(accountType)) {
                            // Players create teams via the Player view
                            navigateTo(CreateTeamPlayer.class);
                        } else if ("Referee".equalsIgnoreCase(accountType)) {
                            // Referees do nothing
                            Toast.makeText(this, "Referees cannot create teams", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Error verifying role", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

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
        findViewById(R.id.navProfile).setOnClickListener(v -> navigateToUserProfile());



    }

    private void navigateToUserProfile() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = user.getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("Organizer").hasChild(uid)) {
                    navigateTo(General_Profile_Page_Controller.class);
                } else if (snapshot.child("Player").hasChild(uid)) {
                    navigateTo(Player_Profile_Page_Controller.class);
                } else if (snapshot.child("Referee").hasChild(uid)) {
                    navigateTo(Referee_Profile_Page_Controller.class);
                } else {
                    Toast.makeText(Homepage_Controller.this, "Profile type not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Homepage_Controller.this, "Error fetching profile", Toast.LENGTH_SHORT).show();
            }
        });
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
