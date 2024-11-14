package ch.unil.doplab.beeaware.domain;

import ch.unil.doplab.beeaware.DTO.LocationDTO;
import ch.unil.doplab.beeaware.Domain.Location;
import ch.unil.doplab.beeaware.service.GeoApiService;
import ch.unil.doplab.beeaware.service.LocationService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

class LocationServiceTest {

    private LocationService locationService;
    private GeoApiService geoApiService;
    private Location ecublens;
    private Location nyon;
    private Location vevey;
    private Location payerne;

    private String APIKEY = ResourceBundle.getBundle("application").getString("API_KEY");

    @SneakyThrows
    @BeforeEach
    void setUp() {
        // Initiate instances
        locationService = new LocationService();
        geoApiService = new GeoApiService(APIKEY);

        ecublens = new Location(1040, "CH");
        nyon = new Location(1260, "CH");
        vevey = new Location(1800, "CH");
        payerne = new Location(1530, "CH");

        // GET coordinates for each location
        geoApiService.getCoordinates(ecublens);
        geoApiService.getCoordinates(nyon);
        geoApiService.getCoordinates(vevey);
        geoApiService.getCoordinates(payerne);

        // ADD locations to LOCATIONS LIST
        locationService.addLocation(ecublens);
        locationService.addLocation(nyon);
        locationService.addLocation(vevey);
        locationService.addLocation(payerne);
    }

    @SneakyThrows
    @Test
    void testAddNewLocation() {

        // Assert that all locations have been added correctly to the locations' list of the service
        List<LocationDTO> allLocations = locationService.getAllRegisteredLocations();
        assertEquals(4, allLocations.size());
        assertEquals(1800, allLocations.get(2).getNPA());
        assertEquals("CH", allLocations.get(0).getCountry());

        // Assert that duplicated locations are not added to the list
        Location ecublensBis = new Location(1040, "CH");
        geoApiService.getCoordinates(ecublensBis);
        locationService.addLocation(ecublensBis);
        assertEquals(4, allLocations.size());
    }

    @Test
    void testRemoveLocation() {

        // Assert the removing a location by ID works
        Long payerneId = payerne.getId();
        assertTrue(locationService.removeLocation(payerneId));
        List<LocationDTO> allLocations = locationService.getAllRegisteredLocations();
        assertEquals(3, allLocations.size());

        // Assert that removing a location by an invalid ID returns false
        assertFalse(locationService.removeLocation(999L));
    }
}