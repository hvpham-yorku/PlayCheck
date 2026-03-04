package com.example.playcheck.activityfiles;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.playcheck.R;
import com.example.playcheck.puremodel.Organizer;
import com.example.playcheck.puremodel.User;

import java.time.LocalDate;

public class OrganizerActivity extends AppCompatActivity {

    User theOrganizer;
    Button orgProfileSubmitButton;
    RadioButton maleButton, femaleButton;  // Changed to RadioButton
    RadioGroup genderRadioGroup;
    EditText firstNameTextField, lastNameTextField, dateOfBirthTextField, userNameTextField;
    EditText teamNameTextField, leagueNameTextField; // NEW

    // TODO: 2026-03-03 Improve your code structure and move all implementations of the database functions to the OrganizerLinkToDatabase
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer);

        theOrganizer = new Organizer();

        // Initialize views
        firstNameTextField = findViewById(R.id.firstNameInput);
        lastNameTextField = findViewById(R.id.lastNameInput);
        dateOfBirthTextField = findViewById(R.id.dateOfBirthInput);
        maleButton = findViewById(R.id.maleButton);
        femaleButton = findViewById(R.id.femaleButton);
        genderRadioGroup = findViewById(R.id.genderRadioGroup);
        userNameTextField = findViewById(R.id.userNameTextField);

        // NEW fields
        teamNameTextField = findViewById(R.id.teamNameInput);
        leagueNameTextField = findViewById(R.id.leagueNameInput);

        orgProfileSubmitButton = findViewById(R.id.orgProfileSubmitButton);

        // Handle gender selection through RadioGroup only
        genderRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.maleButton) {

                    Log.d("tag", "male button selected");
                    theOrganizer.setGender("male");
                } else if (checkedId == R.id.femaleButton) {
                    Log.d("tag", "female button selected");
                    theOrganizer.setGender("female");
                } else {
                    // checkedId == -1 means nothing selected
                    Log.d("tag", "gender deselected");
                    theOrganizer.setGender(null);
                }
            }
        });

        // Handle submit button click
        orgProfileSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitForm();

            }
        });

    }

    private void submitForm() {
        // Get all input values
        String fName = firstNameTextField.getText().toString().trim();
        String lName = lastNameTextField.getText().toString().trim();
        String fdOb = dateOfBirthTextField.getText().toString().trim();
        String fuserName = userNameTextField.getText().toString().trim();

        // NEW input values
        String fTeamName = teamNameTextField.getText().toString().trim();
        String fLeagueName = leagueNameTextField.getText().toString().trim();

        Log.d("tag", "Form submitted with values: " + fName + ", " + lName + ", " + fdOb + ", " + fuserName
                + ", " + fTeamName + ", " + fLeagueName);
        Log.d("tag", "Current gender: " + theOrganizer.getGender());

        // Validate all fields are filled
        if (fName.isEmpty() || lName.isEmpty() || fdOb.isEmpty() ||
                fuserName.isEmpty() || fTeamName.isEmpty() || fLeagueName.isEmpty() ||
                theOrganizer.getGender() == null) {

            Log.d("tag", "Validation failed - some fields are empty");
            Toast.makeText(this, "Please fill all fields and select gender",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Set name
            theOrganizer.setName(fName, lName);
            Log.d("tag", "Name set successfully");

            // Parse and set date of birth
            LocalDate dob = LocalDate.parse(fdOb);
            theOrganizer.setDateOfBirth(dob);
            Log.d("tag", "DOB set successfully: " + dob);

            // Set username
            theOrganizer.setUserId(fuserName);
            Log.d("tag", "Username set successfully");

            // Set team name + league name
            ((Organizer) theOrganizer).setTeamName(fTeamName);
            ((Organizer) theOrganizer).setLeagueName(fLeagueName);
            Log.d("tag", "Team + League set successfully");


            // Navigate to next page
            Log.d("tag", "Attempting to navigate to MainActivity");
            Intent goToNextPage = new Intent(this, MainActivity.class);
            startActivity(goToNextPage);
            Log.d("tag", "Navigation intent sent");
            Toast.makeText(OrganizerActivity.this,"Account created.",Toast.LENGTH_SHORT).show();
            finish(); // Optional: close this activity
            Log.d("tag", "Activity finished");

        } catch (Exception e) {
            // Handle date parsing errors
            Log.e("OrganizerActivity", "Error parsing date", e);
            Toast.makeText(this, "Invalid date format. Use yyyy-mm-dd",
                    Toast.LENGTH_SHORT).show();

        }
    }
}