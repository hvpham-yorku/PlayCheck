package com.example.playcheck;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class ProfileSetup extends AppCompatActivity {

    TextInputEditText firstNameEdit, dobEdit;
    AutoCompleteTextView genderDropdown;
    Button saveBtn;

    FirebaseAuth auth;
    DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setup);

        auth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference();

        firstNameEdit = findViewById(R.id.firstName);
        dobEdit = findViewById(R.id.dob);
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

            if (TextUtils.isEmpty(firstName)) {
                Toast.makeText(this, "Enter first name", Toast.LENGTH_SHORT).show();
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

            String uid = auth.getCurrentUser().getUid();

            Map<String, Object> profile = new HashMap<>();
            profile.put("firstName", firstName);
            profile.put("dob", dob);
            profile.put("gender", gender);

            dbRef.child("users")
                    .child(uid)
                    .child("profile")
                    .setValue(profile)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(this, "Profile saved!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, MainActivity.class));
                        finish();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Save failed: " + e.getMessage(), Toast.LENGTH_LONG).show()
                    );
        });
    }
}