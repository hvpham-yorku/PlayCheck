package com.example.playcheck.activityfiles;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.playcheck.Database.UserLinkToDatabase;
import com.example.playcheck.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
import java.util.Map;

public class ProfileSetup extends AppCompatActivity {

    TextInputEditText firstNameEdit, lastNameEdit, dobEdit, usernameEdit;
    AutoCompleteTextView genderDropdown;
    Button saveBtn;

    private UserLinkToDatabase userDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setup);

        userDb = new UserLinkToDatabase();

        firstNameEdit = findViewById(R.id.firstName);
        lastNameEdit = findViewById(R.id.lastName);
        dobEdit = findViewById(R.id.dob);
        usernameEdit = findViewById(R.id.username);
        genderDropdown = findViewById(R.id.genderDropdown);
        saveBtn = findViewById(R.id.btnSaveProfile);

        // Gender dropdown adapter
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.gender_choices)
        );
        genderDropdown.setAdapter(genderAdapter);
        genderDropdown.setKeyListener(null); // optional: force choosing

        saveBtn.setOnClickListener(v -> {
            String firstName = String.valueOf(firstNameEdit.getText()).trim();
            String dob = String.valueOf(dobEdit.getText()).trim();
            String gender = String.valueOf(genderDropdown.getText()).trim();
            String lastName = String.valueOf(lastNameEdit.getText()).trim();
            String username = String.valueOf(usernameEdit.getText()).trim();

            if (TextUtils.isEmpty(firstName)) {
                Toast.makeText(this, "Enter first name", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(lastName)) {
                Toast.makeText(this, "Enter last name", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(dob)) {
                Toast.makeText(this, "Enter date of birth", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(gender)) {
                Toast.makeText(this, "Select gender", Toast.LENGTH_SHORT).show();
                return;
            }

            if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                Toast.makeText(this, "Error: Not Logged In", Toast.LENGTH_LONG).show();
            if (TextUtils.isEmpty(username)) {
                Toast.makeText(this, "Enter username", Toast.LENGTH_SHORT).show();
                return;
            }

            }

            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            String accountType = getIntent().getStringExtra("accountType");
            if (TextUtils.isEmpty(accountType)){
                Toast.makeText(this, "Error: No Account Type", Toast.LENGTH_LONG).show();
                return;
            }

            Map<String, Object> profile = new HashMap<>();
            profile.put("firstName", firstName);
            profile.put("lastName", lastName);
            profile.put("dob", dob);
            profile.put("gender", gender);
            profile.put("email", email);
            profile.put("accountType", accountType);
            profile.put("username", username);

            userDb.saveUserProfile(uid, accountType, profile)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(this, "Profile saved!", Toast.LENGTH_SHORT).show();
                        // Will connect to a home screen that specifically is for that account type
                        Intent nextIntent = null;
                        if (accountType.equals("Organizer")) {
                            nextIntent = new Intent(this, Homepage_Controller.class);
                        }
                        else if (accountType.equals("Player")) {
                            nextIntent = new Intent(this, Homepage_Controller.class);
                        }
                        else if (accountType.equals("Referee")) {
                            startActivity(new Intent(this, Homepage_Controller.class));
                        }

                        if (nextIntent != null) {
                            startActivity(nextIntent);
                            finish();
                        }
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Save failed: " + e.getMessage(), Toast.LENGTH_LONG).show()
                    );


        });
    }
}