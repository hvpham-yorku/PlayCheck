package com.example.playcheck.activityfiles;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.playcheck.Database.GameLinkToDatabase;
import com.example.playcheck.Database.TeamLinkToDatabase;
import com.example.playcheck.R;
import com.example.playcheck.puremodel.Game;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CreateGameActivity extends AppCompatActivity {

    AutoCompleteTextView teamA, teamB;
    EditText venue, type;
    Button saveGame, dateBtn, timeBtn;
    DatePickerDialog datePicker;
    TimePickerDialog timePicker;

    TeamLinkToDatabase teamsDB;
    GameLinkToDatabase gameToDB;
    int selectedYear, selectedMonth, selectedDay;
    int hour, minute;

    ArrayList<String> teamIDs;
    ArrayList<String> teamNames;

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
        saveGame = findViewById(R.id.saveGame);


        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        teamsDB = new TeamLinkToDatabase();
        gameToDB = new GameLinkToDatabase();

        //get team names and ids
        teamsDB.getTeamIDs(new TeamLinkToDatabase.TeamIdCallback() {
            @Override
            public void onCallback(ArrayList<String> teamIds) {
                teamIDs = teamIds;

                teamsDB.getTeamNames(new TeamLinkToDatabase.TeamNamesCallback() {
                    @Override
                    public void onCallback(ArrayList<String> allTeamNames) {
                        teamNames = allTeamNames;
                        //create search bar for teamA and teamB
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(CreateGameActivity.this,android.R.layout.simple_dropdown_item_1line, teamNames);
                        teamA.setThreshold(1);
                        teamA.setAdapter(adapter);
                        teamB.setThreshold(1);
                        teamB.setAdapter(adapter);
                    }
                });



            }
        });


        saveGame.setOnClickListener(v -> saveGame());
    }

    // Initialize Date Picker
    private void initializeDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                month = month + 1; //months are numbered 0 - 11 by default
                selectedYear = year;
                selectedMonth = month;
                selectedDay = dayOfMonth;
                String date = createDateString(dayOfMonth, selectedMonth, selectedYear);
                dateBtn.setText(date);

            }
        };

        //today's date is the default date
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        datePicker = new DatePickerDialog(CreateGameActivity.this, dateSetListener, year, month, day);

    }

    public void expandDatePicker(View view){ //date picker shows when date button is clicked
        datePicker.show();

    }

    public void expandTimePicker(View view){//time picker shows when time button is clicked
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int chosenHourOfDay, int chosenMinute) {
                hour = chosenHourOfDay;
                minute = chosenMinute;
                //format the time and display
                SimpleDateFormat pattern = new SimpleDateFormat("hh:mm a");
                String dateAndTime = pattern.format(new java.util.Date(0, 0, 0, hour, minute));
                timeBtn.setText(dateAndTime);

            }
        };

        timePicker = new TimePickerDialog(CreateGameActivity.this, onTimeSetListener, hour, minute, false);
        timePicker.show();

    }

    /*save game btn pressed */
    private void saveGame() {

        String teamAVal = teamA.getText().toString().trim();
        String teamBVal = teamB.getText().toString().trim();
        String venueVal = venue.getText().toString().trim();
        String typeVal = type.getText().toString().trim();

        //check if fields are empty
        if (teamAVal.isEmpty() || teamBVal.isEmpty() || venueVal.isEmpty() || typeVal.isEmpty()){
            Toast.makeText(CreateGameActivity.this, "One or more fields are empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedDay == 0 || selectedMonth == 0 || selectedYear == 0){
            Toast.makeText(CreateGameActivity.this, "Select a date and time", Toast.LENGTH_SHORT).show();
            return;
        }

        //get team ids
        int indexTeamA = teamNames.indexOf(teamAVal);
        int indexTeamB = teamNames.indexOf(teamBVal);

        if (indexTeamA < 0){
            Toast.makeText(CreateGameActivity.this, "Team A not Found", Toast.LENGTH_SHORT).show();
            return;
        } else if (indexTeamB < 0){
            Toast.makeText(CreateGameActivity.this, "Team B not Found", Toast.LENGTH_SHORT).show();
            return;
        }

        String teamAid = teamIDs.get(indexTeamA);
        String teamBid = teamIDs.get(indexTeamB);


        long dateTimeInt = (new Game()).getEpochTime(selectedYear, selectedMonth, selectedDay, hour, minute); //date and time as a long int

        //Create Game in DB
        teamsDB.getPlayersFromTeam(teamAid, (idsA, namesA) -> {
            teamsDB.getPlayersFromTeam(teamBid, (idsB, namesB) -> {

                // Merge lists together; all players names and ids are now in A
                idsA.addAll(idsB);
                namesA.addAll(namesB);

                gameToDB.createGame(teamAid, teamBid, teamAVal, teamBVal, venueVal, typeVal, dateTimeInt, idsA, namesA,
                        task -> {
                            if(task.isSuccessful()){
                                Toast.makeText(CreateGameActivity.this, "Game Created", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(CreateGameActivity.this, "Cannot Create Game: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                );

            });

        });
    }

    /* Both methods below format the date to be displayed to the UI */
    private String createDateString(int dayOfMonth, int month, int year) {
        return getFormatMonth(month) + " " + dayOfMonth + " " + year;
    }
    private String getFormatMonth(int month) {
        switch (month) {
            case 1: return "January";
            case 2: return "February";
            case 3: return "March";
            case 4: return "April";
            case 5: return "May";
            case 6: return "June";
            case 7: return "July";
            case 8: return "August";
            case 9: return "September";
            case 10: return "October";
            case 11: return "November";
            case 12: return "December";
            default: return "";
        }


    }

}