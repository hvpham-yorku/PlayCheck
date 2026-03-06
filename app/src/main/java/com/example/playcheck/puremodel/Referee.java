package com.example.playcheck.puremodel;

import com.example.playcheck.dataBaseLinkFiles.RefereeLinkToDatabase;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Referee extends User {

    private List<LocalDate> gameDateAvailabilityDates;
    private List<Game> schedule;

    // Specialized database service
    private transient RefereeLinkToDatabase refereeDbService;

    public Referee() {
        super();
        this.gameDateAvailabilityDates = new ArrayList<>();
        this.schedule = new ArrayList<>();
        this.refereeDbService = new RefereeLinkToDatabase();
    }

    public Referee(String firstName, String lastName, String email, String dateOfBirth, String gender){
        super(firstName, lastName, email, dateOfBirth, gender);
        this.gameDateAvailabilityDates = new ArrayList<>();
        this.schedule = new ArrayList<>();
        this.refereeDbService = new RefereeLinkToDatabase();
    }


    public List<LocalDate> getGameDatesAvailability() {
        return gameDateAvailabilityDates;
    }

    public List<Game> getSchedule() {
        return schedule;
    }

    public void addGameDateAvailability(LocalDate date) {
        this.gameDateAvailabilityDates.add(date);
    }

    public void addToSchedule(Game game) {
        this.schedule.add(game);
    }

    public void deleteGameDate(LocalDate date) {
        this.gameDateAvailabilityDates.remove(date);
    }

    public void deleteGameFromSchedule(Game game) {

        this.schedule.remove(game);
    }

    public boolean isAvailableOnDate(LocalDate date) {
        return this.gameDateAvailabilityDates.contains(date);
    }


    //-------------------------------------------------------------------------------------------
    // Referee-specific Business Logic using specialized service
    //-------------------------------------------------------------------------------------------

    // TODO: 2026-03-05 work on this part
    @Override
    public CompletableFuture<String> register() {
        return super.register().thenCompose(uid -> {
            // After registration, save availability dates
            return refereeDbService.saveAvailabilityDates((String) uid, gameDateAvailabilityDates)
                    .thenApply(v -> uid);
        });
    }

    public CompletableFuture<Void> addAvailableDate(LocalDate date) {
        this.gameDateAvailabilityDates.add(date);
        if (getUid() != null) {
            return refereeDbService.saveAvailabilityDates(getUid(), gameDateAvailabilityDates);
        }
        return CompletableFuture.completedFuture(null);
    }

    public CompletableFuture<Void> removeAvailableDate(LocalDate date) {
        this.gameDateAvailabilityDates.remove(date);
        if (getUid() != null) {
            return refereeDbService.saveAvailabilityDates(getUid(), gameDateAvailabilityDates);
        }
        return CompletableFuture.completedFuture(null);
    }

    public CompletableFuture<Void> acceptGame(Game game) {
        this.schedule.add(game);
        this.gameDateAvailabilityDates.remove(game.getDate());

        if (getUid() != null) {
            return refereeDbService.assignGameToReferee(getUid(), game)
                    .thenCompose(v -> refereeDbService.saveAvailabilityDates(
                            getUid(), gameDateAvailabilityDates));
        }
        return CompletableFuture.completedFuture(null);
    }

    public CompletableFuture<List<LocalDate>> loadAvailabilityDates() {
        if (getUid() != null) {
            return refereeDbService.getAvailabilityDates(getUid())
                    .thenApply(dates -> {
                        this.gameDateAvailabilityDates = dates;
                        return dates;
                    });
        }
        return CompletableFuture.completedFuture(new ArrayList<>());
    }

    public CompletableFuture<List<Game>> loadAssignedGames() {
        if (getUid() != null) {
            return refereeDbService.getAssignedGames(getUid())
                    .thenApply(games -> {
                        this.schedule = games;
                        return games;
                    });
        }
        return CompletableFuture.completedFuture(new ArrayList<>());
    }


}
