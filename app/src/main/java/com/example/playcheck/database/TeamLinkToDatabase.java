package com.example.playcheck.database;

import androidx.annotation.NonNull;

import com.example.playcheck.puremodel.Team;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class TeamLinkToDatabase {

    private DatabaseReference teamsRef;

    public TeamLinkToDatabase() {
        this.teamsRef = FirebaseDatabase.getInstance().getReference("teams");
    }

    public interface OnPlayersFetchedListener {
        void onPlayersFetched(ArrayList<String> playerIds, ArrayList<String> playerNames);
    }

    public interface TeamIdCallback {
        void onCallback(ArrayList<String> teamIds);
    }

    public interface TeamNamesCallback {
        void onCallback(ArrayList<String> teamNames);
    }

    public interface allTeamsCallback {
        void onCallback(ArrayList<Team> teamsList);
    }

    /**
     * Creates a team in the database using a Team object.
     */
    public CompletableFuture<String> createTeam(Team team) {
        CompletableFuture<String> future = new CompletableFuture<>();
        String teamId = teamsRef.push().getKey();
        if (teamId == null) {
            future.completeExceptionally(new Exception("Failed to generate team ID"));
            return future;
        }
        team.setTeamId(teamId);

        teamsRef.child(teamId).setValue(team).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                future.complete(teamId);
            } else {
                future.completeExceptionally(task.getException());
            }
        });

        return future;
    }

    /**
     * Creates a team in the database using individual fields (used by CreateTeam activity).
     */
    public void createTeam(ArrayList<String> playerIds, ArrayList<String> playerNames, String captainId, String captainName, String teamName, OnCompleteListener<Void> listener) {
        String teamId = teamsRef.push().getKey();
        if (teamId == null) return;

        Map<String, Object> teamData = new HashMap<>();
        teamData.put("teamId", teamId);
        teamData.put("teamName", teamName);
        teamData.put("captainId", captainId);
        teamData.put("captainName", captainName);
        teamData.put("memberIds", playerIds);
        teamData.put("memberNames", playerNames);

        teamsRef.child(teamId).setValue(teamData).addOnCompleteListener(listener);
    }

    /**
     * Fetches all teams from the database.
     */
    public CompletableFuture<List<Team>> getAllTeams() {
        CompletableFuture<List<Team>> future = new CompletableFuture<>();
        teamsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Team> teams = new ArrayList<>();
                for (DataSnapshot teamSnapshot : snapshot.getChildren()) {
                    Team team = teamSnapshot.getValue(Team.class);
                    if (team != null) {
                        team.setTeamId(teamSnapshot.getKey());
                        teams.add(team);
                    }
                }
                future.complete(teams);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                future.completeExceptionally(error.toException());
            }
        });
        return future;
    }

    /**
     * Fetches a specific team by ID.
     */
    public CompletableFuture<Team> getTeamById(String teamId) {
        CompletableFuture<Team> future = new CompletableFuture<>();
        teamsRef.child(teamId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Team team = snapshot.getValue(Team.class);
                if (team != null) {
                    team.setTeamId(snapshot.getKey());
                    future.complete(team);
                } else {
                    future.completeExceptionally(new Exception("Team not found"));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                future.completeExceptionally(error.toException());
            }
        });
        return future;
    }

    /**
     * Fetches players from a team by team ID.
     */
    public void getPlayersFromTeam(String teamId, OnPlayersFetchedListener listener) {
        if (teamId == null || teamId.isEmpty()) {
            listener.onPlayersFetched(new ArrayList<>(), new ArrayList<>());
            return;
        }
        getTeamById(teamId).thenAccept(team -> {
            ArrayList<String> playerIds = new ArrayList<>();
            ArrayList<String> playerNames = new ArrayList<>();
            if (team != null && team.getPlayers() != null) {
                for (Map.Entry<String, String> entry : team.getPlayers().entrySet()) {
                    playerIds.add(entry.getKey());
                    playerNames.add(entry.getValue());
                }
            }
            listener.onPlayersFetched(playerIds, playerNames);
        }).exceptionally(ex -> {
            listener.onPlayersFetched(new ArrayList<>(), new ArrayList<>());
            return null;
        });
    }

    public void getTeamIDs(TeamIdCallback callback) {
        teamsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> ids = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    ids.add(child.getKey());
                }
                callback.onCallback(ids);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void getTeamNames(TeamNamesCallback callback) {
        teamsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> names = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    String name = child.child("teamName").getValue(String.class);
                    if (name != null) names.add(name);
                }
                callback.onCallback(names);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void getAllTeamsForUser(allTeamsCallback callback) {
        String currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        teamsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Team> userTeams = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Team team = child.getValue(Team.class);
                    if (team != null) {
                        team.setTeamId(child.getKey());
                        // Check if current user is captain or a member
                        if (currentUid.equals(team.getCaptainId()) || 
                            (team.getPlayers() != null && team.getPlayers().containsKey(currentUid))) {
                            userTeams.add(team);
                        }
                    }
                }
                callback.onCallback(userTeams);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onCallback(new ArrayList<>());
            }
        });
    }

    public void getAllTeamsForStandings(Consumer<ArrayList<Team>> callback) {
        teamsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Team> allTeams = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Team team = child.getValue(Team.class);
                    if (team != null) {
                        team.setTeamId(child.getKey());
                        allTeams.add(team);
                    }
                }
                callback.accept(allTeams);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.accept(new ArrayList<>());
            }
        });
    }

    /**
     * Updates the win/loss record of a team.
     * @param teamId The team ID
     * @param isWin True if the team won, false if they lost
     */
    public void updateTeamRecord(String teamId, boolean isWin) {
        teamsRef.child(teamId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Team team = snapshot.getValue(Team.class);
                if (team != null) {
                    if (isWin) {
                        team.setTeamWins(team.getTeamWins() + 1);
                    } else {
                        team.setTeamLosses(team.getTeamLosses() + 1);
                    }
                    teamsRef.child(teamId).setValue(team);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}
