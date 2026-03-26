package com.example.playcheck.activityfiles;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.playcheck.R;

// This screen just plays a video clip
public class ClipPlayerActivity extends AppCompatActivity {

    TextView txtClipTitle;
    VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clip_player);

        txtClipTitle = findViewById(R.id.txtClipTitle);
        videoView = findViewById(R.id.videoViewClip);

        // Get title and uri from the intent
        String clipTitle = getIntent().getStringExtra("clipTitle");
        String clipUri = getIntent().getStringExtra("clipUri");

        txtClipTitle.setText(clipTitle);

        if (clipUri != null && !clipUri.isEmpty()) {
            try {
                Uri uri = Uri.parse(clipUri);
                
                // Load the video
                videoView.setVideoURI(uri);

                // Add play/pause buttons
                MediaController mediaController = new MediaController(this);
                mediaController.setAnchorView(videoView);
                videoView.setMediaController(mediaController);

                // Start when ready
                videoView.setOnPreparedListener(mp -> videoView.start());

                // Show error message if it fails
                videoView.setOnErrorListener((mp, what, extra) -> {
                    Toast.makeText(this, "Can't play this video", Toast.LENGTH_LONG).show();
                    return true;
                });

            } catch (Exception e) {
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
