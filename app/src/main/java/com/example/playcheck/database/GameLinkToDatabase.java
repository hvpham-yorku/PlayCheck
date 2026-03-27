package com.example.playcheck.Database;

import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.playcheck.activityfiles.AdapterGameList;
import com.example.playcheck.activityfiles.CreateGameActivity;
import com.example.playcheck.puremodel.Game;
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
                    boolean isCreator = uid.equals(info.getGameCreator());
                    boolean isParticipant = info.getPlayers() != null && info.getPlayers().containsKey(uid);
                    if (isCreator || isParticipant) {
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
    public void createGame(String teamAid, String teamBid, String teamA, String teamB, String gameVenue, String sport, long gameDateTime, ArrayList<String> playerIds, ArrayList<String> playerNames,OnCompleteListener<Void> listener){
        String gameId = gamesRef.push().getKey();

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Map<String,Object> game = new HashMap<>();

        Map<String, String> playersMap = new HashMap<>();
        for (int i = 0; i < playerIds.size(); i++) {
            playersMap.put(playerIds.get(i), playerNames.get(i));
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

        gamesRef.child(gameId).setValue(game).addOnCompleteListener(listener);

    }


}