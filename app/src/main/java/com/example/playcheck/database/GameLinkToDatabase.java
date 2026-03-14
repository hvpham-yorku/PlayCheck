package com.example.playcheck.Database;

import androidx.annotation.NonNull;

import com.example.playcheck.activityfiles.AdapterGameList;
import com.example.playcheck.puremodel.Game;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class GameLinkToDatabase {
    /* Reusable method that gets game data from the database based on a Query object*/
    public static void getGameData(Query gamedata, ArrayList<Game> games, AdapterGameList adapter){ //DatabaseReference is a child class of Query so this is ok to do
        gamedata.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {//when games are added/deleted in Firebase, the ArrayList that stores the games gets updated
                games.clear(); //clear list before updating it again
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Game info = dataSnapshot.getValue(Game.class);
                    games.add(info);
                }
                adapter.notifyDataSetChanged(); //Tells the recycler view to update with new game list
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
