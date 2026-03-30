package com.example.playcheck.activityfiles;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.playcheck.Database.GameLinkToDatabase;
import com.example.playcheck.Database.UserLinkToDatabase;
import com.example.playcheck.R;
import com.example.playcheck.puremodel.Game;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Map;

public class GameDetailsActivity extends AppCompatActivity {

    private TextView teamAText, teamBText, teamBNameText, teamANameText, dateText, locationText, scoreText, refereeText, notesText;
    private RecyclerView rvTeamA, rvTeamB, rvRefs;
    private ViewFlipper flipper;
    private Button refereeReportButton, btnViewClips, editGameButton;

    private String gameId;
    private String gameType;
    private long gameDateMillis = 0;

    private GameLinkToDatabase gamesDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game_details);

        teamAText = findViewById(R.id.teamA);
        teamBText = findViewById(R.id.teamB);
        teamBNameText = findViewById(R.id.teamBTeamName);
        teamANameText = findViewById(R.id.teamATeamName);
        dateText = findViewById(R.id.dateText);
        locationText = findViewById(R.id.locationText);
        scoreText = findViewById(R.id.scoreText);
        refereeText = findViewById(R.id.refereeText);
        notesText = findViewById(R.id.detailsNotes);

        rvTeamA = findViewById(R.id.recycleviewTeamAPlayers);
        rvTeamB = findViewById(R.id.recycleViewTeamBPlayers);
        rvRefs = findViewById(R.id.recycleViewReferees);

        flipper = findViewById(R.id.detailsStatsFlipper);

        refereeReportButton = findViewById(R.id.refereeReportButton);
        btnViewClips = findViewById(R.id.btnViewClips);
        editGameButton = findViewById(R.id.editGameButton);

        rvTeamA.setLayoutManager(new LinearLayoutManager(this));
        rvTeamB.setLayoutManager(new LinearLayoutManager(this));
        rvRefs.setLayoutManager(new LinearLayoutManager(this));

        gamesDB = new GameLinkToDatabase();

        Intent intent = getIntent();
        gameId = intent.getStringExtra("gameId");
        gameType = intent.getStringExtra("gameType");
        gameDateMillis = intent.getLongExtra("gameDateMillis", 0);

        String teamA = intent.getStringExtra("teamA");
        String teamB = intent.getStringExtra("teamB");
        String date = intent.getStringExtra("date");
        String location = intent.getStringExtra("location");
        String score = intent.getStringExtra("score");
        ArrayList<String> teamAPlayers = intent.getStringArrayListExtra("teamAPlayers");
        ArrayList<String> teamBPlayers = intent.getStringArrayListExtra("teamBPlayers");
        ArrayList<String> referees = intent.getStringArrayListExtra("referees");

        teamAText.setText(teamA);
        teamBText.setText(teamB);
        dateText.setText("Date: " + date);
        locationText.setText("Location: " + location);
        scoreText.setText(score != null ? "Score: " + score : "Score: Not Reported");
        refereeText.setText("Referees");
        teamANameText.setText(teamA);
        teamBNameText.setText(teamB);

        if (teamAPlayers != null) {
            rvTeamA.setAdapter(new AddedPlayersAdapter(teamAPlayers, false));
        } else {
            rvTeamA.setAdapter(new AddedPlayersAdapter(new ArrayList<>(), false));
        }

        if (teamBPlayers != null) {
            rvTeamB.setAdapter(new AddedPlayersAdapter(teamBPlayers, false));
        } else {
            rvTeamB.setAdapter(new AddedPlayersAdapter(new ArrayList<>(), false));
        }

        if (referees != null) {
            rvRefs.setAdapter(new AddedPlayersAdapter(referees, false));
        } else {
            rvRefs.setAdapter(new AddedPlayersAdapter(new ArrayList<>(), false));
        }

        refereeReportButton.setVisibility(View.GONE);
        btnViewClips.setVisibility(View.GONE);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        UserLinkToDatabase user = new UserLinkToDatabase();
        if (currentUser != null) {
            user.getUserAccountType(currentUser).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String accountType = task.getResult();
                    if ("Referee".equals(accountType)) {
                        refereeReportButton.setVisibility(View.VISIBLE);
                        btnViewClips.setVisibility(View.VISIBLE);
                    }
                }
            });
        }

        btnViewClips.setOnClickListener(v -> {
            Intent clipsIntent = new Intent(this, MatchClipListActivity.class);
            clipsIntent.putExtra("gameId", gameId);
            clipsIntent.putExtra("gameName", teamAText.getText().toString() + " vs " + teamBText.getText().toString());
            startActivity(clipsIntent);
        });

        refereeReportButton.setOnClickListener(v -> {
            Intent reportIntent = new Intent(this, RefereeReportActivity.class);
            reportIntent.putExtra("gameId", gameId);
            reportIntent.putExtra("teamA", teamAText.getText().toString());
            reportIntent.putExtra("teamB", teamBText.getText().toString());
            reportIntent.putExtra("gameType", gameType);
            startActivity(reportIntent);
        });

        editGameButton.setOnClickListener(v -> {
            Intent editIntent = new Intent(this, EditGameActivity.class);
            editIntent.putExtra("gameId", gameId);
            editIntent.putExtra("teamA", teamAText.getText().toString());
            editIntent.putExtra("teamB", teamBText.getText().toString());
            editIntent.putExtra("location", locationText.getText().toString().replace("Location: ", ""));
            editIntent.putExtra("gameType", gameType);
            editIntent.putExtra("gameDateMillis", gameDateMillis);
            startActivity(editIntent);
        });

        setFlipperChild();
        observeMatchReport();
        refreshGameDetails();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshGameDetails();
    }

    private void refreshGameDetails() {
        if (gameId == null || gameId.isEmpty()) return;

        FirebaseDatabase.getInstance()
                .getReference("games")
                .child(gameId)
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (!snapshot.exists()) return;

                    String teamA = snapshot.child("teamA").getValue(String.class);
                    String teamB = snapshot.child("teamB").getValue(String.class);
                    String location = snapshot.child("gameVenue").getValue(String.class);
                    String latestGameType = snapshot.child("gameType").getValue(String.class);
                    Long latestGameDate = snapshot.child("gameDate").getValue(Long.class);
                    String score = snapshot.child("score").getValue(String.class);

                    if (teamA != null) {
                        teamAText.setText(teamA);
                        teamANameText.setText(teamA);
                    }

                    if (teamB != null) {
                        teamBText.setText(teamB);
                        teamBNameText.setText(teamB);
                    }

                    if (location != null) {
                        locationText.setText("Location: " + location);
                    }

                    if (latestGameDate != null) {
                        gameDateMillis = latestGameDate;
                        String formattedDate = new Game().getGameDateLongtoString(latestGameDate);
                        dateText.setText("Date: " + formattedDate);
                    }

                    if (latestGameType != null) {
                        gameType = latestGameType;
                        setFlipperChild();
                    }

                    if (score != null) {
                        scoreText.setText("Score: " + score);
                    } else {
                        scoreText.setText("Score: Not Reported");
                    }

                    ArrayList<String> refereeNames = new ArrayList<>();
                    DataSnapshot refsSnapshot = snapshot.child("referees");
                    if (refsSnapshot.exists()) {
                        for (DataSnapshot refSnap : refsSnapshot.getChildren()) {
                            String refName = refSnap.getValue(String.class);
                            if (refName != null) refereeNames.add(refName);
                        }
                    }
                    rvRefs.setAdapter(new AddedPlayersAdapter(refereeNames, false));

                    String teamAid = snapshot.child("teamAid").getValue(String.class);
                    String teamBid = snapshot.child("teamBid").getValue(String.class);

                    if (teamAid != null) {
                        FirebaseDatabase.getInstance().getReference("teams").child(teamAid).child("players")
                                .get().addOnSuccessListener(teamASnapshot -> {
                                    ArrayList<String> teamAPlayers = new ArrayList<>();
                                    for (DataSnapshot playerSnap : teamASnapshot.getChildren()) {
                                        String playerName = playerSnap.getValue(String.class);
                                        if (playerName != null) teamAPlayers.add(playerName);
                                    }
                                    rvTeamA.setAdapter(new AddedPlayersAdapter(teamAPlayers, false));
                                });
                    }

                    if (teamBid != null) {
                        FirebaseDatabase.getInstance().getReference("teams").child(teamBid).child("players")
                                .get().addOnSuccessListener(teamBSnapshot -> {
                                    ArrayList<String> teamBPlayers = new ArrayList<>();
                                    for (DataSnapshot playerSnap : teamBSnapshot.getChildren()) {
                                        String playerName = playerSnap.getValue(String.class);
                                        if (playerName != null) teamBPlayers.add(playerName);
                                    }
                                    rvTeamB.setAdapter(new AddedPlayersAdapter(teamBPlayers, false));
                                });
                    }
                });
    }

    private void observeMatchReport() {
        if (gameId == null || gameId.isEmpty()) return;

        gamesDB.observeMatchReport(gameId, report -> {
            if (report != null) {
                scoreText.setText("Score: " + report.getScore());
                notesText.setText("Notes: " + report.getNotes());

                Map<String, Object> stats = report.getDetailedStats();
                if (stats != null) {
                    updateStatsUI(gameType, stats);
                }
            } else {
                scoreText.setText("Score: Not Reported");
                notesText.setText("Notes: No report submitted yet.");
            }
        });
    }

    private void setFlipperChild() {
        if (gameType == null) {
            flipper.setDisplayedChild(0);
            return;
        }

        if (gameType.equalsIgnoreCase("Football") || gameType.equalsIgnoreCase("Soccer")) {
            flipper.setDisplayedChild(0);
        } else if (gameType.equalsIgnoreCase("Basketball")) {
            flipper.setDisplayedChild(1);
        } else if (gameType.equalsIgnoreCase("Volleyball")) {
            flipper.setDisplayedChild(2);
        } else {
            flipper.setDisplayedChild(0);
        }
    }

    private void updateStatsUI(String gameType, Map<String, Object> stats) {
        if ("Basketball".equalsIgnoreCase(gameType)) {
            ((TextView) findViewById(R.id.detailsBasketPoints)).setText("Points: " + stats.get("pointsLeft") + " - " + stats.get("pointsRight"));
            ((TextView) findViewById(R.id.detailsBasketRebounds)).setText("Rebounds: " + stats.get("reboundsLeft") + " - " + stats.get("reboundsRight"));
        } else if ("Volleyball".equalsIgnoreCase(gameType)) {
            ((TextView) findViewById(R.id.detailsVolleyAces)).setText("Aces: " + stats.get("acesLeft") + " - " + stats.get("acesRight"));
            ((TextView) findViewById(R.id.detailsVolleyBlocks)).setText("Blocks: " + stats.get("volleyballBlocksLeft") + " - " + stats.get("volleyballBlocksRight"));
        } else {
            ((TextView) findViewById(R.id.detailsFootShots)).setText("Shots: " + stats.get("shootingLeft") + " - " + stats.get("shootingRight"));
            ((TextView) findViewById(R.id.detailsFootYellow)).setText("Yellow Cards: " + stats.get("yellowCardsLeft") + " - " + stats.get("yellowCardsRight"));
        }
    }
}