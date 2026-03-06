package com.example.playcheck.activityfiles;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.playcheck.R;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class OrganizerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer);

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