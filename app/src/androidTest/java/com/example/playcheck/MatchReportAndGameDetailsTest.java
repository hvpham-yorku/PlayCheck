package com.example.playcheck;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.playcheck.activityfiles.RefereeReportActivity;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


/* This Test involves Creating a Game on the CreateGame page, submitting a match report
on the referee report page, and viewing the game details and match report on the Game Details page */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class MatchReportAndGameDetailsTest {


    //this is the intent the Report page receives
    private static Intent createIntent() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), RefereeReportActivity.class);
        intent.putExtra("gameId", "test_game_123");
        intent.putExtra("teamA", "teamA");
        intent.putExtra("teamB", "teamB");
        intent.putExtra("gameType", "gameType");
        return intent;
    }

    @BeforeClass
    public static void setUp() throws ExecutionException, InterruptedException, TimeoutException {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            Tasks.await(auth.signInWithEmailAndPassword("tri@gmail.com", "123abc"), 10, TimeUnit.SECONDS);
        }
    }

    @Rule
    public ActivityScenarioRule<RefereeReportActivity> activityRule = new ActivityScenarioRule<>(createIntent());

    //Tests for each kind of stat fields
    @Test
    public void testSubmitFootballReport() {
        onView(withId(R.id.footGoalsA)).perform(replaceText("3"), closeSoftKeyboard());
        onView(withId(R.id.footGoalsB)).perform(replaceText("1"), closeSoftKeyboard());
        onView(withId(R.id.footShotsTeamA)).perform(replaceText("12"), closeSoftKeyboard());
        onView(withId(R.id.footYellowTeamA)).perform(replaceText("1"), closeSoftKeyboard());
        onView(withId(R.id.inputNotes)).perform( replaceText("Clear victory for Team A."), closeSoftKeyboard());
        onView(withId(R.id.submitButton)).perform(click());

    }


}
