package com.example.playcheck.activityfiles;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.playcheck.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CreateGameActivity extends AppCompatActivity {

    AutoCompleteTextView teamA, teamB;
    EditText venue, type;
    Button saveGame, backBtn, dateBtn, timeBtn;
    DatePickerDialog datePicker;
    TimePickerDialog timePicker;

    int hour, minute;

    DatabaseReference gamesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_game);
        initializeDatePicker();

        gamesRef = FirebaseDatabase.getInstance()
                .getReference("games");

        teamA = findViewById(R.id.teamA);
        teamB = findViewById(R.id.teamB);
        venue = findViewById(R.id.gameVenue);
        type = findViewById(R.id.gameType);
        dateBtn = findViewById(R.id.gameDateBtn);
        timeBtn = findViewById(R.id.gameTimeBtn);
        saveGame = findViewById(R.id.saveGame);

        //back to previous page button
        backBtn = findViewById(R.id.backBtnCreateGame);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        saveGame.setOnClickListener(v -> saveGame());
    }

    // Initialize Date Picker
    private void initializeDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                month = month + 1;
                String date = createDateString(dayOfMonth, month, year);
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

    private void saveGame() {

        String teamAVal = teamA.getText().toString();
        String teamBVal = teamB.getText().toString();
        String venueVal = venue.getText().toString();
        String typeVal = type.getText().toString();
        //long dateVal = Long.parseLong(date.getText().toString());

        String gameId = gamesRef.push().getKey();

        Map<String,Object> game = new HashMap<>();

        game.put("teamA", teamAVal);
        game.put("teamB", teamBVal);
        game.put("gameVenue", venueVal);
        game.put("gameType", typeVal);
        //game.put("gameDate", dateVal);

        gamesRef.child(gameId).setValue(game)
                .addOnSuccessListener(unused ->
                        Toast.makeText(this,"Game Created",Toast.LENGTH_SHORT).show());
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