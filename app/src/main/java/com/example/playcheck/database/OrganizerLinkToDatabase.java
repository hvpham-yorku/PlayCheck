package com.example.playcheck.Database;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import com.example.playcheck.puremodel.Event;
import com.example.playcheck.puremodel.Game;
import com.example.playcheck.puremodel.Team;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class OrganizerLinkToDatabase extends com.example.playcheck.Database.UserLinkToDatabase {

    public OrganizerLinkToDatabase() {
        super();
    }

    //-------------------------------------------------------------------------------------------
    // Organizer-specific Database Operations
    //-------------------------------------------------------------------------------------------

    /**
     * Create a new event
     */
    public CompletableFuture<String> createEvent(String organizerUid, Event event) {
        CompletableFuture<String> future = new CompletableFuture<>();

        String eventId = databaseRef.child("events").push().getKey();
        if (eventId == null) {
            future.completeExceptionally(new Exception("Failed to generate event ID"));
            return future;
        }
        event.setEventId(eventId);

        Map<String, Object> updates = new HashMap<>();
        updates.put("events/" + eventId, event);
        updates.put("organizers/" + organizerUid + "/events/" + eventId, true);

        databaseRef.updateChildren(updates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        future.complete(eventId);
                    } else {
                        future.completeExceptionally(task.getException());
                    }
                });

        return future;
    }

    /**
     * Update event details
     */
    public CompletableFuture<Void> updateEvent(String eventId, Map<String, Object> updates) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        databaseRef.child("events")
                .child(eventId)
                .updateChildren(updates)
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
     * Delete an event
     */
    public CompletableFuture<Void> deleteEvent(String organizerUid, String eventId) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        Map<String, Object> updates = new HashMap<>();
        updates.put("events/" + eventId, null);
        updates.put("organizers/" + organizerUid + "/events/" + eventId, null);

        databaseRef.updateChildren(updates)
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
     * Create a new team
     */
    public CompletableFuture<String> createTeam(Team team) {
        CompletableFuture<String> future = new CompletableFuture<>();

        String teamId = databaseRef.child("teams").push().getKey();
        if (teamId == null) {
            future.completeExceptionally(new Exception("Failed to generate team ID"));
            return future;
        }
        team.setTeamId(teamId);

        databaseRef.child("teams")
                .child(teamId)
                .setValue(team)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        future.complete(teamId);
                    } else {
                        future.completeExceptionally(task.getException());
                    }
                });

        return future;
    }

    /**
     * Schedule a game
     */
    public CompletableFuture<String> scheduleGame(Game game) {
        CompletableFuture<String> future = new CompletableFuture<>();

        String gameId = databaseRef.child("games").push().getKey();
        if (gameId == null) {
            future.completeExceptionally(new Exception("Failed to generate game ID"));
            return future;
        }
        game.setGameId(gameId);

        Map<String, Object> updates = new HashMap<>();
        updates.put("games/" + gameId, game);
        updates.put("events/" + game.getEventId() + "/games/" + gameId, true);

        if (game.getTeamA() != null) {
            updates.put("teams/" + game.getTeamA() + "/games/" + gameId, true);
        }
        if (game.getTeamB() != null) {
            updates.put("teams/" + game.getTeamB() + "/games/" + gameId, true);
        }
        if (game.getRefereeId() != null) {
            updates.put("referees/" + game.getRefereeId() + "/assignedGames/" + gameId, true);
        }

        databaseRef.updateChildren(updates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        future.complete(gameId);
                    } else {
                        future.completeExceptionally(task.getException());
                    }
                });

        return future;
    }

    /**
     * Update game result
     */
    public CompletableFuture<Void> updateGameResult(String gameId, Map<String, Object> result) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        databaseRef.child("games")
                .child(gameId)
                .child("result")
                .setValue(result)
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
     * Get all events created by organizer
     */
    public CompletableFuture<List<Event>> getOrganizerEvents(String organizerUid) {
        CompletableFuture<List<Event>> future = new CompletableFuture<>();
        List<Event> events = new ArrayList<>();

        databaseRef.child("organizers")
                .child(organizerUid)
                .child("events")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        List<CompletableFuture<Void>> futures = new ArrayList<>();

                        for (DataSnapshot eventSnapshot : snapshot.getChildren()) {
                            String eventId = eventSnapshot.getKey();
                            CompletableFuture<Void> eventFuture = new CompletableFuture<>();

                            databaseRef.child("events")
                                    .child(eventId)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot eventDataSnapshot) {
                                            Event event = eventDataSnapshot.getValue(Event.class);
                                            if (event != null) {
                                                event.setEventId(eventId);
                                                events.add(event);
                                            }
                                            eventFuture.complete(null);
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError error) {
                                            eventFuture.completeExceptionally(error.toException());
                                        }
                                    });

                            futures.add(eventFuture);
                        }

                        if (futures.isEmpty()) {
                            future.complete(events);
                        } else {
                            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                                    .thenAccept(v -> future.complete(events))
                                    .exceptionally(throwable -> {
                                        future.completeExceptionally(throwable);
                                        return null;
                                    });
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
     * Assign referee to a game
     */
    public CompletableFuture<Void> assignRefereeToGame(String gameId, String refereeId) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        Map<String, Object> updates = new HashMap<>();
        updates.put("games/" + gameId + "/refereeId", refereeId);
        updates.put("referees/" + refereeId + "/assignedGames/" + gameId, true);

        databaseRef.updateChildren(updates)
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
