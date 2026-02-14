package com.example.playcheck;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Calendar-based game schedule view
 * Shows popup with game previews when date is clicked
 */
public class GameSchedule extends AppCompatActivity {

    private CalendarView calendarView;
    private ArrayList<Game> allGames;  // All games from Firebase
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_schedule);

        // Initialize calendar
        calendarView = findViewById(R.id.calendarView);

        // Initialize data
        allGames = new ArrayList<>();

        // Load games from Firebase
        loadGamesFromFirebase();

        // Calendar date selection listener - shows popup
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                // Convert selected date to milliseconds
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth, 0, 0, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                long selectedDateInMillis = calendar.getTimeInMillis();

                // Show popup with games for this date
                showGamesPopup(selectedDateInMillis, month, dayOfMonth);
            }
        });
    }

    /**
     * Load all games from Firebase
     */
    private void loadGamesFromFirebase() {
        Toast.makeText(this, "Loading games...", Toast.LENGTH_SHORT).show();

        databaseReference = FirebaseDatabase.getInstance()
                .getReference("games");

        // Use addListenerForSingleValueEvent instead of addValueEventListener
        // if you don't need real-time updates
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    Toast.makeText(GameSchedule.this,
                            "No games found in database",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                allGames.clear();

                // DEBUG: Check if we got any data
                System.out.println("DataSnapshot exists: " + dataSnapshot.exists());
                System.out.println("Children count: " + dataSnapshot.getChildrenCount());

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Game game = snapshot.getValue(Game.class);
                    if (game != null) {
                        allGames.add(game);
                        // DEBUG: Print each game
                        System.out.println("Loaded game: " + game.getGameName());
                    }
                }

                sortGamesByDate();

                // Show toast with count
                Toast.makeText(GameSchedule.this,
                        "Loaded " + allGames.size() + " games",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.err.println("Firebase error: " + databaseError.getMessage());
                Toast.makeText(GameSchedule.this,
                        "Failed to load games: " + databaseError.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Show popup dialog with games for selected date
     */
    private void showGamesPopup(long selectedDate, int month, int day) {
        // Filter games for this date
        ArrayList<Game> gamesOnDate = getGamesForDate(selectedDate);

        // If no games, show message
        if (gamesOnDate.isEmpty()) {
            Toast.makeText(this, "No games scheduled for this date", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create custom dialog
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.games_popup);

        // Set dialog size (85% of screen width, wrap content height)
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(
                    (int)(getResources().getDisplayMetrics().widthPixels * 0.85),
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            window.setBackgroundDrawableResource(android.R.color.transparent);
        }

        // Set date header
        TextView txtPopupDate = dialog.findViewById(R.id.txtPopupDate);
        String[] months = {"January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"};
        txtPopupDate.setText(months[month] + " " + day);

        // Get the container for game cards
        LinearLayout gamesContainer = dialog.findViewById(R.id.gamesContainer);
        gamesContainer.removeAllViews();

        // Add each game as a card
        for (Game game : gamesOnDate) {
            View gameCard = createGameCard(game);
            gamesContainer.addView(gameCard);
        }

        // Close button
        TextView btnClose = dialog.findViewById(R.id.btnClosePopup);
        btnClose.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    /**
     * Create a game card view programmatically
     */
    private View createGameCard(Game game) {
        // Inflate the card layout
        View cardView = getLayoutInflater().inflate(R.layout.item_game_schedule, null);

        // Set the game data
        TextView txtGameName = cardView.findViewById(R.id.txtGameName);
        TextView txtGameTime = cardView.findViewById(R.id.txtGameTime);
        TextView txtGameVenue = cardView.findViewById(R.id.txtGameVenue);

        txtGameName.setText(game.getGameName());

        // Extract just the time from the full date string
        String fullDate = game.getGameDateLongtoString(game.getGameDate());
        String time = fullDate.split(" - ")[0]; // Get "h:mm a" part
        txtGameTime.setText(time);

        txtGameVenue.setText(game.getGameVenue());

        // Make card clickable for future detail view
        cardView.setOnClickListener(v -> {
            // TODO: Navigate to detailed game page (your teammate's work)
            Toast.makeText(this, "Clicked: " + game.getGameName(), Toast.LENGTH_SHORT).show();
        });

        return cardView;
    }

    /**
     * Get games for specific date
     */
    /**
     * Get games for specific date - SIMPLIFIED VERSION
     */
    private ArrayList<Game> getGamesForDate(long selectedDate) {
        ArrayList<Game> gamesOnDate = new ArrayList<>();

        // Convert selected date to simple string format (YYYY-MM-DD)
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String selectedDateString = sdf.format(new Date(selectedDate));

        // Loop through all games and compare dates as strings (ignores time completely)
        for (Game game : allGames) {
            String gameDateString = sdf.format(new Date(game.getGameDate()));

            if (selectedDateString.equals(gameDateString)) {
                gamesOnDate.add(game);
                System.out.println("MATCH: " + game.getGameName() + " on " + gameDateString);
            }
        }

        return gamesOnDate;
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