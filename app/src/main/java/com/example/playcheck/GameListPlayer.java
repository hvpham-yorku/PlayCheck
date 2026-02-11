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

public class GameListPlayer extends AppCompatActivity {

    DatabaseReference gamedetails;
    RecyclerView recyclerView;
    ArrayList<GameListPlayerInfo> games;

    AdapterGameListPlayer adapter;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gamelist);
        recyclerView = findViewById(R.id.gamelist);
        gamedetails = FirebaseDatabase.getInstance("https://recycleviewgamelistplayer-default-rtdb.firebaseio.com/").getReference("games"); //points to the "games" folder in database
        games = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdapterGameListPlayer(this, games);
        recyclerView.setAdapter(adapter);

        gamedetails.addValueEventListener(new ValueEventListener() {
            @Override
            //when games are added/deleted in firebase, the game list gets updated
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                games.clear(); //clear list before updating it again
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    GameListPlayerInfo info = dataSnapshot.getValue(GameListPlayerInfo.class);
                    games.add(info);
                }
                Log.d("FirebaseTest", "Total items found: " + snapshot.getChildrenCount());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }



}
