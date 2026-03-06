package com.example.playcheck.activityfiles;

import static com.example.playcheck.dataBaseLinkFiles.OrganizerLinkToDatabase.getPlayerIDs;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.playcheck.R;
import com.example.playcheck.dataBaseLinkFiles.OrganizerLinkToDatabase;
import com.example.playcheck.dataBaseLinkFiles.PlayerLinkToDatabase;
import com.example.playcheck.puremodel.Game;
import com.example.playcheck.puremodel.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class CreateGame extends AppCompatActivity {
    private DatabaseReference databaseRef;

    EditText teamNameEditText;
    AutoCompleteTextView playerSearchBar;
    Button addPlayerButton, createTeamButton;
    RecyclerView addedPlayersRecyclerView;
    ArrayList<String> playerIds = new ArrayList<>();
    ArrayList<String> currentAddedPlayerIds = new ArrayList<>();
    ArrayList<String> currentAddedPlayerNames = new ArrayList<>();
    ArrayList<String> playerNames = new ArrayList<>();

    @Override
    public void onStart() {super.onStart();}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_team);

        databaseRef = FirebaseDatabase.getInstance("https://recycleviewgamelistplayer-default-rtdb.firebaseio.com/").getReference("users");

        teamNameEditText = findViewById(R.id.teamName);
        addPlayerButton = findViewById(R.id.btnAddPlayer);
        createTeamButton = findViewById(R.id.btnCreateTeam);
        addedPlayersRecyclerView = findViewById(R.id.AddedPlayers);

        //recycleview for added players so far
        AddedPlayersAdapter adapter = new AddedPlayersAdapter(currentAddedPlayerNames);
        addedPlayersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        addedPlayersRecyclerView.setAdapter(adapter);

        OrganizerLinkToDatabase.getPlayerIDs(new OrganizerLinkToDatabase.PlayerIdCallback() { //get player ids
            @Override
            public void onCallback(ArrayList<String> ids) {
                playerIds = ids;

            }
        });

        OrganizerLinkToDatabase.getPlayerNames(new OrganizerLinkToDatabase.PlayerNameCallback() {//get player names
            @Override
            public void onCallback(ArrayList<String> names) {
                playerNames = names;

                //create search bar
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(CreateGame.this,android.R.layout.simple_dropdown_item_1line, playerNames);
                playerSearchBar = (AutoCompleteTextView)findViewById(R.id.searchPlayer);
                playerSearchBar.setThreshold(1); //start searching from first character
                playerSearchBar.setAdapter(adapter);

            }
        });

        //add player to team and update recycleview
        addPlayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String selectedPlayerName = playerSearchBar.getText().toString();

                if (!playerNames.contains(selectedPlayerName)) {
                    Toast.makeText(CreateGame.this, "Player not found", Toast.LENGTH_SHORT).show();
                    return;
                }

                currentAddedPlayerNames.add(selectedPlayerName);

                int indexOfSelectedPlayer = playerNames.indexOf(selectedPlayerName);
                String selectedPlayerId = playerIds.get(indexOfSelectedPlayer);

                if (!currentAddedPlayerIds.contains(selectedPlayerId)){
                    currentAddedPlayerIds.add(selectedPlayerId);
                    adapter.notifyItemInserted(currentAddedPlayerIds.size() - 1); //change recycle view since player is added
                    playerSearchBar.setText("");
                } else {
                    Toast.makeText(CreateGame.this, "Player is already added to Team", Toast.LENGTH_SHORT).show();
                }

            }
        });

        //create team
        createTeamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
            }
        });



    }

}
