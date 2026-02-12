package com.example.playcheck;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Firebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/*
The GameListPlayer class creates the Game List page.
 */

public class GameListPlayer extends AppCompatActivity {


    DatabaseReference gamedetails;
    RecyclerView recyclerView;
    ArrayList<Games> games;
    AdapterGameListPlayer adapter;

    @Override
    public void onCreate(Bundle savedInstanceState){//called when the game list page starts
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gamelist);
        recyclerView = findViewById(R.id.gamelist);
        gamedetails = FirebaseDatabase.getInstance("https://recycleviewgamelistplayer-default-rtdb.firebaseio.com/").getReference("games"); //points to the "games" folder in database
        games = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdapterGameListPlayer(this, games); //creating a new adapter for this activity using data from 'games'
        recyclerView.setAdapter(adapter); //set the adapter that will be used to add data to games list

        gamedetails.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {//when games are added/deleted in Firebase, the ArrayList that stores the games gets updated
                games.clear(); //clear list before updating it again
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Games info = dataSnapshot.getValue(Games.class);
                    games.add(info);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }



}
