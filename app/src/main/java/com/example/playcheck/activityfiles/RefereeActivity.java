package com.example.playcheck.activityfiles;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.playcheck.R;
import com.example.playcheck.puremodel.Referee;
import com.example.playcheck.puremodel.User;

public class RefereeActivity extends AppCompatActivity implements View.OnClickListener {

    User theReferee;
    Button maleButton,femaleButton;
    EditText firstNameTextField,lastNameTextField,dateOfBirthTextField,userNameTextField;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_referee);

        theReferee = new Referee();

        maleButton =  findViewById(R.id.maleButton);
        femaleButton = findViewById(R.id.femaleButton);

        maleButton.setOnClickListener(this);
        femaleButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();

        if (id == R.id.maleButton) {
            Log.d("tag","male button was selected");
            theReferee.setGender("male");

        } else if (id == R.id.femaleButton) {
            Log.d("tag","female button was selected");
            theReferee.setGender("female");
        }


    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
