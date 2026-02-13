package com.example.playcheck;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Chronological schedule of games
 */
public class GameSchedule extends AppCompatActivity {

    private RecyclerView recyclerView;
    private GameScheduleView adapter;
    private ArrayList<GameListPlayerInfo> gamesList;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_schedule);

        // Initialize views
        recyclerView = findViewById(R.id.recyclerViewSchedule);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize data
        gamesList = new ArrayList<>();
        adapter = new GameScheduleView(this, gamesList);
        recyclerView.setAdapter(adapter);

        // Load games from Firebase
        loadGamesFromFirebase();
    }

    /**
     * Load games from Firebase and sort by date/time
     */
    private void loadGamesFromFirebase() {
        databaseReference = FirebaseDatabase.getInstance().getReference("games");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                gamesList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    GameListPlayerInfo game = snapshot.getValue(GameListPlayerInfo.class);
                    if (game != null) {
                        gamesList.add(game);
                    }
                }

                // Sort games by date (upcoming first)
                sortGamesByDate();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    /**
     * Sorts games chronologically
     * You'll need to add date/time fields to GameListPlayerInfo
     */
    private void sortGamesByDate() {
        Collections.sort(gamesList, new Comparator<GameListPlayerInfo>() {
            @Override
            public int compare(GameListPlayerInfo g1, GameListPlayerInfo g2) {
                // TODO: Compare based on actual date fields
                // For now, this is a placeholder
                // You'll need: g1.getDateTime().compareTo(g2.getDateTime())
                return 0;
            }
        });
    }
}