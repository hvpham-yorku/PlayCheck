package com.example.playcheck.activityfiles;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;


import com.example.playcheck.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class Registration extends AppCompatActivity {

    TextInputEditText editTextEmail, editTextPassword;
    Button registrationButton;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    TextView loginLink;
    AutoCompleteTextView dropdown;

    String email,password,accountType;



    @Override
    public void onStart() {super.onStart();}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        mAuth = FirebaseAuth.getInstance();

        // Initialization of fields
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        registrationButton = findViewById(R.id.btn_register);
        progressBar = findViewById(R.id.progressBar);
        loginLink = findViewById(R.id.loginNow);
        dropdown = findViewById(R.id.accountTypeDropdown);

        // Populate the dropdown with account accountTypes
        String[] accountTypes = getResources().getStringArray(R.array.account_type_choices);
        ArrayAdapter<String> adapter = new ArrayAdapter<>
                        (this,
                        android.R.layout.simple_list_item_1,
                        accountTypes);

        dropdown.setAdapter(adapter);


        //For going back to the login Page
        loginLink.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });

        registrationButton.setOnClickListener(view -> {

            progressBar.setVisibility(View.VISIBLE);

            //getting the email,password and account type information
            email = String.valueOf(editTextEmail.getText());
            password = String.valueOf(editTextPassword.getText());
            accountType = dropdown.getText().toString();

            // Validate inputs FIRST
            if (TextUtils.isEmpty(accountType)){
                Log.d("tag", "account type was NOT selected");
                Toast.makeText(Registration.this, "Select account type", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                return;
            }

            if (TextUtils.isEmpty(email)){
                Log.d("tag", "email address was NOT entered");
                Toast.makeText(Registration.this, "Enter email", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                return;
            }

            if (TextUtils.isEmpty(password)){
                Log.d("tag", "password was NOT entered");
                Toast.makeText(Registration.this, "Enter password", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                return;
            }

            progressBar.setVisibility(View.GONE);

            // Navigate BASED ON ACCOUNT TYPEà
            Intent nextIntent;

            switch (accountType) {

                case "Referee":
                    Log.d("Registration", "Referee selected");
                    nextIntent = new Intent(this, RefereeActivity.class);
                    break;

                case "Player":
                    Log.d("Registration", "Player selected");
                    nextIntent = new Intent(this, PlayerActivity.class);
                    break;

                case "Organizer":
                    Log.d("Registration", "Organizer selected");
                    nextIntent = new Intent(this, OrganizerActivity.class);
                    break;

                default:
                    nextIntent = new Intent(this, Registration.class);
            }


            nextIntent.putExtra("email", email);
            nextIntent.putExtra("password", password);
            nextIntent.putExtra("accountType", accountType);
            progressBar.setVisibility(ProgressBar.GONE);

            startActivity(nextIntent);
            finish();



        });
    }
}
