package com.example.playcheck;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.example.playcheck.Database.GameLinkToDatabase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;

import java.util.HashMap;
import java.util.Map;

public class GameUpdateIntegrationTest {

    private static MockedStatic<FirebaseDatabase> staticFirebaseDatabase;
    private static MockedStatic<FirebaseApp> staticFirebaseApp;
    private static MockedStatic<FirebaseAuth> staticFirebaseAuth;

    private FirebaseDatabase mockFirebaseDatabase;
    private DatabaseReference mockGamesRef;
    private DatabaseReference mockGameRef;

    private GameLinkToDatabase gameDb;

    @BeforeClass
    public static void setupClass() {
        staticFirebaseDatabase = mockStatic(FirebaseDatabase.class);
        staticFirebaseApp = mockStatic(FirebaseApp.class);
        staticFirebaseAuth = mockStatic(FirebaseAuth.class);

        FirebaseApp mockApp = mock(FirebaseApp.class);
        staticFirebaseApp.when(FirebaseApp::getInstance).thenReturn(mockApp);

        FirebaseAuth mockAuth = mock(FirebaseAuth.class);
        staticFirebaseAuth.when(FirebaseAuth::getInstance).thenReturn(mockAuth);
    }

    @AfterClass
    public static void tearDownClass() {
        staticFirebaseDatabase.close();
        staticFirebaseApp.close();
        staticFirebaseAuth.close();
    }

    @Before
    public void setup() {
        mockFirebaseDatabase = mock(FirebaseDatabase.class);
        mockGamesRef = mock(DatabaseReference.class);
        mockGameRef = mock(DatabaseReference.class);

        staticFirebaseDatabase.when(FirebaseDatabase::getInstance).thenReturn(mockFirebaseDatabase);

        when(mockFirebaseDatabase.getReference("games")).thenReturn(mockGamesRef);
        when(mockGamesRef.child("game123")).thenReturn(mockGameRef);

        gameDb = new GameLinkToDatabase();
    }

    @Test
    public void updateGameDetails_shouldCallFirebaseUpdateChildren() {
        Map<String, Object> updates = new HashMap<>();
        updates.put("teamA", "Lions");
        updates.put("teamB", "Tigers");
        updates.put("gameVenue", "Main Gym");
        updates.put("gameType", "Basketball");
        updates.put("gameDate", 1775000000000L);

        Task<Void> mockTask = mock(Task.class);
        when(mockGameRef.updateChildren(anyMap())).thenReturn(mockTask);

        OnCompleteListener<Void> listener = mock(OnCompleteListener.class);

        gameDb.updateGameDetails("game123", updates, listener);

        verify(mockGamesRef).child("game123");
        verify(mockGameRef).updateChildren(eq(updates));
        verify(mockTask).addOnCompleteListener(listener);
    }

    @Test
    public void updateGameDetails_shouldPassCorrectEditedValues() {
        Map<String, Object> updates = new HashMap<>();
        updates.put("teamA", "Sharks");
        updates.put("teamB", "Wolves");
        updates.put("gameVenue", "West Arena");
        updates.put("gameType", "Volleyball");
        updates.put("gameDate", 1888000000000L);

        Task<Void> mockTask = mock(Task.class);
        when(mockGameRef.updateChildren(anyMap())).thenReturn(mockTask);

        OnCompleteListener<Void> listener = mock(OnCompleteListener.class);

        gameDb.updateGameDetails("game123", updates, listener);

        ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);
        verify(mockGameRef).updateChildren(captor.capture());

        Map<String, Object> captured = captor.getValue();
        assertNotNull(captured);
        assertEquals("Sharks", captured.get("teamA"));
        assertEquals("Wolves", captured.get("teamB"));
        assertEquals("West Arena", captured.get("gameVenue"));
        assertEquals("Volleyball", captured.get("gameType"));
        assertEquals(1888000000000L, captured.get("gameDate"));
    }
}