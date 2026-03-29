package com.example.playcheck;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

import com.example.playcheck.puremodel.User;

public class UserTest {

    private TestUser user;
    private FakeUserDatabase fakeDatabase;

    // Concrete subclass for testing
    static class TestUser extends User {
        public TestUser(String email, String password) {
            super(email,password);
        }
    }

    @Before
    public void setup() {

        fakeDatabase = new FakeUserDatabase();

        User.setDatabaseService(fakeDatabase);

        user = new TestUser("test@email.com","password123");

        user.setFirstName("John");
        user.setLastName("Doe");
        user.setGender("Male");
        user.setDateOfBirth(LocalDate.of(2000,1,1));
    }

    // ------------------------------------------------
    // REGISTER
    // ------------------------------------------------

    @Test
    public void register_ShouldAssignUID() throws Exception {

        String uid = user.register().get();

        assertEquals("TEST_UID", uid);
        assertEquals("TEST_UID", user.getUid());
    }

    // ------------------------------------------------
    // LOGIN
    // ------------------------------------------------

    @Test
    public void login_ShouldReturnStoredUser() throws Exception {

        user.register().get();

        User loggedIn = User.login("test@email.com","password123").get();

        assertEquals(user, loggedIn);
    }

    // ------------------------------------------------
    // SAVE PROFILE
    // ------------------------------------------------

    @Test
    public void saveProfile_ShouldUpdateDatabase() throws Exception {

        user.register().get();

        user.setFirstName("Alice");

        user.saveProfile().get();

        assertEquals("Alice", fakeDatabase.storedUser.getFirstName());
    }

    // ------------------------------------------------
    // SAVE PROFILE FIELDS
    // ------------------------------------------------

    @Test
    public void saveProfileFields_ShouldUpdateLocalUser() throws Exception {

        user.register().get();

        Map<String,Object> fields = new HashMap<>();
        fields.put("firstName","Bob");

        user.saveProfileFields(fields).get();

        assertEquals("Bob", user.getFirstName());
    }

    // ------------------------------------------------
    // UPDATE EMAIL
    // ------------------------------------------------

    @Test
    public void updateEmail_ShouldChangeEmail() throws Exception {

        user.register().get();

        //user.updateEmail("new@email.com").get();

        assertEquals("new@email.com", user.getEmail());
    }

    // ------------------------------------------------
    // DELETE ACCOUNT
    // ------------------------------------------------

    @Test
    public void deleteAccount_ShouldRemoveUser() throws Exception {

        user.register().get();

       // user.deleteAccount().get();

        assertNull(fakeDatabase.storedUser);
    }

    // ------------------------------------------------
    // REFRESH PROFILE
    // ------------------------------------------------

    @Test
    public void refreshProfile_ShouldReloadUser() throws Exception {

        user.register().get();

        fakeDatabase.storedUser.setFirstName("Updated");

       // user.refreshProfile().get();

        assertEquals("Updated", user.getFirstName());
    }

}
