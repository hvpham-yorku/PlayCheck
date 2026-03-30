package com.example.playcheck.Database;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.playcheck.activityfiles.AdapterGameList;
import com.example.playcheck.activityfiles.CreateGameActivity;
import com.example.playcheck.puremodel.Game;
import com.example.playcheck.puremodel.MatchReport;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GameLinkToDatabase {

    /* Interfaces used for callbacks*/
    public interface RefereeNamesCallback {
        void onCallback(ArrayList<String> refereeIds, ArrayList<String> refereeNames);
    }

    public interface TeamIdsFromGameCallback {
        void onCallback(String teamAid, String teamBid);
    }

    public interface MatchReportCallback {
        void onCallback(MatchReport report);
    }

    DatabaseReference gamesRef = FirebaseDatabase.getInstance().getReference("games");
    /* Reusable method that gets game data from the database based on a Query object*/
    public void getGameData(Query gamedata, ArrayList<Game> games, AdapterGameList adapter){ //DatabaseReference is a child class of Query so this is ok to do
        gamedata.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {//when games are added/deleted in Firebase, the ArrayList that stores the games gets updated
                games.clear(); //clear list before updating it again
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Game info = dataSnapshot.getValue(Game.class);
                    info.setGameId(dataSnapshot.getKey()); //set game id for each game
                    boolean isCreator = uid.equals(info.getGameCreator());
                    boolean isParticipant = info.getPlayers() != null && info.getPlayers().containsKey(uid);
                    boolean isReferee = info.getReferees() != null && info.getReferees().containsKey(uid);
                    if (isCreator || isParticipant || isReferee) {
                        games.add(info);
                    }
                }
                adapter.notifyDataSetChanged(); //Tells the recycler view to update with new game list
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /* Create a game in the games folder */
    public void createGame(String teamAid, String teamBid, String teamA, String teamB, String gameVenue, String sport, long gameDateTime, ArrayList<String> playerIds, ArrayList<String> playerNames, ArrayList<String> refIds, ArrayList<String> refNames, OnCompleteListener<Void> listener){
        String gameId = gamesRef.push().getKey();

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Map<String,Object> game = new HashMap<>();

        Map<String, String> playersMap = new HashMap<>();
        for (int i = 0; i < playerIds.size(); i++) {
            playersMap.put(playerIds.get(i), playerNames.get(i));
        }

        Map<String, String> refsMap = new HashMap<>();
        for (int i = 0; i < refIds.size(); i++) {
            refsMap.put(refIds.get(i), refNames.get(i));
        }

        game.put("teamA", teamA);
        game.put("teamB", teamB);
        game.put("teamAid", teamAid);
        game.put("teamBid", teamBid);
        game.put("gameVenue", gameVenue);
        game.put("gameType", sport);
        game.put("gameDate", gameDateTime);
        game.put("gameCreator", uid);
        game.put("players", playersMap);
        game.put("referees", refsMap);
        game.put("score", "No score logged yet");

        gamesRef.child(gameId).setValue(game).addOnCompleteListener(listener);

    }

    /* Update specific fields for an existing game */
    public void updateGameDetails(String gameId, Map<String, Object> updates, OnCompleteListener<Void> listener) {
        gamesRef.child(gameId).updateChildren(updates).addOnCompleteListener(listener);
    }

    /* Given a game id, return the ids and names of the referees */
    public void getRefNamesFromGame(String gameId, RefereeNamesCallback callback){
        ArrayList<String> refIds = new ArrayList<>();
        ArrayList<String> refNames = new ArrayList<>();

        gamesRef.child(gameId).child("referees")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        refIds.clear();
                        refNames.clear();
                        for (DataSnapshot refSnap : snapshot.getChildren()) {
                            String refId = refSnap.getKey(); // uid
                            String refName = refSnap.getValue(String.class); // name
                            refIds.add(refId);
                            refNames.add(refName);
                        }
                        callback.onCallback(refIds, refNames);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("Firebase", error.getMessage());
                    }
                });

    }

    /*Given game id, returne the team ids */
    public void getTeamIdsFromGame(String gameId, TeamIdsFromGameCallback callback) {
        gamesRef.child(gameId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String teamAid = snapshot.child("teamAid").getValue(String.class);
                    String teamBid = snapshot.child("teamBid").getValue(String.class);
                    callback.onCallback(teamAid, teamBid);
                } else {
                    callback.onCallback(null, null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Error fetching team IDs: " + error.getMessage());
            }
        });
    }

    /*Method that gets game stats */
    public void observeMatchReport(String gameId, MatchReportCallback callback) {
        gamesRef.child(gameId).child("matchReport").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    MatchReport report = snapshot.getValue(MatchReport.class);
                    callback.onCallback(report);
                } else {
                    callback.onCallback(null);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "MatchReport update failed: " + error.getMessage());
            }
        });
    }



}