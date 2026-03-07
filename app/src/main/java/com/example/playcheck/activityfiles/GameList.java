package com.example.playcheck.activityfiles;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.playcheck.R;
import com.example.playcheck.puremodel.Game;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.Instant;
import java.util.ArrayList;
import com.example.playcheck.database.GameLinkToDatabase;

/*
The GameListPlayer class creates the Game List page.
 */
// TODO: 2026-03-03 Improve your code structure and move all implementations of the database functions to the GameLinkToDatabase
public class GameList extends AppCompatActivity {


    DatabaseReference gamedetails;
    RecyclerView recyclerView;
    ArrayList<Game> games;
    AdapterGameList adapter;
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
        adapter = new AdapterGameList(this, games); //creating a new adapter for this activity using data from 'games'
        recyclerView.setAdapter(adapter); //set the adapter that will be used to add data to games list
        GameLinkToDatabaseHelper.getGameData(gamedetails, games, adapter);

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
                    GameLinkToDatabaseHelper.getGameData(gamedetails.orderByChild("gameDate").endBefore(current_dateTime), games, adapter); //get games before the current time
                } else if (clickedItem.equals("Upcoming Games")){
                    GameLinkToDatabaseHelper.getGameData(gamedetails.orderByChild("gameDate").startAt(current_dateTime), games, adapter); //get games during and after the current time
                } else if (clickedItem.equals("Game Venue (Alphabetical)")){
                    GameLinkToDatabaseHelper.getGameData(gamedetails.orderByChild("gameVenue"), games, adapter);
                } else if (clickedItem.equals("Game Type (Alphabetical)")){
                    GameLinkToDatabaseHelper.getGameData(gamedetails.orderByChild("gameType"), games, adapter);
                } else {
                    GameLinkToDatabaseHelper.getGameData(gamedetails, games, adapter); //show all games
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }

    /* Reusable method that gets the current DateTime (UTC) as a long. Also, note that times in database are UTC.*/
    public long getCurrentDateTimeAsLong(){
        Instant dt = Instant.now(); //gets the current instance on the timeline in UTC (so current datetime UTC)
        return dt.toEpochMilli(); //convert to milliseconds
    }



}
