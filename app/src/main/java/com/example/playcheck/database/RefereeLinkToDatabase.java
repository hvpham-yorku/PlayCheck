package com.example.playcheck.database;

import com.example.playcheck.puremodel.Game;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class RefereeLinkToDatabase extends UserLinkToDatabase {

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
}