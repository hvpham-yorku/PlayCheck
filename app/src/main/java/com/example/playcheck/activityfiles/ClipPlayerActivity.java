package com.example.playcheck.activityfiles;

import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.playcheck.R;

public class ClipPlayerActivity extends AppCompatActivity {

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
            Uri uri = Uri.parse(clipUri);
            videoView.setVideoURI(uri);

            MediaController mediaController = new MediaController(this);
            mediaController.setAnchorView(videoView);
            videoView.setMediaController(mediaController);

            videoView.start();
        }
    }
}


