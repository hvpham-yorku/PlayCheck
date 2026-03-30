package com.example.playcheck;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.example.playcheck.database.PlayerLinkToDatabase;
import com.example.playcheck.puremodel.Game;
import com.example.playcheck.puremodel.Player;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/*
Intergration test for viewing match results and opponents.
User Stories:
PC-2.2 (Match History) -> Integration test for viewing match results and opponents.
PC-2.3 (Past Game Statistics) -> Integration test for viewing statistics of past games.
*/

@RunWith(MockitoJUnitRunner.class)
public class PlayerMatchHistoryIntegrationTest {

    private Player player;

    @Mock
    private PlayerLinkToDatabase mockPlayerDbService;

    @Before
    public void setUp() throws Exception {
        player = new Player();
        player.setUid("testPlayer123");

        // Inject the mock database service into the Player model
        try {
            Field dbField = Player.class.getDeclaredField("playerDbService");
            dbField.setAccessible(true);
            dbField.set(player, mockPlayerDbService);
        } catch (NoSuchFieldException e) {
            fail("Could not find playerDbService field in Player class");
        }
    }

    /**
     * PC-2.2: Integration test for viewing match results and opponents.
     */
    @Test
    public void testPlayerCanRetrieveMatchHistoryResults() throws Exception {
        // Arrange: Create mock match history data
        List<Game> mockHistory = new ArrayList<>();
        Game pastGame = new Game();
        pastGame.setGameId("game_001");
        pastGame.setTeamA("Team A");
        pastGame.setTeamB("Team B");
        mockHistory.add(pastGame);

        // Update: Player model calls getPlayerMatchHistory, not getMatchHistory
        when(mockPlayerDbService.getPlayerMatchHistory("testPlayer123"))
                .thenReturn(CompletableFuture.completedFuture(mockHistory));

        // Act: Call the model method
        CompletableFuture<List<Game>> future = player.loadMatchHistory();
        List<Game> result = future.get();

        // Assert: Verify data flow from DB to Model
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Team A", result.get(0).getTeamA());
        assertEquals("Team B", result.get(0).getTeamB());

        // Verify DB service was actually called with the correct UID
        verify(mockPlayerDbService).getPlayerMatchHistory("testPlayer123");
    }

    /**
     * PC-2.3: Integration test for viewing statistics of past games.
     */
    @Test
    public void testPlayerCanRetrievePastGameStatistics() throws Exception {
        // Arrange: Create mock stats data (goals, assists, etc.)
        Map<String, Object> mockStats = new HashMap<>();
        mockStats.put("goals", 5);
        mockStats.put("matchesPlayed", 10);
        mockStats.put("winRate", "60%");

        when(mockPlayerDbService.getPlayerStats("testPlayer123"))
                .thenReturn(CompletableFuture.completedFuture(mockStats));

        // Act: Model retrieves stats from DB
        CompletableFuture<Map<String, Object>> future = player.loadStatistics();
        Map<String, Object> result = future.get();

        // Assert
        assertNotNull(result);
        assertEquals(5, result.get("goals"));
        assertEquals("60%", result.get("winRate"));

        verify(mockPlayerDbService).getPlayerStats("testPlayer123");
    }
}
