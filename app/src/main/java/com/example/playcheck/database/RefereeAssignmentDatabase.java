package com.example.playcheck.Database;

import com.example.playcheck.puremodel.Referee;
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
 * Database helper for assigning referees to games
 */
public class RefereeAssignmentDatabase {
    
    private DatabaseReference databaseRef;
    
    public RefereeAssignmentDatabase() {
        databaseRef = FirebaseDatabase.getInstance().getReference();
    }
    
    /**
     * Assign a referee to a game
     * @param gameId The game ID
     * @param refereeId The referee's UID
     * @param refereeName The referee's name
     * @return CompletableFuture indicating completion
     */
    public CompletableFuture<Void> assignRefereeToGame(String gameId, String refereeId, String refereeName) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        
        Map<String, Object> updates = new HashMap<>();
        
        // Update game with referee info
        updates.put("games/" + gameId + "/refereeId", refereeId);
        updates.put("games/" + gameId + "/refereeName", refereeName);
        
        // Add game to referee's assigned games list
        updates.put("referees/" + refereeId + "/assignedGames/" + gameId, true);
        
        databaseRef.updateChildren(updates)
            .addOnSuccessListener(aVoid -> future.complete(null))
            .addOnFailureListener(future::completeExceptionally);
        
        return future;
    }
    
    /**
     * Remove referee assignment from a game
     * @param gameId The game ID
     * @param refereeId The referee's UID
     * @return CompletableFuture indicating completion
     */
    public CompletableFuture<Void> removeRefereeFromGame(String gameId, String refereeId) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        
        Map<String, Object> updates = new HashMap<>();
        
        // Remove referee from game
        updates.put("games/" + gameId + "/refereeId", null);
        updates.put("games/" + gameId + "/refereeName", null);
        
        // Remove game from referee's list
        if (refereeId != null) {
            updates.put("referees/" + refereeId + "/assignedGames/" + gameId, null);
        }
        
        databaseRef.updateChildren(updates)
            .addOnSuccessListener(aVoid -> future.complete(null))
            .addOnFailureListener(future::completeExceptionally);
        
        return future;
    }
    
    /**
     * Get all available referees
     * @return CompletableFuture with list of referees
     */
    public CompletableFuture<List<Referee>> getAllReferees() {
        CompletableFuture<List<Referee>> future = new CompletableFuture<>();
        List<Referee> referees = new ArrayList<>();
        
        databaseRef.child("users")
            .orderByChild("classType")
            .equalTo("Referee")
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot refSnapshot : snapshot.getChildren()) {
                        Referee referee = refSnapshot.getValue(Referee.class);
                        if (referee != null) {
                            referee.setUid(refSnapshot.getKey());
                            referees.add(referee);
                        }
                    }
                    future.complete(referees);
                }
                
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    future.completeExceptionally(error.toException());
                }
            });
        
        return future;
    }
    
    /**
     * Get games assigned to a specific referee
     * @param refereeId The referee's UID
     * @return CompletableFuture with list of game IDs
     */
    public CompletableFuture<List<String>> getRefereeAssignedGames(String refereeId) {
        CompletableFuture<List<String>> future = new CompletableFuture<>();
        List<String> gameIds = new ArrayList<>();
        
        databaseRef.child("referees")
            .child(refereeId)
            .child("assignedGames")
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot gameSnapshot : snapshot.getChildren()) {
                        gameIds.add(gameSnapshot.getKey());
                    }
                    future.complete(gameIds);
                }
                
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    future.completeExceptionally(error.toException());
                }
            });
        
        return future;
    }
    
    /**
     * Check if a referee is available for a game (not double-booked)
     * @param refereeId The referee's UID
     * @param gameDate The game timestamp
     * @return CompletableFuture with boolean (true if available)
     */
    public CompletableFuture<Boolean> isRefereeAvailable(String refereeId, long gameDate) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        
        // Check if referee has any games at the same time (within 3 hours)
        long bufferTime = 3 * 60 * 60 * 1000; // 3 hours in milliseconds
        
        getRefereeAssignedGames(refereeId).thenAccept(gameIds -> {
            if (gameIds.isEmpty()) {
                future.complete(true);
                return;
            }
            
            // Check each assigned game's time
            checkGameConflicts(gameIds, gameDate, bufferTime, 0, future);
        }).exceptionally(throwable -> {
            future.completeExceptionally(throwable);
            return null;
        });
        
        return future;
    }
    
    private void checkGameConflicts(List<String> gameIds, long targetDate, long buffer, 
                                    int index, CompletableFuture<Boolean> future) {
        if (index >= gameIds.size()) {
            future.complete(true); // No conflicts found
            return;
        }
        
        String gameId = gameIds.get(index);
        databaseRef.child("games").child(gameId).child("gameDate")
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Long existingDate = snapshot.getValue(Long.class);
                    if (existingDate != null) {
                        long diff = Math.abs(existingDate - targetDate);
                        if (diff < buffer) {
                            future.complete(false); // Conflict found
                            return;
                        }
                    }
                    // Check next game
                    checkGameConflicts(gameIds, targetDate, buffer, index + 1, future);
                }
                
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    future.completeExceptionally(error.toException());
                }
            });
    }
}
