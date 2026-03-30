package com.example.playcheck;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;

/* Local Tests for the logic of CreatTeamPlayer activity */
public class CreateTeamPlayerTest {

    private ArrayList<String> playerNames;
    private ArrayList<String> playerIds;
    private ArrayList<String> currentAddedPlayerNames;
    private ArrayList<String> currentAddedPlayerIds;

    @Before
    public void setUp() {
        // Mocking the data structures used in CreateTeamPlayer
        playerNames = new ArrayList<>();
        playerIds = new ArrayList<>();
        currentAddedPlayerNames = new ArrayList<>();
        currentAddedPlayerIds = new ArrayList<>();

        // Dummy data
        playerNames.add("John Doe");
        playerIds.add("id_123");
        playerNames.add("Jane Smith");
        playerIds.add("id_456");
    }

    @Test
    public void testAddPlayerToListSuccess() {
        String inputName = "John Doe";

        // Logic from addPlayerButton.setOnClickListener
        assertTrue("Player should exist in database list", playerNames.contains(inputName));

        int index = playerNames.indexOf(inputName);
        String id = playerIds.get(index);

        if (!currentAddedPlayerIds.contains(id)) {
            currentAddedPlayerIds.add(id);
            currentAddedPlayerNames.add(inputName);
        }

        assertEquals(1, currentAddedPlayerIds.size());
        assertEquals("id_123", currentAddedPlayerIds.get(0));
    }

    @Test
    public void testAddDuplicatePlayerFails() {
        // Pre-add a player
        currentAddedPlayerIds.add("id_123");

        String inputName = "John Doe";
        int index = playerNames.indexOf(inputName);
        String id = playerIds.get(index);

        boolean alreadyAdded = currentAddedPlayerIds.contains(id);

        assertTrue("Logic should detect player is already added", alreadyAdded);
        assertEquals("List size should not increase", 1, currentAddedPlayerIds.size());
    }

    @Test
    public void testCaptainValidationPlayerMustBeInTeam() {
        currentAddedPlayerNames.add("John Doe"); // Team contains John

        String selectedCaptain = "Jane Smith"; // Set Jane as captain

        int index = currentAddedPlayerNames.indexOf(selectedCaptain);

        assertTrue("Captain must be a team member", index < 0);
    }
}
