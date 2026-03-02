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
import com.example.playcheck.puremodel.Referee;
import com.example.playcheck.puremodel.User;

import java.time.LocalDate;

public class RefereeActivity extends AppCompatActivity {

    User theReferee;
    Button refProfileSubmitButton;
    RadioButton maleButton, femaleButton;  // Changed to RadioButton
    RadioGroup genderRadioGroup;
    EditText firstNameTextField, lastNameTextField, dateOfBirthTextField, userNameTextField;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_referee);

        theReferee = new Referee();

        // Initialize views
        firstNameTextField = findViewById(R.id.firstNameInput);
        lastNameTextField = findViewById(R.id.lastNameInput);
        dateOfBirthTextField = findViewById(R.id.dateOfBirthInput);
        maleButton = findViewById(R.id.maleButton);
        femaleButton = findViewById(R.id.femaleButton);
        genderRadioGroup = findViewById(R.id.genderRadioGroup);
        userNameTextField = findViewById(R.id.userNameTextField);
        refProfileSubmitButton = findViewById(R.id.refProfileSubmitButton);


        // Handle gender selection through RadioGroup only
        genderRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.maleButton) {

                    Log.d("tag", "male button selected");
                    theReferee.setGender("male");
                } else if (checkedId == R.id.femaleButton) {
                    Log.d("tag", "female button selected");
                    theReferee.setGender("female");
                } else {
                    // checkedId == -1 means nothing selected
                    Log.d("tag", "gender deselected");
                    theReferee.setGender(null);
                }
            }
        });

        // Handle submit button click
        refProfileSubmitButton.setOnClickListener(new View.OnClickListener() {
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

        Log.d("tag", "Form submitted with values: " + fName + ", " + lName + ", " + fdOb + ", " + fuserName);
        Log.d("tag", "Current gender: " + theReferee.getGender());

        // Validate all fields are filled
        if (fName.isEmpty() || lName.isEmpty() || fdOb.isEmpty() ||
                fuserName.isEmpty() || theReferee.getGender() == null) {

            Log.d("tag", "Validation failed - some fields are empty");
            Toast.makeText(this, "Please fill all fields and select gender",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Set name
            theReferee.setName(fName, lName);
            Log.d("tag", "Name set successfully");

            // Parse and set date of birth
            LocalDate dob = LocalDate.parse(fdOb);
            theReferee.setDateOfBirth(dob);
            Log.d("tag", "DOB set successfully: " + dob);

            // Set username
            theReferee.setUserId(fuserName);
            Log.d("tag", "Username set successfully");


            // Navigate to next page
            Log.d("tag", "Attempting to navigate to RefereeHomeActivity");
            Intent goToNextPage = new Intent(this, RefereeHomeActivity.class);
            startActivity(goToNextPage);
            Log.d("tag", "Navigation intent sent");
            Toast.makeText(RefereeActivity.this,"Account created.",Toast.LENGTH_SHORT).show();
            finish(); // Optional: close this activity
            Log.d("tag", "Activity finished");

        } catch (Exception e) {
            // Handle date parsing errors
            Log.e("RefereeActivity", "Error parsing date", e);
            Toast.makeText(this, "Invalid date format. Use yyyy-mm-dd",
                    Toast.LENGTH_SHORT).show();

        }
    }
}