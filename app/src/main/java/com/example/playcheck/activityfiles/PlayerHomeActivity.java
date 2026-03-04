package com.example.playcheck.activityfiles;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.playcheck.R;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PlayerHomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Toast.makeText(this, "PLAYER HOME LOADED", Toast.LENGTH_LONG).show();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_home);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewGames);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<String> demoGames = new ArrayList<>();
        demoGames.add("Hawks vs Lions - March 3 - Gym A");
        demoGames.add("Eagles vs Bears - March 5 - Field 2");
        demoGames.add("Sharks vs Wolves - March 10 - Arena 1");

        SimpleGameAdapter adapter = new SimpleGameAdapter(demoGames);
        recyclerView.setAdapter(adapter);
    }
}