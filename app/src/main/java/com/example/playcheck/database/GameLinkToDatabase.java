package com.example.playcheck.database;

import androidx.annotation.NonNull;

import com.example.playcheck.activityfiles.AdapterGameList;
import com.example.playcheck.puremodel.Game;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class GameLinkToDatabase {

    /* Reusable method that gets game data from the database based on a Query object */
    public static void getGameData(Query gamedata, ArrayList<Game> games, AdapterGameList adapter) {
        gamedata.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                games.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Game info = dataSnapshot.getValue(Game.class);

                    if (info != null) {
                        info.setGameId(dataSnapshot.getKey());
                        games.add(info);
                    }
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    /* Update specific fields for an existing game */
    public static Task<Void> updateGameDetails(String gameId, Map<String, Object> updates) {
        DatabaseReference gameRef = FirebaseDatabase.getInstance()
                .getReference("games")
                .child(gameId);

        return gameRef.updateChildren(updates);
    }
}