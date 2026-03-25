package com.example.playcheck.activityfiles;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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

    ArrayList<Map<String, String>> clipList;
    ClipAdapter adapter;

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

        clipList = new ArrayList<>();
        adapter = new ClipAdapter(this, clipList);
        listClips.setAdapter(adapter);

        loadClips();

        attachClipLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        String clipUri = result.getData().getStringExtra("clipUri");
                        if (clipUri != null) {
                            String title = "Clip " + (clipList.size() + 1);
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
    }

    private void loadClips() {
        dbService.getMatchClips(gameId).thenAccept(clips -> {
            runOnUiThread(() -> {
                clipList.clear();
                clipList.addAll(clips);
                adapter.notifyDataSetChanged();
                if (clipList.isEmpty()) {
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
                loadClips();
            });
        }).exceptionally(e -> {
            runOnUiThread(() -> Toast.makeText(this, "Failed to save clip", Toast.LENGTH_SHORT).show());
            return null;
        });
    }

    private void confirmDelete(Map<String, String> clip) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Clip")
                .setMessage("Are you sure you want to delete '" + clip.get("title") + "'?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    dbService.deleteMatchClip(gameId, clip.get("id")).thenAccept(v -> {
                        runOnUiThread(() -> {
                            Toast.makeText(this, "Clip deleted", Toast.LENGTH_SHORT).show();
                            loadClips();
                        });
                    }).exceptionally(e -> {
                        runOnUiThread(() -> Toast.makeText(this, "Failed to delete clip", Toast.LENGTH_SHORT).show());
                        return null;
                    });
                })
                .setNegativeButton("No", null)
                .show();
    }

    // Inner class for custom adapter with delete button
    private class ClipAdapter extends ArrayAdapter<Map<String, String>> {
        public ClipAdapter(Context context, List<Map<String, String>> clips) {
            super(context, 0, clips);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_clip, parent, false);
            }

            Map<String, String> clip = getItem(position);
            TextView txtName = convertView.findViewById(R.id.txtClipName);
            ImageButton btnDelete = convertView.findViewById(R.id.btnDeleteClip);

            if (clip != null) {
                txtName.setText(clip.get("title"));
                
                // Clicking the text plays the video
                convertView.setOnClickListener(v -> {
                    Intent intent = new Intent(MatchClipListActivity.this, ClipPlayerActivity.class);
                    intent.putExtra("clipTitle", clip.get("title"));
                    intent.putExtra("clipUri", clip.get("uri"));
                    startActivity(intent);
                });

                // Clicking the trash icon deletes the video
                btnDelete.setOnClickListener(v -> confirmDelete(clip));
            }

            return convertView;
        }
    }
}
