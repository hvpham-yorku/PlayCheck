package com.example.playcheck.activityfiles;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.playcheck.R;
import com.example.playcheck.puremodel.Referee;
import com.example.playcheck.puremodel.User;

import java.time.LocalDate;

public class RefereeActivity extends AppCompatActivity implements View.OnClickListener {

    User theReferee;
    Button refProfileSubmitButton;
    RadioButton maleButton, femaleButton;  // Changed to RadioButton
    EditText firstNameTextField, lastNameTextField, dateOfBirthTextField, userNameTextField;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_referee);

        theReferee = new Referee();

        // Initialize views
        maleButton = findViewById(R.id.maleButton);
        femaleButton = findViewById(R.id.femaleButton);
        refProfileSubmitButton = findViewById(R.id.refProfileSubmitButton);

        firstNameTextField = findViewById(R.id.firstNameInput);
        lastNameTextField = findViewById(R.id.lastNameInput);
        dateOfBirthTextField = findViewById(R.id.dateOfBirthInput);
        userNameTextField = findViewById(R.id.userNameTextField);

        // Set click listeners
        maleButton.setOnClickListener(this);
        femaleButton.setOnClickListener(this);
        refProfileSubmitButton.setOnClickListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View view) {
        int id = view.getId();

        // Handle gender selection separately
        if (id == R.id.maleButton) {
            Log.d("tag", "male button was selected");
            theReferee.setGender("male");
            return;  // Exit early, don't process submit logic
        }
        else if (id == R.id.femaleButton) {
            Log.d("tag", "female button was selected");
            theReferee.setGender("female");
            return;  // Exit early, don't process submit logic
        }
        else if (id == R.id.refProfileSubmitButton) {
            // Handle form submission
            submitForm();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void submitForm() {
        // Get all input values
        String fName = firstNameTextField.getText().toString().trim();
        String lName = lastNameTextField.getText().toString().trim();
        String fdOb = dateOfBirthTextField.getText().toString().trim();
        String fuserName = userNameTextField.getText().toString().trim();

        // Validate all fields are filled
        if (fName.isEmpty() || lName.isEmpty() || fdOb.isEmpty() ||
                fuserName.isEmpty() || theReferee.getGender() == null) {

            Toast.makeText(this, "Please fill all fields and select gender",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Set name
            theReferee.setName(fName, lName);

            // Parse and set date of birth
            LocalDate dob = LocalDate.parse(fdOb);
            theReferee.setDateOfBirth(dob);

            // Set username
            theReferee.setUserId(fuserName);

            // Navigate to next page
            Intent goToNextPage = new Intent(this, MainActivity.class);
            startActivity(goToNextPage);
            finish(); // Optional: close this activity

        } catch (Exception e) {
            // Handle date parsing errors
            Toast.makeText(this, "Invalid date format. Use yyyy-mm-dd",
                    Toast.LENGTH_SHORT).show();
            Log.e("RefereeActivity", "Error parsing date", e);
        }
    }
}