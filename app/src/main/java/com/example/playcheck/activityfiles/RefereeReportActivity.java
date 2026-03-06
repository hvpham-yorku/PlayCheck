package com.example.playcheck.activityfiles;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.playcheck.R;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;

public class RefereeReportActivity extends AppCompatActivity {

    EditText inputScore;
    EditText inputNotes;
    Button submitButton;

    DatabaseReference gamesRef;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_referee_report);

        inputScore = findViewById(R.id.inputScore);
        inputNotes = findViewById(R.id.inputNotes);
        submitButton = findViewById(R.id.submitButton);

        gamesRef = FirebaseDatabase.getInstance().getReference("games");

        submitButton.setOnClickListener(v -> {

            long gameId = getIntent().getLongExtra("gameId",0);

            String score = inputScore.getText().toString();
            String notes = inputNotes.getText().toString();

            if(gameId != 0){

                DatabaseReference reportRef = gamesRef.child(String.valueOf(gameId)).child("matchReport");

                reportRef.child("score").setValue(score);
                reportRef.child("notes").setValue(notes);

                Toast.makeText(this,"Report Submitted",Toast.LENGTH_SHORT).show();
                finish();
            }

        });
    }
}