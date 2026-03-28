package com.example.playcheck.activityfiles;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.playcheck.R;
import com.example.playcheck.puremodel.Game;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RefereeGamesActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<Game> games;
    AdapterGameList adapter;
    DatabaseReference gamesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_referee_games);

        //back to previous page button
        Button backBtn = findViewById(R.id.backBtnGameListRef);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        recyclerView = findViewById(R.id.recyclerRefGames);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        games = new ArrayList<>();
        adapter = new AdapterGameList(this, games);
        recyclerView.setAdapter(adapter);

        gamesRef = FirebaseDatabase.getInstance().getReference("games");

        loadGames();
    }

    private void loadGames() {

        gamesRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                games.clear();

                for (DataSnapshot child : snapshot.getChildren()) {

                    Game g = child.getValue(Game.class);
                    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    if (g != null) {
                        boolean isReferee = g.getReferees() != null && g.getReferees().containsKey(uid);
                        if (isReferee == true){
                            games.add(g);
                        }
                    }

                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull com.google.firebase.database.DatabaseError error) {

            }
        });
    }
}