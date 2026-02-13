package com.example.playcheck.activityfiles;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.example.playcheck.R;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Registration extends AppCompatActivity {

    TextInputEditText editTextEmail, editTextPassword;
    Button buttonReg;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    TextView textView;

    AutoCompleteTextView accountTypeDropdown;
     TextInputLayout accountTypeLayout;


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        mAuth = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        buttonReg = findViewById(R.id.btn_register);
        progressBar = findViewById(R.id.progressBar);
        textView = findViewById(R.id.loginNow);
        accountTypeDropdown =findViewById(R.id.accountTypeDropdown);
        accountTypeLayout = findViewById(R.id.accountTypeLayout);

//        textView.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View view){
//                Intent intent = new Intent(getApplicationContext(), Login.class);
//                startActivity(intent);
//                finish();
//            }
//        });

        buttonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                String email, password;
                email = String.valueOf(editTextEmail.getText());
                password = String.valueOf(editTextPassword.getText());

                if (TextUtils.isEmpty(email)){
                    Toast.makeText(Registration.this, "Enter email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)){
                    Toast.makeText(Registration.this, "Enter password", Toast.LENGTH_SHORT).show();
                    return;
                }


                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {

                                    Toast.makeText(Registration.this,
                                            "Account created.",
                                            Toast.LENGTH_SHORT).show();

                                    String selectedAccountType =
                                            accountTypeDropdown.getText().toString().trim();

                                    if (selectedAccountType.equals("Referee")) {

                                        Intent intent =
                                                new Intent(Registration.this, RefereeActivity.class);
                                        startActivity(intent);

                                    } else if (selectedAccountType.equals("Player")) {

                                        Intent intent =
                                                new Intent(Registration.this, MainActivity.class);
                                        startActivity(intent);

                                    } else {

                                        Intent intent =
                                                new Intent(Registration.this, MainActivity.class);
                                        startActivity(intent);
                                    }

                                    finish();
                                }
                            }
                        });
            }




        });


        String[] accountTypes = getResources().getStringArray(R.array.account_types);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                accountTypes
        );

        accountTypeDropdown.setAdapter(adapter);

    }
//    Intent goToRefereeProfilePage;
//    @Override
//    public void onClick(View v) {
//        int id;
//
//        id = v.getId();
//
//        if (id == R.id.accountTypeDropdown) {
//
//            String selectedAccountType =
//                    accountTypeDropdown.getText().toString().trim();
//
//            if (selectedAccountType.equals("Referee")) {
//
//                goToRefereeProfilePage = new Intent(this, RefereeActivity.class);
//                startActivity(goToRefereeProfilePage);
//
//            }
//
//        }
//
//    }
}