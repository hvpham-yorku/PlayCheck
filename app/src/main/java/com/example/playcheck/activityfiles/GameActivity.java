package com.example.playcheck.activityfiles;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.playcheck.R;

public class GameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game);
        Log.d("tag","In onCreate");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("tag","In onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("tag","In onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("tag","In onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("tag","In onStop");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("tag","In onRestart");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("tag","In onDestroy");
    }

}
