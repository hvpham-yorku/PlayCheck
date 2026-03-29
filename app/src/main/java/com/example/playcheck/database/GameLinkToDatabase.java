package com.example.playcheck.Database;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.playcheck.activityfiles.AdapterGameList;
import com.example.playcheck.activityfiles.CreateGameActivity;
import com.example.playcheck.puremodel.Game;
import com.example.playcheck.puremodel.MatchReport;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class GameLinkToDatabase {
    
    private DatabaseReference databaseRef;

    public GameLinkToDatabase() {
        this.databaseRef = FirebaseDatabase.getInstance().getReference().child("games");
    }

    /**
     * Fetches all games from the database.
     */
    public CompletableFuture<List<Game>> getAllGames() {
        CompletableFuture<List<Game>> future = new CompletableFuture<>();
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Game> games = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Game game = dataSnapshot.getValue(Game.class);
                    if (game != null) {
                        game.setGameId(dataSnapshot.getKey());
                        games.add(game);
                    }
                }
                future.complete(games);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                future.completeExceptionally(error.toException());
            }
        });
        return future;
    }

    /**
     * Saves a new game or updates an existing one.
     */
    public CompletableFuture<Void> saveGame(Game game) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        String id = game.getGameId();
        if (id == null || id.isEmpty()) {
            id = databaseRef.push().getKey();
            game.setGameId(id);
        }

        databaseRef.child(id).setValue(game).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                future.complete(null);
            } else {
                future.completeExceptionally(task.getException());
            }
        });
        return future;
    }

    /**
     * Fetches a specific game by ID.
     */
    public CompletableFuture<Game> getGameById(String gameId) {
        CompletableFuture<Game> future = new CompletableFuture<>();
        databaseRef.child(gameId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Game game = snapshot.getValue(Game.class);
                if (game != null) {
                    game.setGameId(snapshot.getKey());
                    future.complete(game);
                } else {
                    future.completeExceptionally(new Exception("Game not found"));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                future.completeExceptionally(error.toException());
            }
        });
        return future;
    }

    public static void getGameData(Query query, ArrayList<Game> games, AdapterGameList adapter) {
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                games.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Game game = dataSnapshot.getValue(Game.class);
                    if (game != null) {
                        games.add(game);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public static void getGameData(DatabaseReference databaseReference, ArrayList<Game> games, AdapterGameList adapter) {
        getGameData((Query) databaseReference, games, adapter);
    }
}
