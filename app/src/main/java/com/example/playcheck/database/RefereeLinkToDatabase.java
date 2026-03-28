package com.example.playcheck.Database;

import com.example.playcheck.puremodel.Game;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class RefereeLinkToDatabase extends com.example.playcheck.Database.UserLinkToDatabase {

        public RefereeLinkToDatabase() {
                super();
        }

        //-------------------------------------------------------------------------------------------
        // Referee-specific Database Operations
        //-------------------------------------------------------------------------------------------

        /**
         * Save referee's availability dates
         */
        public CompletableFuture<Void> saveAvailabilityDates(String refereeUid, List<LocalDate> dates) {
                CompletableFuture<Void> future = new CompletableFuture<>();

                List<String> dateStrings = new ArrayList<>();
                for (LocalDate date : dates) {
                        dateStrings.add(date.toString());
                }

                databaseRef.child("referees")
                        .child(refereeUid)
                        .child("availability")
                        .setValue(dateStrings)
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
         * Get referee's availability dates
         */
        public CompletableFuture<List<LocalDate>> getAvailabilityDates(String refereeUid) {
                CompletableFuture<List<LocalDate>> future = new CompletableFuture<>();
                List<LocalDate> dates = new ArrayList<>();

                databaseRef.child("referees")
                        .child(refereeUid)
                        .child("availability")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot snapshot) {
                                        for (DataSnapshot dateSnapshot : snapshot.getChildren()) {
                                                String dateStr = dateSnapshot.getValue(String.class);
                                                if (dateStr != null) {
                                                        dates.add(LocalDate.parse(dateStr));
                                                }
                                        }
                                        future.complete(dates);
                                }

                                @Override
                                public void onCancelled(DatabaseError error) {
                                        future.completeExceptionally(error.toException());
                                }
                        });

                return future;
        }

        /**
         * Assign game to referee
         */
        public CompletableFuture<Void> assignGameToReferee(String refereeUid, Game game) {
                CompletableFuture<Void> future = new CompletableFuture<>();

                databaseRef.child("referees")
                        .child(refereeUid)
                        .child("assignedGames")
                        .child(game.getGameId())
                        .setValue(game)
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
         * Get referee's assigned games
         */
        public CompletableFuture<List<Game>> getAssignedGames(String refereeUid) {
                CompletableFuture<List<Game>> future = new CompletableFuture<>();
                List<Game> games = new ArrayList<>();

                databaseRef.child("referees")
                        .child(refereeUid)
                        .child("assignedGames")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot snapshot) {
                                        for (DataSnapshot gameSnapshot : snapshot.getChildren()) {
                                                Game game = gameSnapshot.getValue(Game.class);
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
         * Remove game from referee's assignments
         */
        public CompletableFuture<Void> removeGameAssignment(String refereeUid, String gameId) {
                CompletableFuture<Void> future = new CompletableFuture<>();

                databaseRef.child("referees")
                        .child(refereeUid)
                        .child("assignedGames")
                        .child(gameId)
                        .removeValue()
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
         * Update referee's game report
         */
        public CompletableFuture<Void> submitGameReport(String refereeUid, String gameId, Object report) {
                CompletableFuture<Void> future = new CompletableFuture<>();

                databaseRef.child("referees")
                        .child(refereeUid)
                        .child("gameReports")
                        .child(gameId)
                        .setValue(report)
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
         * Save a video clip for a specific game
         */
        public CompletableFuture<Void> saveMatchClip(String gameId, String clipTitle, String clipUri) {
                CompletableFuture<Void> future = new CompletableFuture<>();
                
                String clipId = databaseRef.child("matchClips").child(gameId).push().getKey();
                
                java.util.Map<String, String> clipData = new java.util.HashMap<>();
                clipData.put("title", clipTitle);
                clipData.put("uri", clipUri);
                clipData.put("id", clipId); // Store ID for deletion

                databaseRef.child("matchClips")
                        .child(gameId)
                        .child(clipId)
                        .setValue(clipData)
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
         * Get all video clips for a specific game
         */
        public CompletableFuture<List<java.util.Map<String, String>>> getMatchClips(String gameId) {
                CompletableFuture<List<java.util.Map<String, String>>> future = new CompletableFuture<>();
                List<java.util.Map<String, String>> clips = new ArrayList<>();

                databaseRef.child("matchClips")
                        .child(gameId)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot snapshot) {
                                        for (DataSnapshot clipSnapshot : snapshot.getChildren()) {
                                                java.util.Map<String, String> clip = (java.util.Map<String, String>) clipSnapshot.getValue();
                                                if (clip != null) {
                                                        // Ensure the 'id' field is present by using the Firebase Key as a fallback
                                                        if (!clip.containsKey("id")) {
                                                                clip.put("id", clipSnapshot.getKey());
                                                        }
                                                        clips.add(clip);
                                                }
                                        }
                                        future.complete(clips);
                                }

                                @Override
                                public void onCancelled(DatabaseError error) {
                                        future.completeExceptionally(error.toException());
                                }
                        });

                return future;
        }

        /**
         * Delete a specific video clip
         */
        public CompletableFuture<Void> deleteMatchClip(String gameId, String clipId) {
                CompletableFuture<Void> future = new CompletableFuture<>();

                if (clipId == null) {
                        future.completeExceptionally(new Exception("Clip ID is null"));
                        return future;
                }

                databaseRef.child("matchClips")
                        .child(gameId)
                        .child(clipId)
                        .removeValue()
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
