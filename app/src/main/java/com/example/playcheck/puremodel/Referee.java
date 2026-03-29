package com.example.playcheck.puremodel;

import com.example.playcheck.Database.RefereeLinkToDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Referee extends User {

    private List<LocalDate> gameDateAvailabilityDates;
    private List<Game> schedule;

    private static RefereeLinkToDatabase refereeDbService = new RefereeLinkToDatabase();

    public Referee() {
        super();
        this.gameDateAvailabilityDates = new ArrayList<>();
        this.schedule = new ArrayList<>();
    }

    public Referee(String firstName, String lastName, String email, String dateOfBirth, String gender){
        super(firstName, lastName, email, dateOfBirth, gender);
        this.gameDateAvailabilityDates = new ArrayList<>();
        this.schedule = new ArrayList<>();
    }

    public Referee(String firstName, String lastName, String gender, LocalDate dateOfBirth) {
        super();
        setFirstName(firstName);
        setLastName(lastName);
        setGender(gender);
        setDateOfBirth(dateOfBirth);
        this.gameDateAvailabilityDates = new ArrayList<>();
        this.schedule = new ArrayList<>();
    }

    //-------------------------------------------------------------------------------------------
    // Business Logic Methods
    //-------------------------------------------------------------------------------------------

    @Override
    public CompletableFuture<String> register() {
        return super.register().thenCompose(uid -> 
            refereeDbService.saveAvailabilityDates(uid, gameDateAvailabilityDates)
                    .thenApply(v -> uid)
        );
    }

    public CompletableFuture<Void> addAvailableDate(LocalDate date) {
        this.gameDateAvailabilityDates.add(date);
        return refereeDbService.saveAvailabilityDates(getUid(), gameDateAvailabilityDates);
    }

    public CompletableFuture<Void> removeAvailableDate(LocalDate date) {
        this.gameDateAvailabilityDates.remove(date);
        return refereeDbService.saveAvailabilityDates(getUid(), gameDateAvailabilityDates);
    }

    public CompletableFuture<Void> acceptGame(Game game) {
        this.schedule.add(game);
        this.gameDateAvailabilityDates.remove(game.getDateInLocalDate());
        return refereeDbService.assignGameToReferee(getUid(), game)
                .thenCompose(v -> refereeDbService.saveAvailabilityDates(getUid(), gameDateAvailabilityDates));
    }

    public CompletableFuture<List<LocalDate>> loadAvailabilityDates() {
        return refereeDbService.getAvailabilityDates(getUid())
                .thenApply(dates -> {
                    this.gameDateAvailabilityDates = dates;
                    return dates;
                });
    }

    public CompletableFuture<List<Game>> loadAssignedGames() {
        return refereeDbService.getAssignedGames(getUid())
                .thenApply(games -> {
                    this.schedule = games;
                    return games;
                });
    }

    public String getRefereeId() {
        if (getUid() != null) return getUid();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            setUid(currentUser.getUid());
            return currentUser.getUid();
        }
        return null;
    }

    //-------------------------------------------------------------------------------------------
    // Getters and Setters
    //-------------------------------------------------------------------------------------------

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
}
