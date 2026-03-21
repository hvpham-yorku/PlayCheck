package com.example.playcheck.Database;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TeamLinkToDatabase {

    DatabaseReference teamsRef = FirebaseDatabase.getInstance().getReference("teams");

    /* Interfaces used for callbacks*/
    public interface TeamNamesCallback {
        void onCallback(ArrayList<String> teamNames);
    }
    public interface TeamIdCallback {
        void onCallback(ArrayList<String> teamIds);
    }

    /*Create a team in the teams folder. Listener return if task is sucessful or not */
    public void createTeam(ArrayList<String> playerIds, ArrayList<String> playerNames, String captainId, String captainName ,String teamName, OnCompleteListener<Void> listener){
        //create team id
        String teamId = teamsRef.push().getKey();

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //add players to team
        Map<String, String> playersMap = new HashMap<>();

        int nop = 0;
        for (String playerId : playerIds) {
            playersMap.put(playerId, playerNames.get(nop));
            nop++;
        }

        //add captain folder
        Map<String, String> captainMap = new HashMap<>();
        captainMap.put(captainId, captainName);

        //add team data to team name
        Map<String, Object> teamData = new HashMap<>();
        teamData.put("teamName", teamName);
        teamData.put("players", playersMap);
        teamData.put("Captain", captainMap);
        teamData.put("teamCreator", uid);


        teamsRef.child(teamId).setValue(teamData).addOnCompleteListener(listener);

    }

    /*Returns an ArrayList of all teams ids in DB */
    public void getTeamIDs(final TeamLinkToDatabase.TeamIdCallback callback) {
        ArrayList<String> teamIds = new ArrayList<>();

        teamsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                teamIds.clear();

                for (DataSnapshot idSnapshot : snapshot.getChildren()) {
                    String teamId = idSnapshot.getKey();
                    teamIds.add(teamId);
                }

                callback.onCallback(teamIds); //return data
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", error.getMessage());
            }
        });

    }

    /* Method that returns all player names in the database */
    public void getTeamNames(final TeamLinkToDatabase.TeamNamesCallback callback) {

        ArrayList<String> allTeamNames = new ArrayList<>();

        teamsRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {
                allTeamNames.clear();

                for (DataSnapshot teamSnapshot : snapshot.getChildren()) {
                    String tn = teamSnapshot.child("teamName").getValue(String.class);
                    allTeamNames.add(tn);
                    Log.d("FirebaseTest for teamNames" , tn);
                }

                callback.onCallback(allTeamNames);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("Firebase", error.getMessage());
            }
        });
    }







}
