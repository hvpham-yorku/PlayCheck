package com.example.playcheck.database;

import androidx.annotation.NonNull;

import com.example.playcheck.activityfiles.AdapterGameList;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class GameLinkToDatabase {
    
    private DatabaseReference databaseRef;

    public GameLinkToDatabase() {
        // Lazy initialization to avoid issues in unit tests
    }

    public GameLinkToDatabase(DatabaseReference databaseRef) {
        this.databaseRef = databaseRef;
    }

    protected DatabaseReference getDatabaseRef() {
        if (databaseRef == null) {
            databaseRef = FirebaseDatabase.getInstance().getReference().child("games");
        }
        return databaseRef;
    }

    public interface OnRefereesFetchedListener {
        void onRefereesFetched(ArrayList<String> refereeIds, ArrayList<String> refereeNames);
    }

    /**
     * Fetches all games from the database.
     */
    public CompletableFuture<List<Game>> getAllGames() {
        CompletableFuture<List<Game>> future = new CompletableFuture<>();
        getDatabaseRef().addListenerForSingleValueEvent(new ValueEventListener() {
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
            id = getDatabaseRef().push().getKey();
            game.setGameId(id);
        }

        getDatabaseRef().child(id).setValue(game).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                future.complete(null);
            } else {
                future.completeExceptionally(task.getException());
            }
        });
        return future;
    }

    /**
     * Creates a new game and saves it to the database.
     */
    public void createGame(String teamAid, String teamBid, String teamA, String teamB, String venue, String type, long date, ArrayList<String> playerIds, ArrayList<String> playerNames, ArrayList<String> refIds, ArrayList<String> refNames, OnCompleteListener<Void> listener) {
        Map<String, String> players = new HashMap<>();
        if (playerIds != null && playerNames != null) {
            for (int i = 0; i < Math.min(playerIds.size(), playerNames.size()); i++) {
                players.put(playerIds.get(i), playerNames.get(i));
            }
        }

        Map<String, String> referees = new HashMap<>();
        if (refIds != null && refNames != null) {
            for (int i = 0; i < Math.min(refIds.size(), refNames.size()); i++) {
                referees.put(refIds.get(i), refNames.get(i));
            }
        }

        String creatorId = FirebaseAuth.getInstance().getCurrentUser() != null ? FirebaseAuth.getInstance().getCurrentUser().getUid() : "";

        Game game = new Game(teamA, teamB, date, venue, type, players, creatorId, teamAid, teamBid, referees, "0-0");
        
        String id = getDatabaseRef().push().getKey();
        if (id != null) {
            game.setGameId(id);
            getDatabaseRef().child(id).setValue(game).addOnCompleteListener(listener);
        }
    }

    /* Update specific fields for an existing game */
    public void updateGameDetails(String gameId, Map<String, Object> updates, OnCompleteListener<Void> listener) {
        getDatabaseRef().child(gameId).updateChildren(updates).addOnCompleteListener(listener);
    }

    /**
     * Fetches a specific game by ID.
     */
    public CompletableFuture<Game> getGameById(String gameId) {
        CompletableFuture<Game> future = new CompletableFuture<>();
        getDatabaseRef().child(gameId).addListenerForSingleValueEvent(new ValueEventListener() {
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

    /**
     * Fetches referees for a game by game ID.
     */
    public void getRefNamesFromGame(String gameId, OnRefereesFetchedListener listener) {
        if (gameId == null || gameId.isEmpty()) {
            listener.onRefereesFetched(new ArrayList<>(), new ArrayList<>());
            return;
        }
        getGameById(gameId).thenAccept(game -> {
            ArrayList<String> refereeIds = new ArrayList<>();
            ArrayList<String> refereeNames = new ArrayList<>();
            if (game != null && game.getReferees() != null) {
                for (Map.Entry<String, String> entry : game.getReferees().entrySet()) {
                    refereeIds.add(entry.getKey());
                    refereeNames.add(entry.getValue());
                }
            }
            listener.onRefereesFetched(refereeIds, refereeNames);
        }).exceptionally(ex -> {
            listener.onRefereesFetched(new ArrayList<>(), new ArrayList<>());
            return null;
        });
    }

    /**
     * Observes the match report for a game.
     */
    public void observeMatchReport(String gameId, Consumer<MatchReport> callback) {
        getDatabaseRef().child(gameId).child("matchReport").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                MatchReport report = snapshot.getValue(MatchReport.class);
                callback.accept(report);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    /**
     * Fetches team IDs for a specific game.
     */
    public void getTeamIdsFromGame(String gameId, BiConsumer<String, String> callback) {
        getGameById(gameId).thenAccept(game -> {
            if (game != null) {
                callback.accept(game.getTeamAid(), game.getTeamBid());
            }
        });
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
