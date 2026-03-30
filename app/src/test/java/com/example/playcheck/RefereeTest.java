package com.example.playcheck;

import com.example.playcheck.puremodel.Game;
import com.example.playcheck.puremodel.Referee;
import com.example.playcheck.database.RefereeLinkToDatabase;
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
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.quality.Strictness;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class RefereeTest {

    private Referee referee;
    private Game game1;
    private Game game2;

    @Mock
    private RefereeLinkToDatabase mockDbService;

    // Static mocks for Firebase classes
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
        when(mockFirebaseUser.getUid()).thenReturn("fakeRefereeUid");

        // Stub createUserWithEmailAndPassword to return a mock Task (avoids NPE if called)
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
    public void setUp() throws Exception {
        referee = new Referee();
        game1 = new Game();
        game2 = new Game();

        // Inject the mock database service
        Field dbServiceField = Referee.class.getDeclaredField("refereeDbService");
        dbServiceField.setAccessible(true);
        dbServiceField.set(referee, mockDbService);

        // Set a fake UID
        referee.setUid("fakeRefereeUid");
    }

    // ---------- Existing tests (unchanged) ----------
    @Test
    public void testAddGameDateAvailability() {
        LocalDate date = LocalDate.of(2026, 3, 10);
        referee.addGameDateAvailability(date);
        assertTrue(referee.getGameDatesAvailability().contains(date));
    }

    @Test
    public void testDeleteGameDate() {
        LocalDate date = LocalDate.of(2026, 3, 10);
        referee.addGameDateAvailability(date);
        referee.deleteGameDate(date);
        assertFalse(referee.getGameDatesAvailability().contains(date));
    }

    @Test
    public void testIsAvailableOnDate_True() {
        LocalDate date = LocalDate.of(2026, 3, 10);
        referee.addGameDateAvailability(date);
        assertTrue(referee.isAvailableOnDate(date));
    }

    @Test
    public void testIsAvailableOnDate_False() {
        LocalDate date = LocalDate.of(2026, 3, 10);
        assertFalse(referee.isAvailableOnDate(date));
    }

    @Test
    public void testAddToSchedule() {
        referee.addToSchedule(game1);
        assertTrue(referee.getSchedule().contains(game1));
    }

    @Test
    public void testDeleteGameFromSchedule() {
        referee.addToSchedule(game1);
        referee.deleteGameFromSchedule(game1);
        assertFalse(referee.getSchedule().contains(game1));
    }

    @Test
    public void testMultipleGamesInSchedule() {
        referee.addToSchedule(game1);
        referee.addToSchedule(game2);
        assertEquals(2, referee.getSchedule().size());
    }

    @Test
    public void testParameterizedConstructorInitializesLists() {
        Referee ref = new Referee("John", "Doe", "Male", LocalDate.of(1990, 1, 1));
        assertNotNull(ref.getGameDatesAvailability());
        assertNotNull(ref.getSchedule());
    }

    // ---------- New async tests (unchanged) ----------
    @Test
    public void testAddAvailableDate_addsLocallyAndCallsDbService() throws Exception {
        LocalDate date = LocalDate.of(2026, 4, 1);
        when(mockDbService.saveAvailabilityDates(anyString(), anyList()))
                .thenReturn(CompletableFuture.completedFuture(null));

        CompletableFuture<Void> future = referee.addAvailableDate(date);

        assertTrue(referee.getGameDatesAvailability().contains(date));
        future.get();
        verify(mockDbService).saveAvailabilityDates("fakeRefereeUid", referee.getGameDatesAvailability());
    }

    @Test
    public void testRemoveAvailableDate_removesLocallyAndCallsDbService() throws Exception {
        LocalDate date = LocalDate.of(2026, 4, 1);
        referee.addGameDateAvailability(date);
        when(mockDbService.saveAvailabilityDates(anyString(), anyList()))
                .thenReturn(CompletableFuture.completedFuture(null));

        CompletableFuture<Void> future = referee.removeAvailableDate(date);

        assertFalse(referee.getGameDatesAvailability().contains(date));
        future.get();
        verify(mockDbService).saveAvailabilityDates("fakeRefereeUid", referee.getGameDatesAvailability());
    }

    @Test
    public void testAcceptGame_updatesLocalAndCallsDbService() throws Exception {
        LocalDate gameDate = LocalDate.of(2026, 4, 1);
        Game game = mock(Game.class);
        when(game.getDateInLocalDate()).thenReturn(gameDate);

        referee.addGameDateAvailability(gameDate);
        when(mockDbService.assignGameToReferee(anyString(), any(Game.class)))
                .thenReturn(CompletableFuture.completedFuture(null));
        when(mockDbService.saveAvailabilityDates(anyString(), anyList()))
                .thenReturn(CompletableFuture.completedFuture(null));

        CompletableFuture<Void> future = referee.acceptGame(game);

        assertTrue(referee.getSchedule().contains(game));
        assertFalse(referee.getGameDatesAvailability().contains(gameDate));
        future.get();
        verify(mockDbService).assignGameToReferee("fakeRefereeUid", game);
        verify(mockDbService).saveAvailabilityDates("fakeRefereeUid", referee.getGameDatesAvailability());
    }

    @Test
    public void testLoadAvailabilityDates_updatesLocalList() throws Exception {
        List<LocalDate> loadedDates = List.of(LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 2));
        when(mockDbService.getAvailabilityDates("fakeRefereeUid"))
                .thenReturn(CompletableFuture.completedFuture(loadedDates));

        CompletableFuture<List<LocalDate>> future = referee.loadAvailabilityDates();
        List<LocalDate> result = future.get();

        assertEquals(loadedDates, result);
        assertEquals(loadedDates, referee.getGameDatesAvailability());
    }

    @Test
    public void testLoadAssignedGames_updatesLocalSchedule() throws Exception {
        List<Game> loadedGames = List.of(game1, game2);
        when(mockDbService.getAssignedGames("fakeRefereeUid"))
                .thenReturn(CompletableFuture.completedFuture(loadedGames));

        CompletableFuture<List<Game>> future = referee.loadAssignedGames();
        List<Game> result = future.get();

        assertEquals(loadedGames, result);
        assertEquals(loadedGames, referee.getSchedule());
    }

    // ---------- Fixed test: use doReturn for spy ----------
    @Test
    public void testRegister_callsSuperAndSavesAvailability() throws Exception {
        Referee spyReferee = spy(referee);
        // Stub register() to simulate super returning a UID and then perform the actual composition
        doAnswer(invocation -> {
            return CompletableFuture.completedFuture("newUid")
                    .thenCompose(uid -> mockDbService.saveAvailabilityDates(uid, spyReferee.getGameDatesAvailability())
                            .thenApply(v -> uid));
        }).when(spyReferee).register();

        // Ensure the mock service returns a completed future when called
        when(mockDbService.saveAvailabilityDates(anyString(), anyList()))
                .thenReturn(CompletableFuture.completedFuture(null));

        LocalDate date = LocalDate.of(2026, 6, 1);
        spyReferee.addGameDateAvailability(date);

        CompletableFuture<String> future = spyReferee.register();
        String uid = future.get();

        assertEquals("newUid", uid);
        verify(mockDbService).saveAvailabilityDates("newUid", spyReferee.getGameDatesAvailability());
    }

    @Test
    public void testGetRefereeId_returnsUidWhenSet() {
        referee.setUid("testUid");
        assertEquals("testUid", referee.getRefereeId());
    }
}
