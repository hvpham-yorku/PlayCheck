package com.example.playcheck.database;

import androidx.annotation.NonNull;

import com.example.playcheck.puremodel.Team;
import com.google.android.gms.tasks.OnCompleteListener;
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

public class TeamLinkToDatabase {

    private DatabaseReference teamsRef;

    public TeamLinkToDatabase() {
        this.teamsRef = FirebaseDatabase.getInstance().getReference("teams");
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
}
