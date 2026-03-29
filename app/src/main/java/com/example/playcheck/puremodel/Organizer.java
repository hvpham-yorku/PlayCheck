package com.example.playcheck.puremodel;

import com.example.playcheck.database.OrganizerLinkToDatabase;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Organizer extends User {

    private List<LocalDate> gameDateAvailabilityDates;
    private List<Game> schedule;

    private String teamName;
    private String leagueName;

    private static OrganizerLinkToDatabase organizerDbService = new OrganizerLinkToDatabase();

    public Organizer() {
        super();
        this.gameDateAvailabilityDates = new ArrayList<>();
        this.schedule = new ArrayList<>();
    }

    public Organizer(String firstName, String lastName, String email, String dob, String gender) {
        super(firstName, lastName, email, dob, gender);
        this.schedule = new ArrayList<>();
        this.gameDateAvailabilityDates = new ArrayList<>();
    }

    public Organizer(String userId, String firstName, String lastName, String gender, LocalDate dateOfBirth) {
        this();
        super.setName(firstName, lastName);
        super.setGender(gender);
        super.setDateOfBirth(dateOfBirth);
        this.schedule = new ArrayList<>();
        this.gameDateAvailabilityDates = new ArrayList<>();
    }

    // -------------------------------------------------------------------------------------------
    // Business Logic Methods
    // -------------------------------------------------------------------------------------------

    public CompletableFuture<String> createNewEvent(Event event) {
        return organizerDbService.createEvent(getUid(), event);
    }

    public CompletableFuture<String> createNewTeam(Team team) {
        return organizerDbService.createTeam(team);
    }

    public CompletableFuture<String> scheduleNewGame(Game game) {
        return organizerDbService.scheduleGame(game);
    }

    public CompletableFuture<List<Event>> fetchOrganizerEvents() {
        return organizerDbService.getOrganizerEvents(getUid());
    }

    public CompletableFuture<Void> assignReferee(String gameId, String refereeId) {
        return organizerDbService.assignRefereeToGame(gameId, refereeId);
    }

    // -------------------------------------------------------------------------------------------
    // Getters and Setters
    // -------------------------------------------------------------------------------------------

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

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getLeagueName() {
        return leagueName;
    }

    public void setLeagueName(String leagueName) {
        this.leagueName = leagueName;
    }
}
