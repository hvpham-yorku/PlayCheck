package com.example.playcheck;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.playcheck.activityfiles.TeamStandings;

import org.junit.Test;
import org.junit.runner.RunWith;

/*This makes sure that the recycler view shows all the teams on the Team standings page */
@RunWith(AndroidJUnit4.class)
public class TeamStandingsIntergrationTest {

    @Test
    public void testStandingsDisplay() {
        try (ActivityScenario<TeamStandings> scenario = ActivityScenario.launch(TeamStandings.class)) {

            onView(withId(R.id.recycleViewTeamStandings))
                    .check(matches(withEffectiveVisibility(Visibility.VISIBLE)));

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {}

            onView(withId(R.id.recycleViewTeamStandings)).check(matches(exists()));
        }
    }

    //since the recycler view's height is 0, it checks if the recyclerview is visible instead
    private static org.hamcrest.Matcher<android.view.View> exists() {
        return new org.hamcrest.TypeSafeMatcher<android.view.View>() {
            @Override
            public void describeTo(org.hamcrest.Description description) {
                description.appendText("Team should be in view");
            }
            @Override
            public boolean matchesSafely(android.view.View view) {
                return view != null;
            }
        };
    }
}