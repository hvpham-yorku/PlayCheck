package com.example.playcheck;

import com.example.playcheck.puremodel.Game;
import com.example.playcheck.puremodel.Organizer;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class OrganizerTest {

    private Organizer organizer;
    private Game game1;
    private Game game2;

    // Static mocks for Firebase classes (to avoid real initialization)
    private static MockedStatic<FirebaseAuth> firebaseAuthMock;
    private static MockedStatic<FirebaseApp> firebaseAppMock;
    private static MockedStatic<FirebaseDatabase> firebaseDatabaseMock;

    private static FirebaseAuth mockFirebaseAuth;
    private static FirebaseUser mockFirebaseUser;
    private static FirebaseApp mockFirebaseApp;
    private static FirebaseDatabase mockFirebaseDatabase;

    @BeforeClass
    public static void setUpClass() {
        // Mock FirebaseAuth
        firebaseAuthMock = mockStatic(FirebaseAuth.class);
        mockFirebaseAuth = mock(FirebaseAuth.class);
        mockFirebaseUser = mock(FirebaseUser.class);
        when(FirebaseAuth.getInstance()).thenReturn(mockFirebaseAuth);
        when(mockFirebaseAuth.getCurrentUser()).thenReturn(mockFirebaseUser);
        when(mockFirebaseUser.getUid()).thenReturn("fakeOrganizerUid");

        // Stub createUserWithEmailAndPassword to return a mock Task (optional but safe)
        when(mockFirebaseAuth.createUserWithEmailAndPassword(anyString(), anyString()))
                .thenReturn(mock(Task.class));

        // Mock FirebaseApp
        firebaseAppMock = mockStatic(FirebaseApp.class);
        mockFirebaseApp = mock(FirebaseApp.class);
        when(FirebaseApp.getInstance()).thenReturn(mockFirebaseApp);

        // Mock FirebaseDatabase
        firebaseDatabaseMock = mockStatic(FirebaseDatabase.class);
        mockFirebaseDatabase = mock(FirebaseDatabase.class);
        when(FirebaseDatabase.getInstance()).thenReturn(mockFirebaseDatabase);
    }

    @AfterClass
    public static void tearDownClass() {
        firebaseAuthMock.close();
        firebaseAppMock.close();
        firebaseDatabaseMock.close();
    }

    @Before
    public void setUp() {
        // Use parameterized constructor to ensure lists are initialized
        organizer = new Organizer("John", "Doe", "john@example.com", "1990-01-01", "Male");
        game1 = new Game();   // assumes default constructor exists
        game2 = new Game();
    }

    // ---------- Constructor Tests ----------
    @Test
    public void testDefaultConstructor() {
        Organizer org = new Organizer();
        assertNotNull(org.getGameDatesAvailability());
        assertNotNull(org.getSchedule());
        assertTrue(org.getGameDatesAvailability().isEmpty());
        assertTrue(org.getSchedule().isEmpty());
    }

    @Test
    public void testParameterizedConstructorWithStrings() {
        Organizer org = new Organizer("Jane", "Smith", "jane@example.com", "1992-05-15", "Female");
        assertEquals("Jane", org.getFirstName());
        assertEquals("Smith", org.getLastName());
        assertEquals("jane@example.com", org.getEmail());
        //assertEquals("1992-05-15", org.getDOBasString());
        assertEquals("Female", org.getGender());
        assertNotNull(org.getGameDatesAvailability());
    }

    @Test
    public void testParameterizedConstructorWithLocalDate() {
        LocalDate dob = LocalDate.of(1990, 1, 1);
        Organizer org = new Organizer("userId123", "Alice", "Johnson", "Female", dob);
        assertEquals("Alice", org.getFirstName());
        assertEquals("Johnson", org.getLastName());
        assertEquals("Female", org.getGender());
        assertEquals(dob, org.getDateOfBirth());
        assertNotNull(org.getGameDatesAvailability()); // initialized by this()
        assertNotNull(org.getSchedule());
    }

    // ---------- Availability Tests ----------
    @Test
    public void testAddGameDateAvailability() {
        LocalDate date = LocalDate.of(2026, 3, 10);
        organizer.addGameDateAvailability(date);
        assertTrue(organizer.getGameDatesAvailability().contains(date));
    }

    @Test
    public void testDeleteGameDate() {
        LocalDate date = LocalDate.of(2026, 3, 10);
        organizer.addGameDateAvailability(date);
        organizer.deleteGameDate(date);
        assertFalse(organizer.getGameDatesAvailability().contains(date));
    }

    @Test
    public void testIsAvailableOnDate_True() {
        LocalDate date = LocalDate.of(2026, 3, 10);
        organizer.addGameDateAvailability(date);
        assertTrue(organizer.isAvailableOnDate(date));
    }

    @Test
    public void testIsAvailableOnDate_False() {
        LocalDate date = LocalDate.of(2026, 3, 10);
        assertFalse(organizer.isAvailableOnDate(date));
    }

    // ---------- Schedule Tests ----------
    @Test
    public void testAddToSchedule() {
        organizer.addToSchedule(game1);
        assertTrue(organizer.getSchedule().contains(game1));
    }

    @Test
    public void testDeleteGameFromSchedule() {
        organizer.addToSchedule(game1);
        organizer.deleteGameFromSchedule(game1);
        assertFalse(organizer.getSchedule().contains(game1));
    }

    @Test
    public void testMultipleGamesInSchedule() {
        organizer.addToSchedule(game1);
        organizer.addToSchedule(game2);
        assertEquals(2, organizer.getSchedule().size());
    }

    // ---------- Team and League Name Tests ----------
    @Test
    public void testSetTeamName() {
        organizer.setTeamName("Red Dragons");
        assertEquals("Red Dragons", organizer.getTeamName());
    }

    @Test
    public void testSetLeagueName() {
        organizer.setLeagueName("Premier League");
        assertEquals("Premier League", organizer.getLeagueName());
    }

    @Test
    public void testTeamAndLeagueInitiallyNull() {
        assertNull(organizer.getTeamName());
        assertNull(organizer.getLeagueName());
    }
}
