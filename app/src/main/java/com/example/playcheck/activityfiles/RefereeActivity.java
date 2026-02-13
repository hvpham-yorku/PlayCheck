package com.example.playcheck.activityfiles;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.playcheck.R;
import com.example.playcheck.puremodel.Referee;
import com.example.playcheck.puremodel.User;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class RefereeActivity extends AppCompatActivity implements View.OnClickListener {

    User theReferee;
    Button maleButton,femaleButton,refProfileSubmitButton;
    EditText firstNameTextField,lastNameTextField,dateOfBirthTextField,userNameTextField;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_referee);

        theReferee = new Referee();

        /*
        TODO: A Back Button to the previous page needs to be done
        TODO: All of the input text fields and Selections need to be connected to a Database
         */

        maleButton =  findViewById(R.id.maleButton);
        femaleButton = findViewById(R.id.femaleButton);
        refProfileSubmitButton = findViewById((R.id.refProfileSubmitButton));



        firstNameTextField = findViewById(R.id.firstNameInput);
        lastNameTextField = findViewById(R.id.lastNameInput);
        dateOfBirthTextField = findViewById(R.id.dateOfBirthInput);
        userNameTextField = findViewById(R.id.userNameTextField);


        maleButton.setOnClickListener(this);
        femaleButton.setOnClickListener(this);
        refProfileSubmitButton.setOnClickListener(this);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View view) {
        int id,refProfileSubmitId;

        id = view.getId();

        if (id == R.id.maleButton) {
            Log.d("tag","male button was selected");
            theReferee.setGender("male");

        } else if (id == R.id.femaleButton) {
            Log.d("tag","female button was selected");
            theReferee.setGender("female");
        }

        refProfileSubmitId = view.getId();

        Boolean profileNotCompleted = true;
        while(profileNotCompleted){

            if(refProfileSubmitId == R.id.refProfileSubmitButton){

                /*
                This is where the object to take the app to the next page is defined
                MainActivity Page has now been Temporarily been selected
                TODO: Input the Appropriate Next Page
                 to move to after the referee Profile Page
                 */

                Intent goToNextPage = new Intent(this,MainActivity.class);



                String fName,lName,fdOb,fuserName;


                /*
                This is where the first Name and the Last Name of the Referee is
                being collected and stored to the above Strings and then finally set
                 */
                fName = firstNameTextField.getText().toString();
                lName = lastNameTextField.getText().toString();
                theReferee.setName(fName,lName);

                /*
                This is where the date of birth and username is being
                collected as Strings
                 */
                fdOb = dateOfBirthTextField.getText().toString();
                fuserName = userNameTextField.getText().toString();


                /*This is trying to make sure that the all fields have been
                filled before we can go to the next of the App
                 */

                if(fName != null && lName != null && theReferee != null
                        && fdOb != null && fuserName != null){

                    profileNotCompleted = false;

                }

                /*
                The date of Birth and Username is finally set here. The Date of Birth is
                supposed to be of type Local Date hence I tried parsing the collected String
                to a Local Date type.
                 */

                assert theReferee != null;
                theReferee.setDateOfBirth(LocalDate.parse(fdOb));
                theReferee.setUserId(fuserName);

                /*
                Finally the Referee Details have been collected
                and now we head to the next page
                 */
                startActivity(goToNextPage);

            }


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
