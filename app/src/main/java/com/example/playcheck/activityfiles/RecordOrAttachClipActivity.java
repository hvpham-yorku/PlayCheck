package com.example.playcheck.activityfiles;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.playcheck.R;

// This activity is for choosing a video from the phone
public class RecordOrAttachClipActivity extends AppCompatActivity {

    Button btnPickVideo;
    ActivityResultLauncher<String[]> pickVideoLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_or_attach_clip);

        btnPickVideo = findViewById(R.id.btnPickVideo);

        // Open the file picker for videos
        pickVideoLauncher = registerForActivityResult(
                new ActivityResultContracts.OpenDocument(),
                uri -> {
                    if (uri != null) {
                        // Keep access to the video even if app closes
                        try {
                            final int takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION;
                            getContentResolver().takePersistableUriPermission(uri, takeFlags);
                        } catch (SecurityException e) {
                            // ignore error if not supported
                        }

                        // Send uri back to the list screen
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("clipUri", uri.toString());
                        setResult(RESULT_OK, resultIntent);
                        Toast.makeText(this, "Clip attached", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
        );

        btnPickVideo.setOnClickListener(v -> pickVideoLauncher.launch(new String[]{"video/*"}));
    }
}
