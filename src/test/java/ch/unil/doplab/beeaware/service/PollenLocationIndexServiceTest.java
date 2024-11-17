package ch.unil.doplab.beeaware.service;

import ch.unil.doplab.beeaware.Domain.Location;
import ch.unil.doplab.beeaware.Domain.PollenLocationIndex;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PollenLocationIndexServiceTest {

    private PollenLocationIndexService pollenLocationIndexService;
    private Location ecublens;
    private Location nyon;

    @BeforeEach
    void setUp() {
        pollenLocationIndexService = new PollenLocationIndexService();

        // Define Locations with IDs
        ecublens = new Location(1040, "CH"); // Create Location for Ecublens with NPA and country
        ecublens.setId(1L); // Manually setting ID for ecublens

        nyon = new Location(1260, "CH"); // Create Location for Nyon with NPA and country
        nyon.setId(2L); // Manually setting ID for nyon
    }

    @Test
    void testAddPollenLocationIndex_NewEntry() {
        PollenLocationIndex pollenLocationIndex = new PollenLocationIndex();
        pollenLocationIndex.setLocation(ecublens);
        pollenLocationIndex.setDisplayName("PollenA_EN");
        pollenLocationIndex.setDate(new Date());
        pollenLocationIndex.setRecommendation(Collections.singletonList("Recommended"));
        pollenLocationIndex.setCrossReaction("Cross1");

        // Act
        boolean result = pollenLocationIndexService.addPollenLocationIndex(pollenLocationIndex);

        // Assert
        assertTrue(result, "The result should be true when adding a new PollenLocationIndex.");
        assertNotNull(pollenLocationIndex.getId(), "The ID should not be null.");
        assertEquals(0L, pollenLocationIndex.getId(), "The first added pollenLocationIndex should have ID 0.");
        assertEquals(1, pollenLocationIndexService.getPollenLocationIndexMap().size(), "The map should contain 1 element after adding a new entry.");

        // Verify Recommendation and Cross
        assertEquals(Collections.singletonList("Recommended"), pollenLocationIndex.getRecommendation(), "The Recommendation should be 'Recommended'.");
        assertEquals("Cross1", pollenLocationIndex.getCrossReaction(), "The Cross should be 'Cross1'.");
    }


    @Test
    void testRemovePollenLocationIndex_ExistingEntry() {
        PollenLocationIndex pollenLocationIndex = new PollenLocationIndex();
        pollenLocationIndex.setId(1L); // Simulating an existing entry with ID
        pollenLocationIndex.setLocation(nyon); // Use the Location object directly
        pollenLocationIndex.setDisplayName("PollenB_EN");
        pollenLocationIndex.setDate(new Date());
        pollenLocationIndex.setRecommendation(Collections.singletonList("Recommended"));  // Set Recommendation
        pollenLocationIndex.setCrossReaction("Cross2");  // Set Cross

        pollenLocationIndexService.addPollenLocationIndex(pollenLocationIndex);

        // Act
        boolean result = pollenLocationIndexService.removePollenLocationIndex(1L); // Remove by ID

        // Assert
        assertTrue(result, "The pollenLocationIndex should be removed successfully.");
        assertEquals(0, pollenLocationIndexService.getPollenLocationIndexMap().size(), "The map should be empty after removal.");
    }

    @Test
    void testRemovePollenLocationIndex_NonExistingEntry() {
        // Act
        boolean result = pollenLocationIndexService.removePollenLocationIndex(999L); // ID doesn't exist

        // Assert
        assertFalse(result, "Removing a non-existing PollenLocationIndex should return false.");
        assertEquals(0, pollenLocationIndexService.getPollenLocationIndexMap().size(), "The map should still be empty.");
    }
}
