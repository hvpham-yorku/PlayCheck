package com.example.playcheck.activityfiles;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.playcheck.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;

public class Video_Review_Page_Plus_Stats_controller extends AppCompatActivity {

    private AutoCompleteTextView sportTypeSpinner;
    private MaterialButtonToggleGroup toggleGroup;
    private TextView tvLikes, tvDislikes, tvSaved;
    private MaterialButton btnDaily, btnWeekly, btnMonthly, btnAllTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_video_review_page_plus_stats);
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupSportTypeSpinner();
        setupToggleGroup();
        setupClickListeners();
    }

    private void initViews() {
        sportTypeSpinner = findViewById(R.id.sport_type_spinner);
        toggleGroup = findViewById(R.id.toggleGroup);
        tvLikes = findViewById(R.id.tv_score);
        tvDislikes = findViewById(R.id.tv_steps);
        tvSaved = findViewById(R.id.tv_calories);
        
        btnDaily = findViewById(R.id.btn_daily);
        btnWeekly = findViewById(R.id.btn_weekly);
        btnMonthly = findViewById(R.id.btn_monthly);
        btnAllTime = findViewById(R.id.btn_all_time);
    }

    private void setupSportTypeSpinner() {
        String[] sports = {"Football", "Basketball", "Tennis", "Volleyball", "Running"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, sports);
        sportTypeSpinner.setAdapter(adapter);
        sportTypeSpinner.setText(sports[0], false);
    }

    private void setupToggleGroup() {
        toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                // Reset all button backgrounds to white
                btnDaily.setBackgroundColor(getResources().getColor(android.R.color.white, getTheme()));
                btnWeekly.setBackgroundColor(getResources().getColor(android.R.color.white, getTheme()));
                btnMonthly.setBackgroundColor(getResources().getColor(android.R.color.white, getTheme()));
                btnAllTime.setBackgroundColor(getResources().getColor(android.R.color.white, getTheme()));

                // Set checked button background to the lime green color
                MaterialButton checkedButton = findViewById(checkedId);
                checkedButton.setBackgroundColor(0xFFD4E157); // Lime color from image

                updateStats(checkedId);
            }
        });
    }

    private void updateStats(int checkedId) {
        if (checkedId == R.id.btn_daily) {
            tvLikes.setText("1,200 / 2,000");
            tvDislikes.setText("8,000 / 10,000");
            tvSaved.setText("450 / 600");
        } else if (checkedId == R.id.btn_weekly) {
            tvLikes.setText("5,615 / 7,699");
            tvDislikes.setText("5,615 / 7,699");
            tvSaved.setText("5,615 / 7,699");
        } else if (checkedId == R.id.btn_monthly) {
            tvLikes.setText("22,400 / 30,000");
            tvDislikes.setText("150,000 / 200,000");
            tvSaved.setText("12,000 / 15,000");
        } else if (checkedId == R.id.btn_all_time) {
            tvLikes.setText("120,500 / 150,000");
            tvDislikes.setText("1,200,000 / 1,500,000");
            tvSaved.setText("85,000 / 100,000");
        }
    }

    private void setupClickListeners() {

        //Bottom Navigation
        findViewById(R.id.navHome).setOnClickListener(v -> navigateTo(Homepage_Controller.class));
        findViewById(R.id.navNews).setOnClickListener(v -> handleNavClick("Notifications"));
        findViewById(R.id.navClipboard).setOnClickListener(v -> navigateTo(Video_Review_Page_Plus_Stats_controller.class));
        findViewById(R.id.navBack).setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        findViewById(R.id.navProfile).setOnClickListener(v -> handleNavClick("Profile"));

    }

    private void navigateTo(Class<?> targetActivity) {
        try {
            Intent intent = new Intent(this, targetActivity);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Activity not yet implemented", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleNavClick(String navItem) {
        Toast.makeText(this, "You are already on " + navItem, Toast.LENGTH_SHORT).show();
    }

}
