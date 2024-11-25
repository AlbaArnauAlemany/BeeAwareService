package ch.unil.doplab.beeaware.service;

import ch.unil.doplab.beeaware.Domain.DTO.LocationDTO;
import ch.unil.doplab.beeaware.Domain.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

class LocationServiceTest {

    private LocationService locationService;
    private GeoApiService geoApiService;
    private BeezzerService beezzerService;
    private SymptomService symptomService;
    private Location ecublens;
    private Location nyon;
    private Location vevey;
    private Location payerne;

    private String APIKEY = ResourceBundle.getBundle("application").getString("API_KEY");

    @BeforeEach
    void setUp() {
        // Initiate instances
        geoApiService = new GeoApiService(APIKEY);
        locationService = new LocationService(geoApiService);
        symptomService = new SymptomService();
        beezzerService = new BeezzerService(locationService, symptomService);

        ecublens = new Location(1040, "CH");
        nyon = new Location(1260, "CH");
        vevey = new Location(1800, "CH");
        payerne = new Location(1530, "CH");

//        // ADD locations to LOCATIONS LIST
//        locationService.addOrCreateLocation(ecublens);
//        locationService.addOrCreateLocation(nyon);
//        locationService.addOrCreateLocation(vevey);
//        locationService.addOrCreateLocation(payerne);
    }

    @Test
    void testAddOrCreateLocationNewLocation() {

        // Assert that Location was added correctly
        Location createdLocation = locationService.addOrCreateLocation(ecublens);
        assertNotNull(createdLocation);
        assertNotNull(createdLocation.getId());
        assertEquals(ecublens.getNPA(), createdLocation.getNPA());
        assertEquals(ecublens.getCountry(), createdLocation.getCountry());

        // Assert that the added Location is in All Registered Locations
        List<LocationDTO> allLocations = locationService.getAllRegisteredLocations();
        assertTrue(allLocations.stream()
                .anyMatch(loc -> loc.getNPA() == createdLocation.getNPA() &&
                        loc.getCountry().equals(createdLocation.getCountry())));
    }

    @Test
    void testAddOrCreateLocationExistingLocation() {

        // Assert that existing location was not duplicated
        Location location = locationService.addOrCreateLocation(vevey);
        Location locationDuplicated = locationService.addOrCreateLocation(vevey);
        assertNotNull(locationDuplicated);
        assertEquals(locationDuplicated.getId(), location.getId());
        assertEquals(locationDuplicated.getNPA(), location.getNPA());
        assertEquals(locationDuplicated.getCountry(), location.getCountry());
        List<LocationDTO> allLocations = locationService.getAllRegisteredLocations();
        assertEquals(1, allLocations.size());
    }

    @Test
    void testAddOrCreateLocationInvalidLocation() {

        // Assert that invalid Location was not added
        Location invalidLocation = new Location(0, "INVALID_COUNTRY");
        Location createdLocation = locationService.addOrCreateLocation(invalidLocation);
        assertNull(createdLocation);
    }

    @Test
    void testRemoveLocation() {

        // Assert the removing a location by ID works
        locationService.addOrCreateLocation(payerne);
        locationService.addOrCreateLocation(nyon);
        Long payerneId = payerne.getId();
        assertTrue(locationService.removeLocation(payerneId));
        List<LocationDTO> allLocations = locationService.getAllRegisteredLocations();
        assertEquals(1, allLocations.size());

        // Assert that removing a location by an invalid ID returns false
        assertFalse(locationService.removeLocation(999L));
    }
}