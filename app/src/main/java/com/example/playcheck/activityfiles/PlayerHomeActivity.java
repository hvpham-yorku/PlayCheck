package com.example.playcheck.activityfiles;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.playcheck.R;
import com.example.playcheck.puremodel.Game;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PlayerHomeActivity extends AppCompatActivity {

    private RecyclerView recyclerPreview;
    private ArrayList<Game> previewGames;
    private AdapterGameListPlayer previewAdapter;
    private DatabaseReference gamesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_home);

        Button btnViewGames = findViewById(R.id.btnPlayerViewGames);
        Button btnSchedule = findViewById(R.id.btnPlayerSchedule);

        recyclerPreview = findViewById(R.id.recyclerPreview);
        recyclerPreview.setLayoutManager(new LinearLayoutManager(this));

        previewGames = new ArrayList<>();
        previewAdapter = new AdapterGameListPlayer(this, previewGames);
        recyclerPreview.setAdapter(previewAdapter);

        // Button opens FULL game list page
        btnViewGames.setOnClickListener(v -> {
            startActivity(new Intent(this, GameListPlayer.class));
        });

        // Button opens schedule page
        btnSchedule.setOnClickListener(v -> {
            startActivity(new Intent(this, GameSchedule.class));
        });

        // Load preview: upcoming games only (limit to like 3)
        gamesRef = FirebaseDatabase.getInstance().getReference("games");
        loadUpcomingPreview();
    }

    private void loadUpcomingPreview() {
        gamesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                previewGames.clear();

                long now = System.currentTimeMillis();

                for (DataSnapshot child : snapshot.getChildren()) {
                    Game g = child.getValue(Game.class);
                    if (g != null && g.getGameDate() >= now) {
                        previewGames.add(g);
                    }
                }

                // Optional: limit preview to 3 items
                if (previewGames.size() > 3) {
                    previewGames.subList(3, previewGames.size()).clear();
                }

                previewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }
}