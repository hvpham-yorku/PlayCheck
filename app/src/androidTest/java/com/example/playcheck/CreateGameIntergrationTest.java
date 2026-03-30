package com.example.playcheck;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;

import android.widget.DatePicker;
import android.widget.TimePicker;

import androidx.test.espresso.contrib.PickerActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.playcheck.activityfiles.CreateGameActivity;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

//Testing the Create Game feature
@RunWith(AndroidJUnit4.class)
@LargeTest
public class CreateGameIntergrationTest {
    @BeforeClass
    public static void setUpAuth() throws ExecutionException, InterruptedException, TimeoutException {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            Tasks.await(auth.signInWithEmailAndPassword("apple@gmail.com", "123abc"), 10, TimeUnit.SECONDS); //this is a test user
        }
    }
    @Rule
    public ActivityScenarioRule<CreateGameActivity> activityRule =
            new ActivityScenarioRule<>(CreateGameActivity.class);

    @Test
    public void testCreateGame() throws InterruptedException {
        onView(withId(R.id.teamA)).perform(typeText("Warriors"), closeSoftKeyboard());

        onView(withId(R.id.teamB)).perform(typeText("Titans"), closeSoftKeyboard());

        onView(withId(R.id.gameVenue)).perform(typeText("Main Stadium Yorku"), closeSoftKeyboard());

        onView(withId(R.id.gameType)).perform(typeText("Basketball"), closeSoftKeyboard());

        onView(withId(R.id.gameDateBtn)).perform(click());
        onView(withClassName(equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(2026, 3, 30));
        onView(withText("OK")).perform(click());

        onView(withId(R.id.gameTimeBtn)).perform(click());
        onView(withClassName(equalTo(TimePicker.class.getName()))).perform(PickerActions.setTime(15, 30));
        onView(withText("OK")).perform(click());

        onView(withId(R.id.searchReferee)).perform(replaceText("Trinity Shiloh"), closeSoftKeyboard());
        onView(withId(R.id.btnAddReferee)).perform(click());

        onView(allOf(withId(R.id.playerName), withText("Trinity Shiloh"))).check(matches(isDisplayed()));
        onView(withId(R.id.saveGame)).perform(click());
    }
}
