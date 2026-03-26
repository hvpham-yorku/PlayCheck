package com.example.playcheck.puremodel;

import java.time.LocalDate;
import java.util.List;

public class Tournament extends Event{

    private int tournamentID;
    private String tournamentName;
    private String tournamentDescription;
    private List<Game> games;
    private List<Team> teams;
    private List<Referee> referees;
    private Organizer organizer;

    private LocalDate startDate;
    private LocalDate endDate;

    private String location;

    private String tournamentFormat;
    private SportType sportType;

    private int numberOfTeams;
    private int numberOfGames;



    Tournament(){

    }

    public int getTournamentID() {
        return tournamentID;
    }

    public String getTournamentName() {

        return tournamentName;
    }

    public List<Game> getGames() {
        return games;
    }

    public List<Team> getTeams() {
        return teams;
    }

    public List<Referee> getReferees() {
        return referees;
    }

    public Organizer getOrganizer() {
        return this.organizer;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public String getLocation() {
        return location;
    }
    public String getTournamentFormat() {
        return tournamentFormat;
    }
    public SportType getSportType() {
        return sportType;
    }
    public int getNumberOfTeams() {
        return numberOfTeams;
    }
    public int getNumberOfGames() {
        return numberOfGames;
    }
    public String getTournamentDescription() {
        return tournamentDescription;
    }

    public void setTournamentID(int tournamentID) {
        this.tournamentID = tournamentID;
    }
    public void setTournamentName(String tournamentName) {
        this.tournamentName = tournamentName;
    }
    public void setGames(List<Game> games) {
        this.games = games;
    }
    public void setTeams(List<Team> teams) {
        this.teams = teams;
    }
    public void setReferees(List<Referee> referees) {
        this.referees = referees;
    }
    public void setOrganizers(Organizer organizer) {
        this.organizer = organizer;
    }
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
    public void setLocation(String location) {
        this.location = location;
    }
    public void setTournamentFormat(String tournamentFormat) {
        this.tournamentFormat = tournamentFormat;
    }
    public void setSportType(SportType sportType) {
        this.sportType = sportType;
    }
    public void setNumberOfTeams(int numberOfTeams) {
        this.numberOfTeams = numberOfTeams;
    }
    public void setNumberOfGames(int numberOfGames) {
        this.numberOfGames = numberOfGames;
    }
    public void setTournamentDescription(String tournamentDescription) {
        this.tournamentDescription = tournamentDescription;
    }






}
