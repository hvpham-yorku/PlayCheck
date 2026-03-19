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
    private List<Organizer> organizers;

    private LocalDate startDate;
    private LocalDate endDate;

    private String location;

    private String tournamentFormat;
    private String sportType;

    Tournament(){

    }



}
