package com.example.playcheck;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.playcheck.activityfiles.CreateTeamOrganizer;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/*This intergration test is for the Create Team Organizer Page
* Although it uses CreateTeamOrganizer file, the CreateTeamPlayer has the same layout
* and uses nearly all the same logic. */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class CreateTeamIntegrationTest {
    @BeforeClass
    public static void setUpAuth() throws ExecutionException, InterruptedException, TimeoutException {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            Tasks.await(auth.signInWithEmailAndPassword("apple@gmail.com", "123abc"), 10, TimeUnit.SECONDS); //this is a test user
        }
    }
    @Rule
    public ActivityScenarioRule<CreateTeamOrganizer> activityRule =
            new ActivityScenarioRule<>(CreateTeamOrganizer.class);

    @Test
    public void testCreateTeam() throws InterruptedException {
        onView(withId(R.id.teamName))
                .perform(typeText("Test Team Organizer"), closeSoftKeyboard());

        addPlayer("Zach Char");
        addPlayer("Trinity S");
        addPlayer("Tony Zhang");

        onView(withText("Zach Char")).check(matches(isDisplayed()));
        onView(withText("Trinity S")).check(matches(isDisplayed()));
        onView(withText("Tony Zhang")).check(matches(isDisplayed()));

        onView(withId(R.id.searchCaptain)).perform(typeText("Trinity S"), closeSoftKeyboard());
        onView(withId(R.id.btnCreateTeam)).perform(click());
        Thread.sleep(5000);
    }

    //this is adding the players to the recycle view
    private void addPlayer(String name) {
        onView(withId(R.id.searchPlayer)).perform(replaceText(name), closeSoftKeyboard());
        onView(withId(R.id.btnAddPlayer)).perform(click());
    }

}
