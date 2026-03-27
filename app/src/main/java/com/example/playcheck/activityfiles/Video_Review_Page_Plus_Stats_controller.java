package com.example.playcheck.activityfiles;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

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
    private TextView tvScore, tvSteps, tvCalories;
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
    }

    private void initViews() {
        sportTypeSpinner = findViewById(R.id.sport_type_spinner);
        toggleGroup = findViewById(R.id.toggleGroup);
        tvScore = findViewById(R.id.tv_score);
        tvSteps = findViewById(R.id.tv_steps);
        tvCalories = findViewById(R.id.tv_calories);
        
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
            tvScore.setText("1,200 / 2,000");
            tvSteps.setText("8,000 / 10,000");
            tvCalories.setText("450 / 600");
        } else if (checkedId == R.id.btn_weekly) {
            tvScore.setText("5,615 / 7,699");
            tvSteps.setText("5,615 / 7,699");
            tvCalories.setText("5,615 / 7,699");
        } else if (checkedId == R.id.btn_monthly) {
            tvScore.setText("22,400 / 30,000");
            tvSteps.setText("150,000 / 200,000");
            tvCalories.setText("12,000 / 15,000");
        } else if (checkedId == R.id.btn_all_time) {
            tvScore.setText("120,500 / 150,000");
            tvSteps.setText("1,200,000 / 1,500,000");
            tvCalories.setText("85,000 / 100,000");
        }
    }
}