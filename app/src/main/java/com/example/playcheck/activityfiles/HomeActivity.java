package com.example.playcheck.activityfiles;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.playcheck.R;
import com.example.playcheck.puremodel.Game;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

/**
 * Home screen showing announcements, next match, last match, and standings
 * Converted from Figma React design to Android
 */
public class HomeActivity extends AppCompatActivity {
    
    // Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference gamesRef;
    private ArrayList<Game> allGames;
    private boolean useRealData = true; // Set to false to use dummy data
    
    // UI Elements
    private TextView btnMenu, btnNotifications, btnFullTable;
    private CardView cardNextMatch, cardLastMatch;
    private LinearLayout announcementsContainer;
    
    // Next Match
    private TextView txtNextMatchTeamA, txtNextMatchTeamB;
    private TextView txtNextMatchTime, txtNextMatchVenue;
    
    // Last Match  
    private TextView txtLastMatchTeamA, txtLastMatchTeamB;
    private TextView txtLastMatchScore, txtLastMatchDate, txtLastMatchVenue;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        
        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        gamesRef = FirebaseDatabase.getInstance().getReference("games");
        allGames = new ArrayList<>();
        
        // Initialize UI
        initializeViews();
        setupClickListeners();
        
        // Load data
        if (useRealData) {
            loadGamesFromFirebase();
        } else {
            loadDummyData();
        }
    }
    
    private void initializeViews() {
        // Header buttons (now TextViews with emojis)
        btnMenu = findViewById(R.id.btnMenu);
        btnNotifications = findViewById(R.id.btnNotifications);
        btnFullTable = findViewById(R.id.btnFullTable);
        
        // Announcements
        announcementsContainer = findViewById(R.id.announcementsContainer);
        
        // Next Match card
        cardNextMatch = findViewById(R.id.cardNextMatch);
        txtNextMatchTeamA = findViewById(R.id.txtNextMatchTeamA);
        txtNextMatchTeamB = findViewById(R.id.txtNextMatchTeamB);
        txtNextMatchTime = findViewById(R.id.txtNextMatchTime);
        txtNextMatchVenue = findViewById(R.id.txtNextMatchVenue);
        
        // Last Match card
        cardLastMatch = findViewById(R.id.cardLastMatch);
        txtLastMatchTeamA = findViewById(R.id.txtLastMatchTeamA);
        txtLastMatchTeamB = findViewById(R.id.txtLastMatchTeamB);
        txtLastMatchScore = findViewById(R.id.txtLastMatchScore);
        txtLastMatchDate = findViewById(R.id.txtLastMatchDate);
        txtLastMatchVenue = findViewById(R.id.txtLastMatchVenue);
        
        // Add static announcements
        addAnnouncements();
    }
    
    private void setupClickListeners() {
        btnMenu.setOnClickListener(v -> {
            // TODO: Open navigation drawer
            Toast.makeText(this, "Menu - Coming Soon", Toast.LENGTH_SHORT).show();
        });
        
        btnNotifications.setOnClickListener(v -> {
            // TODO: Open notifications
            Toast.makeText(this, "Notifications - Coming Soon", Toast.LENGTH_SHORT).show();
        });
        
        cardNextMatch.setOnClickListener(v -> {
            Toast.makeText(this, "View match details", Toast.LENGTH_SHORT).show();
        });
        
        findViewById(R.id.btnViewSchedule).setOnClickListener(v -> {
            startActivity(new Intent(this, GameSchedule.class));
        });
        
        findViewById(R.id.btnFullTable).setOnClickListener(v -> {
            Toast.makeText(this, "Full standings table - Coming Soon", Toast.LENGTH_SHORT).show();
        });
    }
    
    /**
     * Load games from Firebase
     */
    private void loadGamesFromFirebase() {
        gamesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    // Fallback to dummy data if Firebase is empty
                    loadDummyData();
                    return;
                }
                
                allGames.clear();
                
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Game game = snapshot.getValue(Game.class);
                    if (game != null) {
                        allGames.add(game);
                    }
                }
                
                sortGamesByDate();
                updateMatchCards();
            }
            
            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(HomeActivity.this, 
                    "Failed to load games", Toast.LENGTH_SHORT).show();
                // Fallback to dummy data
                loadDummyData();
            }
        });
    }
    
    /**
     * Load dummy data for testing when Firebase is not available
     */
    private void loadDummyData() {
        allGames.clear();
        
        // Create dummy past games
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -3); // 3 days ago
        allGames.add(new Game("Wave Riders", "Thunder Bolts", 
            cal.getTimeInMillis(), "Central Volleyball Complex", "League"));
        
        // Create dummy future games
        cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 1); // Tomorrow
        cal.set(Calendar.HOUR_OF_DAY, 18);
        cal.set(Calendar.MINUTE, 0);
        allGames.add(new Game("Wave Riders", "Storm Chasers", 
            cal.getTimeInMillis(), "Downtown Sports Arena", "League"));
        
        cal.add(Calendar.DAY_OF_MONTH, 3); // 4 days from now
        allGames.add(new Game("Wave Riders", "Lightning Squad", 
            cal.getTimeInMillis(), "Main Gym", "Tournament"));
        
        sortGamesByDate();
        updateMatchCards();
        
        Toast.makeText(this, "Using demo data - Connect Firebase for real games", 
            Toast.LENGTH_LONG).show();
    }
    
    /**
     * Add static announcements to the announcements section
     */
    private void addAnnouncements() {
        // Announcement 1
        View announcement1 = getLayoutInflater().inflate(R.layout.item_announcement, 
            announcementsContainer, false);
        TextView title1 = announcement1.findViewById(R.id.txtAnnouncementTitle);
        TextView desc1 = announcement1.findViewById(R.id.txtAnnouncementDesc);
        title1.setText("Summer League Finals - March 15");
        desc1.setText("The championship match will be held at Central Sports Arena");
        announcementsContainer.addView(announcement1);
        
        // Announcement 2
        View announcement2 = getLayoutInflater().inflate(R.layout.item_announcement, 
            announcementsContainer, false);
        TextView title2 = announcement2.findViewById(R.id.txtAnnouncementTitle);
        TextView desc2 = announcement2.findViewById(R.id.txtAnnouncementDesc);
        title2.setText("New Training Sessions Available");
        desc2.setText("Sign up for advanced skills training every Tuesday");
        announcementsContainer.addView(announcement2);
    }
    
    /**
     * Update next match and last match cards
     */
    private void updateMatchCards() {
        long currentTime = System.currentTimeMillis();
        Game nextGame = null;
        Game lastGame = null;
        
        // Find next upcoming game
        for (Game game : allGames) {
            if (game.getGameDate() > currentTime) {
                nextGame = game;
                break;
            }
        }
        
        // Find last completed game (search backwards)
        for (int i = allGames.size() - 1; i >= 0; i--) {
            Game game = allGames.get(i);
            if (game.getGameDate() <= currentTime) {
                lastGame = game;
                break;
            }
        }
        
        // Update Next Match UI
        if (nextGame != null) {
            txtNextMatchTeamA.setText(nextGame.getTeamA());
            txtNextMatchTeamB.setText(nextGame.getTeamB());
            txtNextMatchTime.setText(formatMatchTime(nextGame.getGameDate()));
            txtNextMatchVenue.setText(nextGame.getGameVenue());
            cardNextMatch.setVisibility(View.VISIBLE);
        } else {
            cardNextMatch.setVisibility(View.GONE);
        }
        
        // Update Last Match UI
        if (lastGame != null) {
            txtLastMatchTeamA.setText(lastGame.getTeamA());
            txtLastMatchTeamB.setText(lastGame.getTeamB());
            txtLastMatchDate.setText(lastGame.getGameDateLongtoString(lastGame.getGameDate()));
            txtLastMatchVenue.setText(lastGame.getGameVenue());
            
            // Dummy score (you'll need to add score fields to Game model)
            txtLastMatchScore.setText("3 - 1");
            
            cardLastMatch.setVisibility(View.VISIBLE);
        } else {
            cardLastMatch.setVisibility(View.GONE);
        }
    }
    
    /**
     * Format game time for "Next Match" display
     */
    private String formatMatchTime(long gameDate) {
        Calendar cal = Calendar.getInstance();
        Calendar today = Calendar.getInstance();
        cal.setTimeInMillis(gameDate);
        
        // Check if tomorrow
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DAY_OF_MONTH, 1);
        
        if (cal.get(Calendar.DAY_OF_YEAR) == tomorrow.get(Calendar.DAY_OF_YEAR) &&
            cal.get(Calendar.YEAR) == tomorrow.get(Calendar.YEAR)) {
            return "Tomorrow, " + String.format("%d:%02d %s",
                cal.get(Calendar.HOUR) == 0 ? 12 : cal.get(Calendar.HOUR),
                cal.get(Calendar.MINUTE),
                cal.get(Calendar.AM_PM) == Calendar.AM ? "AM" : "PM");
        } else {
            // Just use the default format
            Game temp = new Game();
            return temp.getGameDateLongtoString(gameDate);
        }
    }
    
    /**
     * Sort games chronologically
     */
    private void sortGamesByDate() {
        Collections.sort(allGames, new Comparator<Game>() {
            @Override
            public int compare(Game g1, Game g2) {
                return Long.compare(g1.getGameDate(), g2.getGameDate());
            }
        });
    }
}
