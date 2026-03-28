package com.example.playcheck.activityfiles;

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

import com.example.playcheck.Database.TeamLinkToDatabase;
import com.example.playcheck.Database.UserLinkToDatabase;
import com.example.playcheck.R;
import com.example.playcheck.Database.OrganizerLinkToDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class CreateTeamOrganizer extends AppCompatActivity{
    private DatabaseReference databaseRef;

    EditText teamNameEditText;
    AutoCompleteTextView playerSearchBar, captainSeachBar;
    Button addPlayerButton, createTeamButton;
    RecyclerView addedPlayersRecyclerView;
    ArrayList<String> playerIds = new ArrayList<>();
    ArrayList<String> currentAddedPlayerIds = new ArrayList<>();
    ArrayList<String> currentAddedPlayerNames = new ArrayList<>();
    ArrayList<String> playerNames = new ArrayList<>();

    String CaptainName;

    String CaptainID;

    UserLinkToDatabase user;
    TeamLinkToDatabase team;

    @Override
    public void onStart() {super.onStart();}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_team);

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid(); //get current user

        team = new TeamLinkToDatabase();
        user = new UserLinkToDatabase();

        teamNameEditText = findViewById(R.id.teamName);
        addPlayerButton = findViewById(R.id.btnAddPlayer);
        createTeamButton = findViewById(R.id.btnCreateTeam);
        addedPlayersRecyclerView = findViewById(R.id.AddedPlayers);
        captainSeachBar = (AutoCompleteTextView) findViewById(R.id.searchCaptain);

        //recycleview for added players so far
        AddedPlayersAdapter adapter = new AddedPlayersAdapter(currentAddedPlayerNames, true);
        addedPlayersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        addedPlayersRecyclerView.setAdapter(adapter);

        user.getPlayerIDs(new UserLinkToDatabase.PlayerIdCallback() { //get player ids
            @Override
            public void onCallback(ArrayList<String> ids) {
                playerIds = ids;

                user.getPlayerNames(new UserLinkToDatabase.PlayerNameCallback() {//get player names
                    @Override
                    public void onCallback(ArrayList<String> names) {
                        playerNames = names;

                        //since this is an organizer creating a team, they are not added to the team

                        //create search bar for adding players
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(CreateTeamOrganizer.this,android.R.layout.simple_dropdown_item_1line, playerNames);
                        playerSearchBar = (AutoCompleteTextView)findViewById(R.id.searchPlayer);
                        playerSearchBar.setThreshold(1); //start searching from first character
                        playerSearchBar.setAdapter(adapter);


                    }
                });

            }
        });


        //add player to team and update recycleview
        addPlayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String selectedPlayerName = playerSearchBar.getText().toString();

                if (!playerNames.contains(selectedPlayerName)) {
                    Toast.makeText(CreateTeamOrganizer.this, "Player not found", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(CreateTeamOrganizer.this, "Player is already added to Team", Toast.LENGTH_SHORT).show();
                }

                //create search bar for adding captain (gets updated when player is added to team)
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(CreateTeamOrganizer.this,android.R.layout.simple_dropdown_item_1line, currentAddedPlayerNames);
                captainSeachBar.setThreshold(1);
                captainSeachBar.setAdapter(adapter);

            }
        });

        //create team
        createTeamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String teamName = teamNameEditText.getText().toString().trim();
                String captainName = captainSeachBar.getText().toString().trim();

                int indexOfCaptainId = currentAddedPlayerNames.indexOf(captainName);

                if (indexOfCaptainId < 0){
                    Toast.makeText(CreateTeamOrganizer.this, "Player need to be in team before assigning it to captain", Toast.LENGTH_SHORT).show();
                    return;
                }
                String captainId = currentAddedPlayerIds.get(indexOfCaptainId);


                if (teamName.isEmpty()) {
                    Toast.makeText(CreateTeamOrganizer.this, "Enter a team name", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (captainName.isEmpty()) {
                    Toast.makeText(CreateTeamOrganizer.this, "Team needs a captain", Toast.LENGTH_SHORT).show();
                    return;
                }

                team.createTeam(currentAddedPlayerIds, currentAddedPlayerNames, captainId, captainName, teamName, task -> {
                    if(task.isSuccessful()){
                        Toast.makeText(CreateTeamOrganizer.this, "Team Created", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(CreateTeamOrganizer.this, "Cannot Create Team: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });





            }
        });



    }

}