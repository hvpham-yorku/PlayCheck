package com.example.playcheck;

import static org.junit.Assert.*;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class EditGameLogicTest {

    private boolean isValidEditInput(String gameId, String teamA, String teamB, String venue, String type) {
        return gameId != null && !gameId.trim().isEmpty()
                && teamA != null && !teamA.trim().isEmpty()
                && teamB != null && !teamB.trim().isEmpty()
                && venue != null && !venue.trim().isEmpty()
                && type != null && !type.trim().isEmpty();
    }

    private Map<String, Object> buildUpdateMap(String teamA, String teamB, String venue, String type, long gameDate) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("teamA", teamA);
        updates.put("teamB", teamB);
        updates.put("gameVenue", venue);
        updates.put("gameType", type);
        updates.put("gameDate", gameDate);
        return updates;
    }

    @Test
    public void validEditInput_shouldPassValidation() {
        boolean result = isValidEditInput(
                "game123",
                "Lions",
                "Tigers",
                "Main Gym",
                "Basketball"
        );

        assertTrue(result);
    }

    @Test
    public void blankGameId_shouldFailValidation() {
        boolean result = isValidEditInput(
                "",
                "Lions",
                "Tigers",
                "Main Gym",
                "Basketball"
        );

        assertFalse(result);
    }

    @Test
    public void blankTeamA_shouldFailValidation() {
        boolean result = isValidEditInput(
                "game123",
                "",
                "Tigers",
                "Main Gym",
                "Basketball"
        );

        assertFalse(result);
    }

    @Test
    public void blankTeamB_shouldFailValidation() {
        boolean result = isValidEditInput(
                "game123",
                "Lions",
                "",
                "Main Gym",
                "Basketball"
        );

        assertFalse(result);
    }

    @Test
    public void blankVenue_shouldFailValidation() {
        boolean result = isValidEditInput(
                "game123",
                "Lions",
                "Tigers",
                "",
                "Basketball"
        );

        assertFalse(result);
    }

    @Test
    public void blankGameType_shouldFailValidation() {
        boolean result = isValidEditInput(
                "game123",
                "Lions",
                "Tigers",
                "Main Gym",
                ""
        );

        assertFalse(result);
    }

    @Test
    public void buildUpdateMap_shouldContainEditedFields() {
        long editedDate = 1775000000000L;

        Map<String, Object> updates = buildUpdateMap(
                "Sharks",
                "Wolves",
                "West Arena",
                "Volleyball",
                editedDate
        );

        assertEquals("Sharks", updates.get("teamA"));
        assertEquals("Wolves", updates.get("teamB"));
        assertEquals("West Arena", updates.get("gameVenue"));
        assertEquals("Volleyball", updates.get("gameType"));
        assertEquals(editedDate, updates.get("gameDate"));
        assertEquals(5, updates.size());
    }

    @Test
    public void buildUpdateMap_shouldReplaceOldValuesWithNewOnes() {
        long newDate = 1888000000000L;

        Map<String, Object> updates = buildUpdateMap(
                "Team Alpha",
                "Team Beta",
                "Updated Court",
                "Soccer",
                newDate
        );

        assertNotEquals("Old Court", updates.get("gameVenue"));
        assertNotEquals("Basketball", updates.get("gameType"));
        assertEquals("Updated Court", updates.get("gameVenue"));
        assertEquals("Soccer", updates.get("gameType"));
    }
}