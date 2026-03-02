package com.example.playcheck.puremodel;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class Organizer extends User {

    private List<LocalDate> gameDateAvailabilityDates;
    private List<Game> schedule;

    private String teamName;
    private String leagueName;

    public Organizer() {
        this.gameDateAvailabilityDates = new ArrayList<LocalDate>();
        this.schedule = new ArrayList<>();
    }

    public Organizer(String userId, String firstName, String lastName, String gender, LocalDate dateOfBirth) {
        this();
        super.setUserId(userId);
        super.setName(firstName, lastName);
        super.setGender(gender);
        super.setDateOfBirth(dateOfBirth);
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