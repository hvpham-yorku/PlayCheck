package com.example.playcheck.dataBaseLinkFiles;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.playcheck.puremodel.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class OrganizerLinkToDatabase extends UserLinkToDatabase{
    OrganizerLinkToDatabase(User theUser) {
        super(theUser);
    }

    /* Interfaces used for callbacks*/
    public interface PlayerIdCallback {
        void onCallback(ArrayList<String> playerIds);
    }

    public interface PlayerNameCallback {
        void onCallback(ArrayList<String> names);
    }

    /* Method that returns all ids for players in the database */
    public static void getPlayerIDs(final PlayerIdCallback callback){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child("Player");
        ArrayList<String> playerIds = new ArrayList<>();

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                playerIds.clear();

                    for (DataSnapshot idSnapshot : snapshot.getChildren()) {
                        String playerId = idSnapshot.getKey();
                        playerIds.add(playerId);
                    }

                callback.onCallback(playerIds); //return data
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", error.getMessage());
            }
        });

    }

    /* Method that returns all player names in the database */
    public static void getPlayerNames(PlayerNameCallback callback) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child("Player");

        ArrayList<String> playerNames = new ArrayList<>();

        ref.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {
                playerNames.clear();

                for (DataSnapshot playerSnapshot : snapshot.getChildren()) {
                    String firstName = playerSnapshot.child("profile").child("firstName").getValue(String.class);
                    String lastName = playerSnapshot.child("profile").child("lastName").getValue(String.class);
                    playerNames.add(firstName + " " + lastName);
                    Log.d("FirebaseTest", firstName + " " + lastName);
                }

                callback.onCallback(playerNames);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("Firebase", error.getMessage());
            }
        });
    }
}
