package com.example.playcheck.Database;

import com.example.playcheck.puremodel.Game;
import com.example.playcheck.puremodel.Tournament;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import androidx.annotation.NonNull;

/**
 * Database helper for tournament management
 */
public class TournamentDatabase {
    
    private DatabaseReference databaseRef;
    
    public TournamentDatabase() {
        databaseRef = FirebaseDatabase.getInstance().getReference();
    }
    
    /**
     * Create a new tournament
     * @param tournament The tournament object
     * @return CompletableFuture with the tournament ID
     */
    public CompletableFuture<String> createTournament(Tournament tournament) {
        CompletableFuture<String> future = new CompletableFuture<>();
        
        DatabaseReference tournamentsRef = databaseRef.child("tournaments");
        String tournamentId = tournamentsRef.push().getKey();
        
        if (tournamentId == null) {
            future.completeExceptionally(new Exception("Failed to generate tournament ID"));
            return future;
        }
        
        tournament.setTournamentId(tournamentId);
        
        tournamentsRef.child(tournamentId).setValue(tournament)
            .addOnSuccessListener(aVoid -> future.complete(tournamentId))
            .addOnFailureListener(future::completeExceptionally);
        
        return future;
    }
    
    /**
     * Get a tournament by ID
     * @param tournamentId The tournament ID
     * @return CompletableFuture with tournament object
     */
    public CompletableFuture<Tournament> getTournament(String tournamentId) {
        CompletableFuture<Tournament> future = new CompletableFuture<>();
        
        databaseRef.child("tournaments").child(tournamentId)
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Tournament tournament = snapshot.getValue(Tournament.class);
                    if (tournament != null) {
                        tournament.setTournamentId(tournamentId);
                        future.complete(tournament);
                    } else {
                        future.completeExceptionally(new Exception("Tournament not found"));
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
     * Get all tournaments
     * @return CompletableFuture with list of tournaments
     */
    public CompletableFuture<List<Tournament>> getAllTournaments() {
        CompletableFuture<List<Tournament>> future = new CompletableFuture<>();
        List<Tournament> tournaments = new ArrayList<>();
        
        databaseRef.child("tournaments")
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot tournSnapshot : snapshot.getChildren()) {
                        Tournament tournament = tournSnapshot.getValue(Tournament.class);
                        if (tournament != null) {
                            tournament.setTournamentId(tournSnapshot.getKey());
                            tournaments.add(tournament);
                        }
                    }
                    future.complete(tournaments);
                }
                
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    future.completeExceptionally(error.toException());
                }
            });
        
        return future;
    }
    
    /**
     * Add a game to a tournament
     * @param tournamentId The tournament ID
     * @param gameId The game ID
     * @return CompletableFuture indicating completion
     */
    public CompletableFuture<Void> addGameToTournament(String tournamentId, String gameId) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        
        Map<String, Object> updates = new HashMap<>();
        updates.put("tournaments/" + tournamentId + "/gameIds/" + gameId, true);
        updates.put("games/" + gameId + "/tournamentId", tournamentId);
        
        databaseRef.updateChildren(updates)
            .addOnSuccessListener(aVoid -> future.complete(null))
            .addOnFailureListener(future::completeExceptionally);
        
        return future;
    }
    
    /**
     * Remove a game from a tournament
     * @param tournamentId The tournament ID
     * @param gameId The game ID
     * @return CompletableFuture indicating completion
     */
    public CompletableFuture<Void> removeGameFromTournament(String tournamentId, String gameId) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        
        Map<String, Object> updates = new HashMap<>();
        updates.put("tournaments/" + tournamentId + "/gameIds/" + gameId, null);
        updates.put("games/" + gameId + "/tournamentId", null);
        
        databaseRef.updateChildren(updates)
            .addOnSuccessListener(aVoid -> future.complete(null))
            .addOnFailureListener(future::completeExceptionally);
        
        return future;
    }
    
    /**
     * Update tournament bracket
     * @param tournamentId The tournament ID
     * @param bracket The bracket data
     * @return CompletableFuture indicating completion
     */
    public CompletableFuture<Void> updateBracket(String tournamentId, Map<String, Object> bracket) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        
        databaseRef.child("tournaments")
            .child(tournamentId)
            .child("bracket")
            .setValue(bracket)
            .addOnSuccessListener(aVoid -> future.complete(null))
            .addOnFailureListener(future::completeExceptionally);
        
        return future;
    }
    
    /**
     * Update tournament status
     * @param tournamentId The tournament ID
     * @param status The new status ("upcoming", "ongoing", "completed")
     * @return CompletableFuture indicating completion
     */
    public CompletableFuture<Void> updateTournamentStatus(String tournamentId, String status) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        
        databaseRef.child("tournaments")
            .child(tournamentId)
            .child("status")
            .setValue(status)
            .addOnSuccessListener(aVoid -> future.complete(null))
            .addOnFailureListener(future::completeExceptionally);
        
        return future;
    }
    
    /**
     * Get all games in a tournament
     * @param tournamentId The tournament ID
     * @return CompletableFuture with list of games
     */
    public CompletableFuture<List<Game>> getTournamentGames(String tournamentId) {
        CompletableFuture<List<Game>> future = new CompletableFuture<>();
        
        getTournament(tournamentId).thenAccept(tournament -> {
            List<String> gameIds = tournament.getGameIds();
            if (gameIds == null || gameIds.isEmpty()) {
                future.complete(new ArrayList<>());
                return;
            }
            
            fetchGames(gameIds, future);
        }).exceptionally(throwable -> {
            future.completeExceptionally(throwable);
            return null;
        });
        
        return future;
    }
    
    private void fetchGames(List<String> gameIds, CompletableFuture<List<Game>> future) {
        List<Game> games = new ArrayList<>();
        fetchGameRecursive(gameIds, 0, games, future);
    }
    
    private void fetchGameRecursive(List<String> gameIds, int index, List<Game> games,
                                    CompletableFuture<List<Game>> future) {
        if (index >= gameIds.size()) {
            future.complete(games);
            return;
        }
        
        String gameId = gameIds.get(index);
        databaseRef.child("games").child(gameId)
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Game game = snapshot.getValue(Game.class);
                    if (game != null) {
                        game.setGameId(gameId);
                        games.add(game);
                    }
                    fetchGameRecursive(gameIds, index + 1, games, future);
                }
                
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    future.completeExceptionally(error.toException());
                }
            });
    }
    
    /**
     * Delete a tournament
     * @param tournamentId The tournament ID
     * @return CompletableFuture indicating completion
     */
    public CompletableFuture<Void> deleteTournament(String tournamentId) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        
        databaseRef.child("tournaments")
            .child(tournamentId)
            .removeValue()
            .addOnSuccessListener(aVoid -> future.complete(null))
            .addOnFailureListener(future::completeExceptionally);
        
        return future;
    }
}
