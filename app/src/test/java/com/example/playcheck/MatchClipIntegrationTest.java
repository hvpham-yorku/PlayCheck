package com.example.playcheck;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import androidx.room.Database;

import com.example.playcheck.database.RefereeLinkToDatabase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Integration Test for Match Clips feature.
 * This test verifies the "Seam" between the Referee database logic and Firebase.
 */
public class MatchClipIntegrationTest {

    @Mock
    private DatabaseReference mockRootRef;
    @Mock
    private DatabaseReference mockMatchClipsRef;
    @Mock
    private DatabaseReference mockGameRef;
    @Mock
    private DatabaseReference mockPushRef;
    @Mock
    private FirebaseDatabase mockFirebaseDatabase;

    private static MockedStatic<FirebaseDatabase> staticFirebaseDatabase;
    private static MockedStatic<FirebaseApp> staticFirebaseApp;
    private static MockedStatic<FirebaseAuth> staticFirebaseAuth;

    private RefereeLinkToDatabase dbService;

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
        MockitoAnnotations.openMocks(this);

        staticFirebaseDatabase.when(FirebaseDatabase::getInstance).thenReturn(mockFirebaseDatabase);
        when(mockFirebaseDatabase.getReference()).thenReturn(mockRootRef);
        
        // Setup the path: matchClips -> gameId -> pushId
        when(mockRootRef.child("matchClips")).thenReturn(mockMatchClipsRef);
        when(mockMatchClipsRef.child(anyString())).thenReturn(mockGameRef);
        when(mockGameRef.push()).thenReturn(mockPushRef);
        when(mockPushRef.getKey()).thenReturn("testClipId");
        
        // Fix: Mock child(clipId) to return the same push reference
        when(mockGameRef.child(anyString())).thenReturn(mockPushRef);

        dbService = new RefereeLinkToDatabase();
    }

    @Test
    public void testSaveMatchClip_CallsFirebaseCorrectly() throws Exception {
        String gameId = "game123";
        String title = "Great Goal";
        String uri = "content://media/external/video/1";

        Task<Void> mockTask = mock(Task.class);
        when(mockPushRef.setValue(any(Map.class))).thenReturn(mockTask);
        
        // Capture the listener to trigger it manually
        ArgumentCaptor<OnCompleteListener<Void>> captor = ArgumentCaptor.forClass(OnCompleteListener.class);
        
        CompletableFuture<Void> future = dbService.saveMatchClip(gameId, title, uri);

        verify(mockPushRef).setValue(any(Map.class));
        verify(mockTask).addOnCompleteListener(captor.capture());

        // Simulate success
        when(mockTask.isSuccessful()).thenReturn(true);
        captor.getValue().onComplete(mockTask);

        future.get(); // Should complete without exception
    }

    @Test
    public void testGetMatchClips_ParsesFirebaseData() throws Exception {
        String gameId = "game123";

        // Setup the listener capture
        ArgumentCaptor<ValueEventListener> listenerCaptor = ArgumentCaptor.forClass(ValueEventListener.class);
        
        CompletableFuture<List<Map<String, String>>> future = dbService.getMatchClips(gameId);
        
        verify(mockGameRef).addListenerForSingleValueEvent(listenerCaptor.capture());

        // Simulate Firebase DataSnapshot
        DataSnapshot mockSnapshot = mock(DataSnapshot.class);
        DataSnapshot mockChild = mock(DataSnapshot.class);
        
        Map<String, String> clipData = Map.of("title", "Test Clip", "uri", "test_uri");
        
        when(mockSnapshot.getChildren()).thenReturn(List.of(mockChild));
        when(mockChild.getValue()).thenReturn(clipData);

        // Trigger the listener
        listenerCaptor.getValue().onDataChange(mockSnapshot);

        List<Map<String, String>> result = future.get();
        
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Clip", result.get(0).get("title"));
    }
}
