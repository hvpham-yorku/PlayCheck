package com.example.playcheck.activityfiles;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.playcheck.Database.GameLinkToDatabase;
import com.example.playcheck.Database.TeamLinkToDatabase;
import com.example.playcheck.Database.UserLinkToDatabase;
import com.example.playcheck.R;
import com.example.playcheck.puremodel.Game;

public class CreateGameActivity extends AppCompatActivity {

    AutoCompleteTextView teamA, teamB, refSearchBar;
    EditText venue, type;
    Button saveGame, dateBtn, timeBtn, btnAddReferee;
    DatePickerDialog datePicker;
    TimePickerDialog timePicker;
    RecyclerView addedRefereesRecycleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_game);
        initializeDatePicker();

        teamA = (AutoCompleteTextView)findViewById(R.id.teamA);
        teamB = (AutoCompleteTextView)findViewById(R.id.teamB);
        venue = findViewById(R.id.gameVenue);
        type = findViewById(R.id.gameType);
        dateBtn = findViewById(R.id.gameDateBtn);
        timeBtn = findViewById(R.id.gameTimeBtn);
        btnAddReferee = findViewById(R.id.btnAddReferee);
        saveGame = findViewById(R.id.saveGame);
        refSearchBar = (AutoCompleteTextView)findViewById(R.id.searchReferee);

        addedRefereesRecycleView = findViewById(R.id.refereeRecyclerView);

        //recycleview for added refs so far (reusing an existing adapter)
        AddedPlayersAdapter adapter = new AddedPlayersAdapter(currentAddedRefNames, true);
        addedRefereesRecycleView.setLayoutManager(new LinearLayoutManager(this));
        addedRefereesRecycleView.setAdapter(adapter);

        backBtn = findViewById(R.id.backBtnCreateGame);
        backBtn.setOnClickListener(view -> finish());

            if (refNames.contains(selectedRef)) {
                if (!currentAddedRefNames.contains(selectedRef)) {
                    currentAddedRefNames.add(selectedRef);

                    int index = refNames.indexOf(selectedRef);
                    currentAddedRefIds.add(refIds.get(index));

                    // Refresh the UI of added referees
                    adapter.notifyDataSetChanged();
                    refSearchBar.setText("");
                } else {
                    Toast.makeText(this, "Referee already added", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Referee does not exist", Toast.LENGTH_SHORT).show();
            }
        });

        saveGame.setOnClickListener(v -> performSaveGame());
    }

    private void performSaveGame() {
        String teamAVal = teamA.getText().toString();
        String teamBVal = teamB.getText().toString();
        String venueVal = venue.getText().toString();
        String typeVal = type.getText().toString();
        String dateStr = date.getText().toString();

        if (teamAVal.isEmpty() || teamBVal.isEmpty() || venueVal.isEmpty() || typeVal.isEmpty() || dateStr.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        long dateVal;
        try {
            dateVal = Long.parseLong(dateStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid date format", Toast.LENGTH_SHORT).show();
            return;
        }

        Game game = new Game(teamAVal, teamBVal, dateVal, venueVal, typeVal);
        game.save().thenAccept(unused -> {
            runOnUiThread(() -> {
                Toast.makeText(this, "Game Created", Toast.LENGTH_SHORT).show();
                finish();
            });
        }).exceptionally(throwable -> {
            runOnUiThread(() -> Toast.makeText(this, "Failed to create game: " + throwable.getMessage(), Toast.LENGTH_SHORT).show());
            return null;
        });
    }
}
