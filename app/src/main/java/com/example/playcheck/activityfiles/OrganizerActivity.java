package com.example.playcheck.activityfiles;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.playcheck.R;
import com.example.playcheck.puremodel.Organizer;

import java.time.LocalDate;

public class OrganizerActivity extends AppCompatActivity {

    private static final String TAG = "OrganizerActivity";

    private Organizer theOrganizer;

    private Button orgProfileSubmitButton;
    private RadioButton maleButton, femaleButton;
    private RadioGroup genderRadioGroup;

    private EditText firstNameTextField;
    private EditText lastNameTextField;
    private EditText dateOfBirthTextField;
    private EditText userNameTextField;
    private EditText teamNameTextField;
    private EditText leagueNameTextField;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer);

        theOrganizer = new Organizer();

        initializeViews();
        setupGenderSelection();
        setupSubmitButton();
    }

    private void initializeViews() {
        firstNameTextField = findViewById(R.id.firstNameInput);
        lastNameTextField = findViewById(R.id.lastNameInput);
        dateOfBirthTextField = findViewById(R.id.dateOfBirthInput);
        maleButton = findViewById(R.id.maleButton);
        femaleButton = findViewById(R.id.femaleButton);
        genderRadioGroup = findViewById(R.id.genderRadioGroup);
        userNameTextField = findViewById(R.id.userNameTextField);
        teamNameTextField = findViewById(R.id.teamNameInput);
        leagueNameTextField = findViewById(R.id.leagueNameInput);
        orgProfileSubmitButton = findViewById(R.id.orgProfileSubmitButton);
    }

    private void setupGenderSelection() {
        genderRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.maleButton) {
                Log.d(TAG, "male button selected");
                theOrganizer.setGender("male");
            } else if (checkedId == R.id.femaleButton) {
                Log.d(TAG, "female button selected");
                theOrganizer.setGender("female");
            } else {
                Log.d(TAG, "gender deselected");
                theOrganizer.setGender(null);
            }
        });
    }

    private void setupSubmitButton() {
        orgProfileSubmitButton.setOnClickListener(v -> submitForm());
    }

    private void submitForm() {
        String firstName = firstNameTextField.getText().toString().trim();
        String lastName = lastNameTextField.getText().toString().trim();
        String dateOfBirth = dateOfBirthTextField.getText().toString().trim();
        String userName = userNameTextField.getText().toString().trim();
        String teamName = teamNameTextField.getText().toString().trim();
        String leagueName = leagueNameTextField.getText().toString().trim();

        Log.d(TAG, "Form submitted with values: " +
                firstName + ", " + lastName + ", " + dateOfBirth + ", " +
                userName + ", " + teamName + ", " + leagueName);
        Log.d(TAG, "Current gender: " + theOrganizer.getGender());

        if (!isFormValid(firstName, lastName, dateOfBirth, userName, teamName, leagueName)) {
            Log.d(TAG, "Validation failed - some fields are empty");
            Toast.makeText(this, "Please fill all fields and select gender", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            populateOrganizer(firstName, lastName, dateOfBirth, userName, teamName, leagueName);
            goToMainActivity();
        } catch (Exception e) {
            Log.e(TAG, "Error parsing date", e);
            Toast.makeText(this, "Invalid date format. Use yyyy-mm-dd", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isFormValid(String firstName, String lastName, String dateOfBirth,
                                String userName, String teamName, String leagueName) {
        return !firstName.isEmpty()
                && !lastName.isEmpty()
                && !dateOfBirth.isEmpty()
                && !userName.isEmpty()
                && !teamName.isEmpty()
                && !leagueName.isEmpty()
                && theOrganizer.getGender() != null;
    }

    private void populateOrganizer(String firstName, String lastName, String dateOfBirth,
                                   String userName, String teamName, String leagueName) {
        theOrganizer.setName(firstName, lastName);
        Log.d(TAG, "Name set successfully");

        LocalDate dob = LocalDate.parse(dateOfBirth);
        theOrganizer.setDateOfBirth(dob);
        Log.d(TAG, "DOB set successfully: " + dob);

        theOrganizer.setUserId(userName);
        Log.d(TAG, "Username set successfully");

        theOrganizer.setTeamName(teamName);
        theOrganizer.setLeagueName(leagueName);
        Log.d(TAG, "Team + League set successfully");
    }

    private void goToMainActivity() {
        Log.d(TAG, "Attempting to navigate to MainActivity");
        Intent goToNextPage = new Intent(this, MainActivity.class);
        startActivity(goToNextPage);
        Log.d(TAG, "Navigation intent sent");
        Toast.makeText(this, "Account created.", Toast.LENGTH_SHORT).show();
        finish();
        Log.d(TAG, "Activity finished");
    }
}