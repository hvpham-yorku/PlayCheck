package com.example.playcheck;

import com.example.playcheck.puremodel.Game;
import com.example.playcheck.puremodel.Player;
import com.example.playcheck.puremodel.Team;
import com.example.playcheck.database.PlayerLinkToDatabase;
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

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class PlayerTest {

    private Player player;
    private Team mockTeam;

    @Mock
    private PlayerLinkToDatabase mockDbService;

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
        when(mockFirebaseUser.getUid()).thenReturn("fakePlayerUid");

        // Stub createUserWithEmailAndPassword to return a mock Task
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
        player = new Player("John", "Doe", "john@example.com", "1990-01-01", "Male");
        mockTeam = mock(Team.class);
        when(mockTeam.getTeamId()).thenReturn("team123");

        // Inject the mock database service
        Field dbServiceField = Player.class.getDeclaredField("playerDbService");
        dbServiceField.setAccessible(true);
        dbServiceField.set(player, mockDbService);

        // Set a fake UID (the constructor may already have set it via FirebaseAuth mock)
        player.setUid("fakePlayerUid");
    }

    // ---------- Basic Getter/Setter Tests ----------
    @Test
    public void testSetTeam() {
        player.setTeam(mockTeam);
        assertEquals(mockTeam, player.getTeam());
        assertEquals("team123", player.getTeamId());
    }

    @Test
    public void testSetTeamId() {
        player.setTeamId("team456");
        assertEquals("team456", player.getTeamId());
        // team object remains null
        assertNull(player.getTeam());
    }

    @Test
    public void testGoalsAndMatches() {
        player.setGoalsScored(5);
        player.setMatchesPlayed(10);
        assertEquals(5, player.getGoalsScored());
        assertEquals(10, player.getMatchesPlayed());
    }

    // ---------- joinTeam() Tests ----------
    @Test
    public void testJoinTeam_success() throws Exception {
        when(mockDbService.addPlayerToTeam(anyString(), anyString()))
                .thenReturn(CompletableFuture.completedFuture(null));
        // Mock saveProfile() from User class (which calls userDbService.saveProfile())
        // Since saveProfile is final in User, we can use doAnswer or rely on the real method calling a mocked service.
        // But to avoid complications, we can stub saveProfile to return a completed future.
        // We'll use a spy on player to stub saveProfile.
        Player spyPlayer = spy(player);
        doReturn(CompletableFuture.completedFuture(null)).when(spyPlayer).saveProfile();

        CompletableFuture<Void> future = spyPlayer.joinTeam(mockTeam);
        future.get();

        verify(mockDbService).addPlayerToTeam("fakePlayerUid", "team123");
        verify(spyPlayer).saveProfile();
        assertEquals(mockTeam, spyPlayer.getTeam());
        assertEquals("team123", spyPlayer.getTeamId());
    }

    @Test
    public void testJoinTeam_noUid() throws Exception {
        player.setUid(null);
        CompletableFuture<Void> future = player.joinTeam(mockTeam);
        future.get(); // should complete immediately

        verifyNoInteractions(mockDbService);
        // Team still set locally
        assertEquals(mockTeam, player.getTeam());
        assertEquals("team123", player.getTeamId());
    }

    // ---------- leaveTeam() Tests ----------
    @Test
    public void testLeaveTeam_success() throws Exception {
        player.setTeam(mockTeam); // set local team
        when(mockDbService.removePlayerFromTeam(anyString(), anyString()))
                .thenReturn(CompletableFuture.completedFuture(null));
        Player spyPlayer = spy(player);
        doReturn(CompletableFuture.completedFuture(null)).when(spyPlayer).saveProfile();

        CompletableFuture<Void> future = spyPlayer.leaveTeam();
        future.get();

        verify(mockDbService).removePlayerFromTeam("fakePlayerUid", "team123");
        verify(spyPlayer).saveProfile();
        assertNull(spyPlayer.getTeam());
        assertNull(spyPlayer.getTeamId());
    }

    @Test
    public void testLeaveTeam_noTeam() throws Exception {
        player.setTeam(null);
        CompletableFuture<Void> future = player.leaveTeam();
        future.get();

        verifyNoInteractions(mockDbService);
    }

    // ---------- updateStats() Tests ----------
    @Test
    public void testUpdateStats_withUid() throws Exception {
        Player spyPlayer = spy(player);
        doReturn(CompletableFuture.completedFuture(null)).when(spyPlayer).saveProfileFields(anyMap());

        spyPlayer.setGoalsScored(5);
        spyPlayer.setMatchesPlayed(10);

        CompletableFuture<Void> future = spyPlayer.updateStats(2, 3);
        future.get();

        assertEquals(7, spyPlayer.getGoalsScored());
        assertEquals(13, spyPlayer.getMatchesPlayed());
        verify(spyPlayer).saveProfileFields(anyMap());
    }

    @Test
    public void testUpdateStats_noUid() throws Exception {
        player.setUid(null);
        player.setGoalsScored(5);
        player.setMatchesPlayed(10);

        CompletableFuture<Void> future = player.updateStats(2, 3);
        future.get();

        assertEquals(7, player.getGoalsScored());
        assertEquals(13, player.getMatchesPlayed());
        verifyNoInteractions(mockDbService);
    }

    // ---------- recordMatchPerformance() Tests ----------
    @Test
    public void testRecordMatchPerformance() throws Exception {
        when(mockDbService.recordMatchPerformance(anyString(), anyString(), anyInt(), anyInt(), anyInt()))
                .thenReturn(CompletableFuture.completedFuture(null));
        Player spyPlayer = spy(player);
        // updateStats will be called as part of thenCompose; we can let it call the real method
        // but we need to ensure saveProfileFields is stubbed if called inside updateStats.
        doReturn(CompletableFuture.completedFuture(null)).when(spyPlayer).saveProfileFields(anyMap());

        CompletableFuture<Void> future = spyPlayer.recordMatchPerformance("game789", 1, 2, 90);
        future.get();

        verify(mockDbService).recordMatchPerformance("fakePlayerUid", "game789", 1, 2, 90);
        assertEquals(1, spyPlayer.getGoalsScored());
        assertEquals(1, spyPlayer.getMatchesPlayed());
        verify(spyPlayer, times(1)).saveProfileFields(anyMap());
    }

    @Test
    public void testRecordMatchPerformance_noUid() throws Exception {
        player.setUid(null);
        CompletableFuture<Void> future = player.recordMatchPerformance("game789", 1, 2, 90);
        future.get();

        verifyNoInteractions(mockDbService);
        // stats are not updated because updateStats is inside thenCompose that never runs
        assertEquals(0, player.getGoalsScored());
        assertEquals(0, player.getMatchesPlayed());
    }

    // ---------- loadTeamInfo() Tests ----------
    @Test
    public void testLoadTeamInfo() throws Exception {
        when(mockDbService.getPlayerTeam("fakePlayerUid"))
                .thenReturn(CompletableFuture.completedFuture(mockTeam));

        CompletableFuture<Team> future = player.loadTeamInfo();
        Team result = future.get();

        assertEquals(mockTeam, result);
        assertEquals(mockTeam, player.getTeam());
        assertEquals("team123", player.getTeamId());
    }

    @Test
    public void testLoadTeamInfo_noUid() throws Exception {
        player.setUid(null);
        CompletableFuture<Team> future = player.loadTeamInfo();
        Team result = future.get();

        assertNull(result);
        verifyNoInteractions(mockDbService);
    }

    // ---------- loadMatchHistory() Tests ----------
    @Test
    public void testLoadMatchHistory() throws Exception {
        List<Game> mockGames = List.of(mock(Game.class), mock(Game.class));
        when(mockDbService.getPlayerMatchHistory("fakePlayerUid"))
                .thenReturn(CompletableFuture.completedFuture(mockGames));

        CompletableFuture<List<Game>> future = player.loadMatchHistory();
        List<Game> result = future.get();

        assertEquals(mockGames, result);
    }

    @Test
    public void testLoadMatchHistory_noUid() throws Exception {
        player.setUid(null);
        CompletableFuture<List<Game>> future = player.loadMatchHistory();
        List<Game> result = future.get();

        assertTrue(result.isEmpty());
        verifyNoInteractions(mockDbService);
    }
}
