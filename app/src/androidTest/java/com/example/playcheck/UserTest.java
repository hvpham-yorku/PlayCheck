package com.example.playcheck;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import com.example.playcheck.Database.UserLinkToDatabase;
import com.example.playcheck.puremodel.Player;
import com.example.playcheck.puremodel.User;
import com.google.firebase.auth.FirebaseAuth;

import java.util.concurrent.TimeUnit;

/* These tests are for the persistence layer for the User link to the database */

@RunWith(AndroidJUnit4.class)
public class UserTest {

    private FirebaseAuth auth;
    private UserLinkToDatabase repo;

    private final String TEST_EMAIL = "usertest@gmail.com";
    private final String TEST_PASSWORD = "123abc";

    @Before
    public void setup() {
        auth = FirebaseAuth.getInstance();
        repo = new UserLinkToDatabase();
    }

    @After
    public void cleanup() {
        auth.signOut();
    }

    /*Test created user in database and in authentication */
    @Test
    public void CreateUserInAuthAndDatabase() throws Exception {

        Player player = new Player(
                "Integration",
                "Tester",
                TEST_EMAIL,
                "2000-01-01",
                "Male"
        );

        String uid = repo.createUser(player, TEST_PASSWORD)
                .get(10, TimeUnit.SECONDS);

        assertNotNull(uid);
    }

    /*Sees if the user is logged in */
    @Test
    public void loginUserObject() throws Exception {

        User user = repo.loginUser(TEST_EMAIL, TEST_PASSWORD)
                .get(10, TimeUnit.SECONDS);

        assertNotNull(user);
        assertEquals(TEST_EMAIL, user.getEmail());
    }

    @Test
    public void fetchUserByUid_shouldReturnCorrectUser() throws Exception {

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        User user = repo.fetchUserByUid(uid)
                .get(10, TimeUnit.SECONDS);

        assertNotNull(user);
        assertEquals(uid, user.getUid());
    }




}