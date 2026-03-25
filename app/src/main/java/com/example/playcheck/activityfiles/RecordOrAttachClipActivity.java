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

public class RecordOrAttachClipActivity extends AppCompatActivity {

    Button btnPickVideo;

    ActivityResultLauncher<String> pickVideoLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_or_attach_clip);

        btnPickVideo = findViewById(R.id.btnPickVideo);

        pickVideoLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        // Request persistable permission to access the URI even after app restarts
                        try {
                            getContentResolver().takePersistableUriPermission(uri, 
                                    Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        } catch (SecurityException e) {
                            // Some providers don't support persistable permissions, 
                            // but we proceed anyway as it might still work in the current session
                        }

                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("clipUri", uri.toString());
                        setResult(RESULT_OK, resultIntent);
                        Toast.makeText(this, "Clip attached", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
        );

        btnPickVideo.setOnClickListener(v -> pickVideoLauncher.launch("video/*"));
    }
}
