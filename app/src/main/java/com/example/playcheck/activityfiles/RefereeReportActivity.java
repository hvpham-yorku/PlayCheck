package com.example.playcheck.activityfiles;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.playcheck.R;
import com.example.playcheck.puremodel.BasketballStats;
import com.example.playcheck.puremodel.MatchReport;
import com.example.playcheck.puremodel.FootballStats;
import com.example.playcheck.puremodel.VolleyballStats;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;

public class RefereeReportActivity extends AppCompatActivity {

    EditText inputScore;
    EditText inputNotes;
    Button submitButton;

    DatabaseReference gamesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_referee_report);

        inputScore = findViewById(R.id.inputScore);
        inputNotes = findViewById(R.id.inputNotes);
        submitButton = findViewById(R.id.submitButton);

        gamesRef = FirebaseDatabase.getInstance().getReference("games");

        submitButton.setOnClickListener(v -> {

            String gameId = getIntent().getStringExtra("gameId"); // Fixed: gameId is now a String (Firebase key)
            String gameType = getIntent().getStringExtra("gameType");

            String score = inputScore.getText().toString();
            String notes = inputNotes.getText().toString();

            if(gameId != null && !gameId.isEmpty()){

                DatabaseReference reportRef = gamesRef.child(gameId).child("matchReport");

                MatchReport report = new MatchReport();
                report.setScore(score);
                report.setNotes(notes);

                // For now, we populate with some sample stats based on gameType
                // In a real scenario, you would have input fields for each of these
                Map<String, Object> statsMap = new HashMap<>();
                if ("Soccer".equalsIgnoreCase(gameType) || "Football".equalsIgnoreCase(gameType)) {
                    FootballStats football = new FootballStats();
                    football.setShootingLeft(12); football.setShootingRight(10);
                    football.setAttacksLeft(45); football.setAttacksRight(38);
                    football.setPossessionLeft(55); football.setPossessionRight(45);
                    football.setCornersLeft(5); football.setCornersRight(3);
                    football.setYellowCardsLeft(1); football.setYellowCardsRight(2);
                    football.setRedCardsLeft(0); football.setRedCardsRight(0);
                    report.setDetailedStats(convertFootballToMap(football));
                } else if ("Basketball".equalsIgnoreCase(gameType)) {
                    BasketballStats bball = new BasketballStats();
                    bball.setPointsLeft(88); bball.setPointsRight(82);
                    bball.setReboundsLeft(42); bball.setReboundsRight(38);
                    bball.setAssistsLeft(24); bball.setAssistsRight(20);
                    bball.setStealsLeft(8); bball.setStealsRight(6);
                    bball.setBlocksLeft(5); bball.setBlocksRight(4);
                    report.setDetailedStats(convertBasketballToMap(bball));
                } else if ("Volleyball".equalsIgnoreCase(gameType)) {
                    VolleyballStats vball = new VolleyballStats();
                    vball.setKillsLeft(25); vball.setKillsRight(20);
                    vball.setAcesLeft(8); vball.setAcesRight(5);
                    vball.setVolleyballBlocksLeft(12); vball.setVolleyballBlocksRight(10);
                    vball.setDigsLeft(30); vball.setDigsRight(28);
                    report.setDetailedStats(convertVolleyballToMap(vball));
                }

                reportRef.setValue(report).addOnSuccessListener(unused -> {
                    Toast.makeText(RefereeReportActivity.this, "Report Submitted", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
        });
    }

    private Map<String, Object> convertFootballToMap(FootballStats s) {
        Map<String, Object> map = new HashMap<>();
        map.put("shootingLeft", s.getShootingLeft());
        map.put("shootingRight", s.getShootingRight());
        map.put("attacksLeft", s.getAttacksLeft());
        map.put("attacksRight", s.getAttacksRight());
        map.put("possessionLeft", s.getPossessionLeft());
        map.put("possessionRight", s.getPossessionRight());
        map.put("cornersLeft", s.getCornersLeft());
        map.put("cornersRight", s.getCornersRight());
        map.put("yellowCardsLeft", s.getYellowCardsLeft());
        map.put("yellowCardsRight", s.getYellowCardsRight());
        map.put("redCardsLeft", s.getRedCardsLeft());
        map.put("redCardsRight", s.getRedCardsRight());
        return map;
    }

    private Map<String, Object> convertBasketballToMap(BasketballStats b) {
        Map<String, Object> map = new HashMap<>();
        map.put("pointsLeft", b.getPointsLeft());
        map.put("pointsRight", b.getPointsRight());
        map.put("reboundsLeft", b.getReboundsLeft());
        map.put("reboundsRight", b.getReboundsRight());
        map.put("assistsLeft", b.getAssistsLeft());
        map.put("assistsRight", b.getAssistsRight());
        map.put("stealsLeft", b.getStealsLeft());
        map.put("stealsRight", b.getStealsRight());
        map.put("blocksLeft", b.getBlocksLeft());
        map.put("blocksRight", b.getBlocksRight());
        return map;
    }

    private Map<String, Object> convertVolleyballToMap(VolleyballStats v) {
        Map<String, Object> map = new HashMap<>();
        map.put("killsLeft", v.getKillsLeft());
        map.put("killsRight", v.getKillsRight());
        map.put("acesLeft", v.getAcesLeft());
        map.put("acesRight", v.getAcesRight());
        map.put("volleyballBlocksLeft", v.getVolleyballBlocksLeft());
        map.put("volleyballBlocksRight", v.getVolleyballBlocksRight());
        map.put("digsLeft", v.getDigsLeft());
        map.put("digsRight", v.getDigsRight());
        return map;
    }
}
