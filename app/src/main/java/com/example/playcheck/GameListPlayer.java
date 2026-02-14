package com.example.playcheck;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;

/*
The GameListPlayer class creates the Game List page.
 */

public class GameListPlayer extends AppCompatActivity {


    DatabaseReference gamedetails;
    RecyclerView recyclerView;
    ArrayList<Game> games;
    AdapterGameListPlayer adapter;
    private ArrayList<String> optionsList; //the options/filters that will be used for dropdown menu

    private GameListDropDownAdapter dropDownAdapter;



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
        getGameData(gamedetails);

        optionsList = new ArrayList<String>();
        optionsList.add("All Games");
        optionsList.add("Past Games");
        optionsList.add("Upcoming Games");
        optionsList.add("Game Venue (Alphabetical)");
        optionsList.add("Game Type (Alphabetical)");

        //connect dropdown adapter, optionsList data and dropdown UI together
        Spinner spinnerDropDown = findViewById(R.id.gameListSpinner);
        dropDownAdapter = new GameListDropDownAdapter(this, optionsList);
        spinnerDropDown.setAdapter(dropDownAdapter);

        spinnerDropDown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {//when filter is selected, it shows on the screen
                String clickedItem = (String) adapterView.getItemAtPosition(i); //gets the item stored in the adapter at index i
                long current_dateTime = getCurrentDateTimeAsLong();
                if (clickedItem.equals("Past Games")){
                    getGameData(gamedetails.orderByChild("gameDate").endBefore(current_dateTime)); //get games before the current time
                } else if (clickedItem.equals("Upcoming Games")){
                    getGameData(gamedetails.orderByChild("gameDate").startAt(current_dateTime)); //get games during and after the current time
                } else if (clickedItem.equals("Game Venue (Alphabetical)")){
                    getGameData(gamedetails.orderByChild("gameVenue"));
                } else if (clickedItem.equals("Game Type (Alphabetical)")){
                    getGameData(gamedetails.orderByChild("gameType"));
                } else {
                    getGameData(gamedetails); //show all games
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }

    /* Reusable method that gets game data from the database based on a Query object*/
    public void getGameData(Query gamedata){ //DatabaseReference is a child class of Query so this is ok to do
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

    /* Reusable method that gets the current DateTime (UTC) as a long. Also, note that times in database are UTC.*/
    public long getCurrentDateTimeAsLong(){
        Instant dt = Instant.now(); //gets the current instance on the timeline in UTC (so current datetime UTC)
        return dt.toEpochMilli(); //convert to milliseconds
    }



}
