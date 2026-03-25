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

public class ClipPlayerActivity extends AppCompatActivity {

    private static final String TAG = "ClipPlayerActivity";
    TextView txtClipTitle;
    VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clip_player);

        txtClipTitle = findViewById(R.id.txtClipTitle);
        videoView = findViewById(R.id.videoViewClip);

        String clipTitle = getIntent().getStringExtra("clipTitle");
        String clipUri = getIntent().getStringExtra("clipUri");

        txtClipTitle.setText(clipTitle);

        if (clipUri != null && !clipUri.isEmpty()) {
            try {
                Uri uri = Uri.parse(clipUri);
                Log.d(TAG, "Attempting to play URI: " + uri.toString());
                
                videoView.setVideoURI(uri);

                MediaController mediaController = new MediaController(this);
                mediaController.setAnchorView(videoView);
                videoView.setMediaController(mediaController);

                videoView.setOnPreparedListener(mp -> {
                    Log.d(TAG, "Video prepared, starting playback");
                    videoView.start();
                });

                videoView.setOnErrorListener((mp, what, extra) -> {
                    Log.e(TAG, "VideoView Error: what=" + what + " extra=" + extra);
                    Toast.makeText(this, "Cannot play this video. Format might be unsupported or permission expired.", Toast.LENGTH_LONG).show();
                    return true; // true means we handled the error
                });

            } catch (Exception e) {
                Log.e(TAG, "Error parsing URI or setting up VideoView", e);
                Toast.makeText(this, "Error playing video: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No video URI provided", Toast.LENGTH_SHORT).show();
        }
    }
}
