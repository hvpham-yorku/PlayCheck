package com.example.playcheck.activityfiles;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.playcheck.R;
import com.example.playcheck.Database.OrganizerLinkToDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CreateTeam extends AppCompatActivity {
    private DatabaseReference databaseRef;

    EditText teamNameEditText;
    AutoCompleteTextView playerSearchBar;
    Button addPlayerButton, createTeamButton, backButton;
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
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        databaseRef = FirebaseDatabase.getInstance("https://recycleviewgamelistplayer-default-rtdb.firebaseio.com/").getReference("users");

        teamNameEditText = findViewById(R.id.teamName);
        addPlayerButton = findViewById(R.id.btnAddPlayer);
        createTeamButton = findViewById(R.id.btnCreateTeam);
        backButton = findViewById(R.id.backBtnCreateTeam);
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
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(CreateTeam.this,android.R.layout.simple_dropdown_item_1line, playerNames);
                playerSearchBar = (AutoCompleteTextView)findViewById(R.id.searchPlayer);
                playerSearchBar.setThreshold(1); //start searching from first character
                playerSearchBar.setAdapter(adapter);

            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //add player to team and update recycleview
        addPlayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String selectedPlayerName = playerSearchBar.getText().toString();

                if (!playerNames.contains(selectedPlayerName)) {
                    Toast.makeText(CreateTeam.this, "Player not found", Toast.LENGTH_SHORT).show();
                    return;
                }

                int indexOfSelectedPlayer = playerNames.indexOf(selectedPlayerName);
                String selectedPlayerId = playerIds.get(indexOfSelectedPlayer);

                if (!currentAddedPlayerIds.contains(selectedPlayerId)){
                    currentAddedPlayerIds.add(selectedPlayerId);
                    currentAddedPlayerNames.add(selectedPlayerName);
                    adapter.notifyItemInserted(currentAddedPlayerIds.size() - 1); //change recycle view since player is added
                    playerSearchBar.setText("");
                } else {
                    Toast.makeText(CreateTeam.this, "Player is already added to Team", Toast.LENGTH_SHORT).show();
                }

            }
        });

        //create team
        createTeamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String teamName = teamNameEditText.getText().toString().trim();

                if (teamName.isEmpty()) {
                    Toast.makeText(CreateTeam.this, "Enter a team name", Toast.LENGTH_SHORT).show();
                    return;
                }

                //create team id
                DatabaseReference teamsRef = FirebaseDatabase.getInstance().getReference("teams");
                String teamId = teamsRef.push().getKey();

                //add current player id to the team
                String uid = currentUser.getUid();
                currentAddedPlayerIds.add(uid);

                //add players to team
                Map<String, String> playersMap = new HashMap<>();
                int nop = 1;
                for (String playerId : currentAddedPlayerIds) {
                    playersMap.put(playerId, "player" + nop);
                    nop++;
                }

                //add team data to team name
                Map<String, Object> teamData = new HashMap<>();
                teamData.put("teamName", teamName);
                teamData.put("players", playersMap);

                teamsRef.child(teamId).setValue(teamData).addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Toast.makeText(CreateTeam.this, "Team Created", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(CreateTeam.this, "Cannot Create Team: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });





            }
        });



    }

}
