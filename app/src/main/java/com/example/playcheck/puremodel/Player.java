package com.example.playcheck.puremodel;

import com.example.playcheck.database.PlayerLinkToDatabase;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class Player extends User implements UserStats {

    private Team team;
    private String teamId;
    private int goalsScored;
    private int matchesPlayed;

    // Specialized database service
    private transient PlayerLinkToDatabase playerDbService;

    public Player() {
        super();
        this.playerDbService = new PlayerLinkToDatabase();
    }

    public Player(String firstName, String lastName, String email, String dob, String gender){
        super(firstName, lastName, email, dob, gender);
        this.goalsScored = 0;
        this.matchesPlayed = 0;
        this.playerDbService = new PlayerLinkToDatabase();
    }

    //-------------------------------------------------------------------------------------------
    // Player-specific Business Logic
    //-------------------------------------------------------------------------------------------

    /**
     * Join a team - updates both team relationship and player profile
     */
    public CompletableFuture<Void> joinTeam(Team newTeam) {
        this.team = newTeam;
        this.teamId = newTeam.getTeamId();

        if (getUid() != null && teamId != null) {
            // First: Update the team membership in database
            return playerDbService.addPlayerToTeam(getUid(), teamId)
                    .thenCompose(v -> {
                        // Second: Save the updated player profile (with new teamId)
                        // This saveProfile() comes from the parent User class
                        return saveProfile();
                    })
                    .thenAccept(v -> {
                        // Third: Update successful - you could add logging here
                        System.out.println("Player successfully joined team: " + teamId);
                    });
        }
        return CompletableFuture.completedFuture(null);
    }

    /**
     * Leave team - removes team relationship and updates profile
     * This is where you saw the saveProfile() call
     */
    public CompletableFuture<Void> leaveTeam() {
        String oldTeamId = this.teamId;
        this.team = null;
        this.teamId = null;

        if (getUid() != null && oldTeamId != null) {
            // First: Remove from team in database
            return playerDbService.removePlayerFromTeam(getUid(), oldTeamId)
                    .thenCompose(v -> {
                        // Second: Save the updated player profile (with teamId = null)
                        // This saveProfile() comes from the parent User class
                        return saveProfile();
                    })
                    .thenAccept(v -> {
                        // Third: Update successful
                        System.out.println("Player successfully left team: " + oldTeamId);
                    });
        }
        return CompletableFuture.completedFuture(null);
    }

    /**
     * Update player statistics and save profile
     */
    public CompletableFuture<Void> updateStats(int goals, int matches) {
        this.goalsScored += goals;
        this.matchesPlayed += matches;

        if (getUid() != null) {
            Map<String, Object> stats = new HashMap<>();
            stats.put("goalsScored", goalsScored);
            stats.put("matchesPlayed", matchesPlayed);

            // Use saveProfileFields (more efficient than saveProfile)
            return saveProfileFields(stats);
        }
        return CompletableFuture.completedFuture(null);
    }

    /**
     * Record match performance and update stats
     */
    public CompletableFuture<Void> recordMatchPerformance(String gameId, int goals, int assists,
                                                          int minutesPlayed) {
        if (getUid() != null) {
            return playerDbService.recordMatchPerformance(getUid(), gameId, goals, assists,
                            minutesPlayed)
                    .thenCompose(v -> updateStats(goals, 1));
        }
        return CompletableFuture.completedFuture(null);
    }

    /**
     * Load team information from database
     */
    public CompletableFuture<Team> loadTeamInfo() {
        if (getUid() != null) {
            return playerDbService.getPlayerTeam(getUid())
                    .thenApply(team -> {
                        this.team = team;
                        if (team != null) {
                            this.teamId = team.getTeamId();
                        }
                        return team;
                    });
        }
        return CompletableFuture.completedFuture(null);
    }

    /**
     * Load match history
     */
    public CompletableFuture<List<Game>> loadMatchHistory() {
        if (getUid() != null) {
            return playerDbService.getPlayerMatchHistory(getUid());
        }
        return CompletableFuture.completedFuture(List.of());
    }

    //-------------------------------------------------------------------------------------------
    // Getters and Setters
    //-------------------------------------------------------------------------------------------

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
        if (team != null) {
            this.teamId = team.getTeamId();
        } else {
            this.teamId = null;
        }
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public int getGoalsScored() {
        return goalsScored;
    }

    public void setGoalsScored(int goalsScored) {
        this.goalsScored = goalsScored;
    }

    public int getMatchesPlayed() {
        return matchesPlayed;
    }

    public void setMatchesPlayed(int matchesPlayed) {
        this.matchesPlayed = matchesPlayed;
    }

    @Override
    public int gettotallikes() {
        return 0;
    }

    @Override
    public int gettotaldislikes() {
        return 0;
    }

    @Override
    public int gettotalSavings() {
        return 0;
    }


}