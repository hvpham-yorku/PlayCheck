package com.example.playcheck.activityfiles;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.playcheck.Database.RefereeLinkToDatabase;
import com.example.playcheck.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MatchClipListActivity extends AppCompatActivity {

    TextView txtTitle;
    Button btnAddClip, btnLiveFeed;
    ListView listClips;

    ArrayList<String> clipTitles;
    ArrayList<String> clipUris;
    ArrayAdapter<String> adapter;

    String gameId;
    String gameName;

    private RefereeLinkToDatabase dbService;

    ActivityResultLauncher<Intent> attachClipLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_clip_list);

        dbService = new RefereeLinkToDatabase();

        txtTitle = findViewById(R.id.txtTitle);
        btnAddClip = findViewById(R.id.btnAddClip);
        btnLiveFeed = findViewById(R.id.btnLiveFeed);
        listClips = findViewById(R.id.listClips);

        gameId = getIntent().getStringExtra("gameId");
        gameName = getIntent().getStringExtra("gameName");

        if (gameId == null) {
            gameId = "defaultGame";
        }
        if (gameName == null) {
            gameName = "Unknown Match";
        }

        txtTitle.setText("Clips for " + gameName);

        clipTitles = new ArrayList<>();
        clipUris = new ArrayList<>();

        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                clipTitles
        );

        listClips.setAdapter(adapter);

        loadClips();

        attachClipLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        String clipUri = result.getData().getStringExtra("clipUri");
                        if (clipUri != null) {
                            String title = "Clip " + (clipTitles.size() + 1);
                            saveClipToDatabase(title, clipUri);
                        }
                    }
                }
        );

        btnAddClip.setOnClickListener(v -> {
            Intent intent = new Intent(this, RecordOrAttachClipActivity.class);
            attachClipLauncher.launch(intent);
        });

        btnLiveFeed.setOnClickListener(v -> {
            Intent intent = new Intent(this, LiveFeedActivity.class);
            startActivity(intent);
        });

        listClips.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(this, ClipPlayerActivity.class);
            intent.putExtra("clipTitle", clipTitles.get(position));
            intent.putExtra("clipUri", clipUris.get(position));
            startActivity(intent);
        });
    }

    private void loadClips() {
        dbService.getMatchClips(gameId).thenAccept(clips -> {
            runOnUiThread(() -> {
                clipTitles.clear();
                clipUris.clear();
                for (Map<String, String> clip : clips) {
                    clipTitles.add(clip.get("title"));
                    clipUris.add(clip.get("uri"));
                }
                adapter.notifyDataSetChanged();
                if (clipTitles.isEmpty()) {
                    txtTitle.setText("Clips for " + gameName + " (No clips yet)");
                } else {
                    txtTitle.setText("Clips for " + gameName);
                }
            });
        }).exceptionally(e -> {
            runOnUiThread(() -> Toast.makeText(this, "Failed to load clips", Toast.LENGTH_SHORT).show());
            return null;
        });
    }

    private void saveClipToDatabase(String title, String uri) {
        dbService.saveMatchClip(gameId, title, uri).thenAccept(v -> {
            runOnUiThread(() -> {
                Toast.makeText(this, "Clip saved", Toast.LENGTH_SHORT).show();
                loadClips(); // Reload to show new clip
            });
        }).exceptionally(e -> {
            runOnUiThread(() -> Toast.makeText(this, "Failed to save clip", Toast.LENGTH_SHORT).show());
            return null;
        });
    }
}
