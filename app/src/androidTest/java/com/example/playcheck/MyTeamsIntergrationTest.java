package com.example.playcheck;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.Matchers.not;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.playcheck.activityfiles.MyTeams;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@RunWith(AndroidJUnit4.class)

/*Tests the my teams page */
public class MyTeamsIntergrationTest {
    @BeforeClass
    public static void setUpAuth() throws ExecutionException, InterruptedException, TimeoutException {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            Tasks.await(auth.signInWithEmailAndPassword("apple@gmail.com", "123abc"), 10, TimeUnit.SECONDS); //this is a test user
        }
    }
    @Test
    public void testMyTeamsPage() {
        try (ActivityScenario<MyTeams> scenario = ActivityScenario.launch(MyTeams.class)) {

            onView(withId(R.id.MyTeamsPageRecycleView)).check(matches(isDisplayed()));

            try { Thread.sleep(3000); } catch (InterruptedException e) {}

            onView(withId(R.id.MyTeamsPageRecycleView)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

            onView(allOf(withId(R.id.extraDetailsLayout), isCompletelyDisplayed())).check(matches(isDisplayed()));

            onView(allOf(withId(R.id.clickShowDetails), isCompletelyDisplayed())).check(matches(withText("Click to Hide Team Details")));

            onView(withId(R.id.MyTeamsPageRecycleView)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

            onView(withId(R.id.extraDetailsLayout)).check(matches(not(isDisplayed())));
        }
    }

    @Test
    public void testRecycleViewPresence() { //see if the recycler view is on the page
        try (ActivityScenario<MyTeams> scenario = ActivityScenario.launch(MyTeams.class)) {
            onView(withId(R.id.MyTeamsPageTitle)).check(matches(withText("My Teams")));
            onView(withId(R.id.MyTeamsPageRecycleView)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)));
        }
    }
}
