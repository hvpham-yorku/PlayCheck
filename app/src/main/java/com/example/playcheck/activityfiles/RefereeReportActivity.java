package com.example.playcheck.activityfiles;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.playcheck.database.GameLinkToDatabase;
import com.example.playcheck.database.TeamLinkToDatabase;
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
    TextView textTeamA, textTeamB;
    Button submitButton;

    android.widget.ViewFlipper flipper;
    DatabaseReference gamesRef;

    GameLinkToDatabase gamesDB;

    MatchReport report;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_referee_report);

        //Initialize UI
        //inputScore = findViewById(R.id.inputScore);
        textTeamA = findViewById(R.id.textTeamA);
        textTeamB = findViewById(R.id.textTeamB);
        inputNotes = findViewById(R.id.inputNotes);
        submitButton = findViewById(R.id.submitButton);
        flipper = findViewById(R.id.sportStatsFlipper);

        //get date from intent
        String gameId = getIntent().getStringExtra("gameId"); // Fixed: gameId is now a String (Firebase key)
        String gameType = getIntent().getStringExtra("gameType");
        String teamAName = getIntent().getStringExtra("teamA");
        String teamBName = getIntent().getStringExtra("teamB");

        //set text for team names
        textTeamA.setText(teamAName);
        textTeamB.setText(teamBName);

        //display input fields based on sport
        if (gameType.equalsIgnoreCase("Football") || gameType.equalsIgnoreCase("Soccer")){
            flipper.setDisplayedChild(0);
        } else if (gameType.equalsIgnoreCase("Basketball")){
            flipper.setDisplayedChild(1);
        } else if (gameType.equalsIgnoreCase("Volleyball")){
            flipper.setDisplayedChild(2);
        } else { //default to football/soccer
            flipper.setDisplayedChild(0);
        }

        gamesRef = FirebaseDatabase.getInstance().getReference("games");
        gamesDB = new GameLinkToDatabase();

        //get all stats and store them in DB
        submitButton.setOnClickListener(v -> {
            int teamAScore = 0;
            int teamBScore = 0;

            if(gameId != null && !gameId.isEmpty()){

                DatabaseReference reportRef = gamesRef.child(gameId).child("matchReport");

                report = new MatchReport();
                String notes = inputNotes.getText().toString();
                report.setNotes(notes);

                //Get stats from each input field
                String scoreString = "0-0";
                Map<String, Object> statsMap = new HashMap<>();
                if ("Soccer".equalsIgnoreCase(gameType) || "Football".equalsIgnoreCase(gameType)) {

                    int goalsA = getIntFromEditText(R.id.footGoalsA);
                    int goalsB = getIntFromEditText(R.id.footGoalsB);
                    scoreString = goalsA + " - " + goalsB;
                    teamAScore = goalsA;
                    teamBScore = goalsB;

                    FootballStats football = new FootballStats();
                    football.setShootingLeft(getIntFromEditText(R.id.footShotsTeamA)); football.setShootingRight(getIntFromEditText(R.id.footShotsTeamB));
                    football.setAttacksLeft(45); football.setAttacksRight(38);
                    football.setPossessionLeft(55); football.setPossessionRight(45);
                    football.setCornersLeft(5); football.setCornersRight(3);
                    football.setYellowCardsLeft(getIntFromEditText(R.id.footYellowTeamA)); football.setYellowCardsRight(getIntFromEditText(R.id.footYellowTeamB));
                    football.setRedCardsLeft(0); football.setRedCardsRight(0);
                    statsMap = convertFootballToMap(football);
                } else if ("Basketball".equalsIgnoreCase(gameType)) {
                    int pointsA = getIntFromEditText(R.id.basketPointsTeamA);
                    int pointsB = getIntFromEditText(R.id.basketPointsTeamB);
                    scoreString = pointsA + " - " + pointsB;
                    teamAScore = pointsA;
                    teamBScore = pointsB;

                    BasketballStats bball = new BasketballStats();
                    bball.setPointsLeft(getIntFromEditText(R.id.basketPointsTeamA)); bball.setPointsRight(getIntFromEditText(R.id.basketPointsTeamB));
                    bball.setReboundsLeft(getIntFromEditText(R.id.basketRebTeamA)); bball.setReboundsRight(getIntFromEditText(R.id.basketRebTeamB));
                    bball.setAssistsLeft(24); bball.setAssistsRight(20);
                    bball.setStealsLeft(8); bball.setStealsRight(6);
                    bball.setBlocksLeft(5); bball.setBlocksRight(4);
                    statsMap = convertBasketballToMap(bball);
                } else if ("Volleyball".equalsIgnoreCase(gameType)) {
                    int setsA = getIntFromEditText(R.id.volleySetsA);
                    int setsB = getIntFromEditText(R.id.volleySetsB);
                    scoreString = setsA + " - " + setsB;
                    teamAScore = setsA;
                    teamBScore = setsB;

                    VolleyballStats vball = new VolleyballStats();
                    vball.setKillsLeft(25); vball.setKillsRight(20);
                    vball.setAcesLeft(8); vball.setAcesRight(5);
                    vball.setVolleyballBlocksLeft(12); vball.setVolleyballBlocksRight(10);
                    vball.setDigsLeft(30); vball.setDigsRight(28);
                    statsMap = convertVolleyballToMap(vball);
                }

                final int finalScoreA = teamAScore;
                final int finalScoreB = teamBScore;
                final String finalScoreString = scoreString;
                report.setScore(scoreString);
                report.setDetailedStats(statsMap);
                gamesDB.getTeamIdsFromGame(gameId, (teamAid, teamBid) -> {
                    TeamLinkToDatabase teamDB = new TeamLinkToDatabase();
                    if (finalScoreA > finalScoreB) {
                        teamDB.updateTeamRecord(teamAid, true);  // Team A Win
                        teamDB.updateTeamRecord(teamBid, false); // Team B Loss
                    } else if (finalScoreB > finalScoreA) {
                        teamDB.updateTeamRecord(teamBid, true);  // Team B Win
                        teamDB.updateTeamRecord(teamAid, false); // Team A Loss
                    }
                });

                reportRef.setValue(report).addOnSuccessListener(unused -> {
                    gamesRef.child(gameId).child("score").setValue(finalScoreString)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(RefereeReportActivity.this, "Report Submitted!", Toast.LENGTH_SHORT).show();
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(RefereeReportActivity.this, "Failed to update main score", Toast.LENGTH_SHORT).show();
                            });
                });
            }
        });
    }

    /*This method returns the number from the edit text*/
    private int getIntFromEditText(int id) {
        String num = ((EditText)findViewById(id)).getText().toString();
        if (num.isEmpty()){
            return 0;  //if there is nothing entered, return 0 as default
        } else {
            return Integer.parseInt(num);
        }
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
