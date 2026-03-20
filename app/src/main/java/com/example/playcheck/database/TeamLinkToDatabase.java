package com.example.playcheck.Database;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TeamLinkToDatabase {

    DatabaseReference teamsRef = FirebaseDatabase.getInstance().getReference("teams");

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






}
