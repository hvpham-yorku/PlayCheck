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

        // Set click listeners
//        maleButton.setOnClickListener(this);
//        femaleButton.setOnClickListener(this);
//        refProfileSubmitButton.setOnClickListener(this);



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

//    @Override
//    public void onClick(View view) {
//        int id = view.getId();
//
//        // Handle gender selection with toggle functionality
//        if (id == R.id.maleButton) {
//            // Check if this button is already selected
//            if (maleButton.isChecked() && "male".equals(theReferee.getGender())) {
//                // If already selected, unselect it
//                genderRadioGroup.clearCheck();
//                theReferee.setGender(null);
//                maleButton.setChecked(false);
//            }
//            // No else needed - RadioGroup will handle selection automatically
//        }
//        else if (id == R.id.femaleButton) {
//            // Check if this button is already selected
//            if (femaleButton.isChecked() && "female".equals(theReferee.getGender())) {
//                // If already selected, unselect it
//                genderRadioGroup.clearCheck();
//                theReferee.setGender(null);
//                femaleButton.setChecked(false);
//            }
//        }
//        else if (id == R.id.refProfileSubmitButton) {
//            // Handle form submission
//            submitForm();
//        }
//    }

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