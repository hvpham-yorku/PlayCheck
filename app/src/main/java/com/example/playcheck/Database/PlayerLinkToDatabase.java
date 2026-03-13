package com.example.playcheck.database;

import com.example.playcheck.puremodel.Game;
import com.example.playcheck.puremodel.Team;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PlayerLinkToDatabase extends UserLinkToDatabase {

    public PlayerLinkToDatabase() {
        super();
    }

    //-------------------------------------------------------------------------------------------
    // Player-specific Database Operations
    //-------------------------------------------------------------------------------------------

    /**
     * Add player to a team
     */
    public CompletableFuture<Void> addPlayerToTeam(String playerUid, String teamId) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        Map<String, Object> updates = new HashMap<>();
        updates.put("players/" + playerUid + "/teamId", teamId);
        updates.put("teams/" + teamId + "/members/" + playerUid, true);

        databaseRef.getRoot().updateChildren(updates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        future.complete(null);
                    } else {
                        future.completeExceptionally(task.getException());
                    }
                });

        return future;
    }

    public static String getCurrentUserId(FirebaseAuth mAuth) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            return currentUser.getUid();
        } else {
            return null;
        }
    }


    /**
     * Remove player from team
     */
    public CompletableFuture<Void> removePlayerFromTeam(String playerUid, String teamId) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        Map<String, Object> updates = new HashMap<>();
        updates.put("players/" + playerUid + "/teamId", null);
        updates.put("teams/" + teamId + "/members/" + playerUid, null);

        databaseRef.getRoot().updateChildren(updates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        future.complete(null);
                    } else {
                        future.completeExceptionally(task.getException());
                    }
                });

        return future;
    }

    /**
     * Get player's team information
     */
    public CompletableFuture<Team> getPlayerTeam(String playerUid) {
        CompletableFuture<Team> future = new CompletableFuture<>();

        databaseRef.child("players")
                .child(playerUid)
                .child("teamId")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        String teamId = snapshot.getValue(String.class);
                        if (teamId != null) {
                            // Fetch team details
                            databaseRef.child("teams")
                                    .child(teamId)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot teamSnapshot) {
                                            Team team = teamSnapshot.getValue(Team.class);
                                            if (team != null) {
                                                team.setTeamId(teamId);
                                                future.complete(team);
                                            } else {
                                                future.complete(null);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError error) {
                                            future.completeExceptionally(error.toException());
                                        }
                                    });
                        } else {
                            future.complete(null);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        future.completeExceptionally(error.toException());
                    }
                });

        return future;
    }

    /**
     * Update player statistics
     */
    public CompletableFuture<Void> updatePlayerStats(String playerUid, Map<String, Object> stats) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        databaseRef.child("players")
                .child(playerUid)
                .child("statistics")
                .updateChildren(stats)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        future.complete(null);
                    } else {
                        future.completeExceptionally(task.getException());
                    }
                });

        return future;
    }

    /**
     * Get player's match history
     */
    public CompletableFuture<List<Game>> getPlayerMatchHistory(String playerUid) {
        CompletableFuture<List<Game>> future = new CompletableFuture<>();
        List<Game> games = new ArrayList<>();

        databaseRef.child("players")
                .child(playerUid)
                .child("matches")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        for (DataSnapshot matchSnapshot : snapshot.getChildren()) {
                            Game game = matchSnapshot.getValue(Game.class);
                            if (game != null) {
                                games.add(game);
                            }
                        }
                        future.complete(games);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        future.completeExceptionally(error.toException());
                    }
                });

        return future;
    }

    /**
     * Record player's performance in a match
     */
    public CompletableFuture<Void> recordMatchPerformance(String playerUid, String gameId,
                                                          int goals, int assists, int minutesPlayed) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        Map<String, Object> performance = new HashMap<>();
        performance.put("goals", goals);
        performance.put("assists", assists);
        performance.put("minutesPlayed", minutesPlayed);
        performance.put("timestamp", System.currentTimeMillis());

        databaseRef.child("players")
                .child(playerUid)
                .child("performances")
                .child(gameId)
                .setValue(performance)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        future.complete(null);
                    } else {
                        future.completeExceptionally(task.getException());
                    }
                });

        return future;
    }
}
