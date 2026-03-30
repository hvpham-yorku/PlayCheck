package com.example.playcheck.activityfiles;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.playcheck.R;
import com.example.playcheck.puremodel.Organizer;
import com.example.playcheck.puremodel.Player;
import com.example.playcheck.puremodel.Referee;
import com.example.playcheck.puremodel.User;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class Login_Controller extends AppCompatActivity {

    private TextInputEditText editTextEmail, editTextPassword;
    private MaterialButton buttonLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_page);

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        buttonLogin.setOnClickListener(v -> performLogin());

        findViewById(R.id.textViewSignUpLink).setOnClickListener(v -> {
            startActivity(new Intent(Login_Controller.this, Registration.class));
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        User.getCurrentUser().thenAccept(user -> {
            if (user != null) {
                runOnUiThread(this::navigateToHomepage);
            }
        }).exceptionally(throwable -> null);
    }

    private void performLogin() {
        String email = String.valueOf(editTextEmail.getText()).trim();
        String password = String.valueOf(editTextPassword.getText());

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            return;
        }

        User.login(email, password).thenAccept(user -> {
            runOnUiThread(this::navigateToHomepage);
        }).exceptionally(throwable -> {
            runOnUiThread(() -> Toast.makeText(Login_Controller.this, "Authentication failed: " + throwable.getMessage(), Toast.LENGTH_SHORT).show());
            return null;
        });
    }

    private void navigateToHomepage() {
        Intent intent = new Intent(Login_Controller.this, Homepage_Controller.class);
        startActivity(intent);
        finish();
    }
}
