package com.example.playcheck.activityfiles;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.playcheck.R;
import com.example.playcheck.Database.UserLinkToDatabase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DatabaseError;

public class Login extends AppCompatActivity {

    TextInputEditText editTextEmail, editTextPassword;
    Button buttonLogin;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    TextView textView;
    UserLinkToDatabase user = new UserLinkToDatabase();

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in already (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null){
            progressBar.setVisibility(View.VISIBLE);
            //get account type
            user.getUserAccountType(currentUser).addOnCompleteListener(task -> {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    String accountType = task.getResult();

                    if (accountType == null){
                        // If no profile found, force profile setup or registration
                        startActivity(new Intent(Login.this, Registration.class));
                        finish();
                        return;
                    }

                    Intent nextIntent = null;
                    if (accountType.equals("Referee")){
                        nextIntent = new Intent(this, RefereeHomeActivity.class);
                    } else if (accountType.equals("Player")){
                        nextIntent = new Intent(this, PlayerHomeActivity.class);
                    } else if (accountType.equals("Organizer")){
                        nextIntent = new Intent(this, OrganizerDashboardActivity.class);
                    }

                    if (nextIntent != null) {
                        startActivity(nextIntent);
                        finish();
                    }
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        buttonLogin = findViewById(R.id.btn_login);
        progressBar = findViewById(R.id.progressBar);
        textView = findViewById(R.id.RegisterNow);

        textView.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), Registration.class);
            startActivity(intent);
            finish();
        });

        buttonLogin.setOnClickListener(v -> {

            progressBar.setVisibility(View.VISIBLE);

            String email = String.valueOf(editTextEmail.getText());
            String password = String.valueOf(editTextPassword.getText());

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(Login.this, "Enter email", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                return;
            }

            if (TextUtils.isEmpty(password)) {
                Toast.makeText(Login.this, "Enter password", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                return;
            }

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser userFB = mAuth.getCurrentUser();
                            user.getUserAccountType(userFB).addOnCompleteListener(typeTask -> {
                                progressBar.setVisibility(View.GONE);
                                if (typeTask.isSuccessful()) {
                                    String accountType = typeTask.getResult();
                                    if (accountType == null) {
                                        startActivity(new Intent(Login.this, Registration.class));
                                    } else if (accountType.equals("Organizer")) {
                                        startActivity(new Intent(Login.this, OrganizerDashboardActivity.class));
                                    } else if (accountType.equals("Player")) {
                                        startActivity(new Intent(Login.this, PlayerHomeActivity.class));
                                    } else if (accountType.equals("Referee")) {
                                        startActivity(new Intent(Login.this, RefereeHomeActivity.class));
                                    }
                                    finish();
                                } else {
                                    Toast.makeText(Login.this, "Failed to get account type.", Toast.LENGTH_SHORT).show();
                                }
                            });

                        } else {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(Login.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}