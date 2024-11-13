package ch.unil.doplab.beeaware.domain;

import ch.unil.doplab.beeaware.DTO.BeezzerDTO;
import ch.unil.doplab.beeaware.Domain.*;
import ch.unil.doplab.beeaware.service.*;
import ch.unil.doplab.beeaware.DTO.LocationDTO;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.ResourceBundle;

import static ch.unil.doplab.beeaware.domain.Utils.printMethodName;
import static org.junit.jupiter.api.Assertions.*;

public class BeezzerServiceTest {

    private BeezzerService beezzerService;
    private GeoApiService coordinatesSetUp;
    private ForeCastService foreCastService;
    private LocationService locationsList;
    private PollenLocationIndexService pollenLocationIndexService;
    private String APIKEY = ResourceBundle.getBundle("application").getString("API_KEY");

    private Beezzer alex;
    private Beezzer dafne;
    private Beezzer paul;
    private Beezzer clara;
    private Location ecublens;
    private Location nyon;
    private Location vevey;
    private Location payerne;

    @SneakyThrows
    @BeforeEach
    void setUp() {
        // Initiate instances
        beezzerService = new BeezzerService();
        locationsList = new LocationService();
        coordinatesSetUp = new GeoApiService(APIKEY);
        foreCastService = new ForeCastService(APIKEY, pollenLocationIndexService);

        ecublens = new Location(1040, "CH");
        nyon = new Location(1260, "CH");
        vevey = new Location(1800, "CH");
        payerne = new Location(1530, "CH");

        alex = new Beezzer("alex", "alex@unil.ch", "A-.yKo321", ecublens, Role.BEEZZER);
        dafne = new Beezzer("dafne", "dafne@unilch", "B-.lPe654", nyon, Role.BEEZZER);
        paul = new Beezzer("paul", "paul@unilc.h", "C-.rBx987", vevey, Role.BEEZZER);
        clara = new Beezzer("clara", "clara@unil.ch", "D-.gAf741", payerne, Role.BEEZZER);

        // GET coordinates for each location
        coordinatesSetUp.getCoordinates(ecublens);
        coordinatesSetUp.getCoordinates(nyon);
        coordinatesSetUp.getCoordinates(vevey);
        coordinatesSetUp.getCoordinates(payerne);

        // ADD locations to LOCATIONS LIST
        locationsList.addLocation(ecublens);
        locationsList.addLocation(nyon);
        locationsList.addLocation(vevey);
        locationsList.addLocation(payerne);

        // ADD locations to each beezzer
        alex.setLocation(ecublens);
        dafne.setLocation(nyon);
        paul.setLocation(vevey);
        clara.setLocation(payerne);

        // ADD beezzers to the BEEZZERS LIST
        beezzerService.addBeezzer(alex);
        beezzerService.addBeezzer(dafne);
        beezzerService.addBeezzer(paul);
        beezzerService.addBeezzer(clara);

        // ADD allergens to some beezzers
        beezzerService.addAllergen("olive", alex.getId());
        beezzerService.addAllergen("ASH", alex.getId());
        beezzerService.addAllergen("grasses", alex.getId());
        beezzerService.addAllergen("cottonwood", dafne.getId());
        beezzerService.addAllergen("pine", dafne.getId());
        beezzerService.addAllergen("ASH", dafne.getId());
        beezzerService.addAllergen("ragweed", paul.getId());
        beezzerService.addAllergen("Weed", paul.getId());

        // FORECAST PollenLocationIndex
        foreCastService.forecastAllLocation(locationsList.getLocations());

    }

    @Test
    void testAddBeezzer() {
        printMethodName();

        // Assert that all previous added beezzers have been added to the list
        assertEquals(4, beezzerService.getAllBeezzers().size());

        // Assert that after adding a beezzer to beezzers, it has been assigned an ID
        assertNotNull(alex.getId());

        // Assert that a beezzer with the same username is not added to the list
        Beezzer dafneBis = new Beezzer();
        dafneBis.setUsername("dafne");
        assertEquals(4, beezzerService.getAllBeezzers().size());
    }

    @Test
    void testGetBeezzer() {
        printMethodName();

        // Assert if the ID and username of a beezzer in the beezzers list is the correct one when using getBeezzer()
        Long paulId = paul.getId();
        BeezzerDTO result = beezzerService.getBeezzer(paulId);
        assertNotNull(result);
        assertEquals(paulId, result.getId());
        assertEquals("paul", result.getUsername());

        // Assert that using getBeezzer() with a non existent ID returns a null value
        assertNull(beezzerService.getBeezzer(999L));
    }

    @Test
    void testGetAllBeezzers() {
        printMethodName();

        // Assert that getAllBeezzers returns all the previously added beezzers
        List<BeezzerDTO> result = beezzerService.getAllBeezzers();
        assertEquals("alex", result.get(0).getUsername());
        assertEquals("dafne", result.get(1).getUsername());
        assertEquals("paul", result.get(2).getUsername());
        assertEquals("clara", result.get(3).getUsername());

        // Assert that getAllBeezzers returns an empty list when used on an empty service
        BeezzerService beezzerServiceEmpty = new BeezzerService();
        List<BeezzerDTO> results = beezzerServiceEmpty.getAllBeezzers();
        assertTrue(results.isEmpty());
    }

    @Test
    void testRemoveBeezzers() {
        printMethodName();

        // Assert that when using removeBeezzer, it correctly removes the beezzer form the beezzers list
        boolean result = beezzerService.removeBeezzer(alex.getId());
        assertTrue(result);
        assertNull(beezzerService.getBeezzer(alex.getId()));

        // Asser that when removeBeezzer is used on an invalid id it returns false
        boolean results = beezzerService.removeBeezzer(999L);
        assertFalse(results);
    }

    @Test
    void testAddAllergen() {
        printMethodName();

        // Assert that one allergen was added to the previously empty allergen list of a Beezzer
        beezzerService.addAllergen("pine", clara.getId());
        assertEquals(1, beezzerService.getBeezzerAllergens(clara.getId()).getPollenList().size());

        // Assert that duplicated allergens are not allowed for the same beezzer
        int initialSize = beezzerService.getBeezzerAllergens(dafne.getId()).getPollenList().size();
        beezzerService.addAllergen("cottonwood", dafne.getId());
        int newSize = beezzerService.getBeezzerAllergens(dafne.getId()).getPollenList().size();
        assertEquals(initialSize, newSize, "Duplicate allergen should not be added");

        // Assert that getBeezzerAllergens() returns null when beezzer doesn't exist
        beezzerService.removeBeezzer(clara.getId());
        assertNull(beezzerService.getBeezzerAllergens(clara.getId()));
    }

    @Test
    void testRemoveAllergen() {
        printMethodName();

        // Assert if an allergen was successfully removed
        Long allergenId = paul.getAllergens().keySet().iterator().next();
        boolean removed = beezzerService.removeAllergen(allergenId, paul.getId());
        assertTrue(removed);
        assertFalse(paul.getAllergens().containsKey(allergenId));

        // Try to remove a non-existing allergen
        assertFalse(beezzerService.removeAllergen(999L, paul.getId()));
    }

    @Test
    void testSetBeezzer() {
        printMethodName();

        // Assert if a change in username is correctly set using setBeezzer
        clara.setUsername("newClara");
        beezzerService.setBeezzer(clara);
        BeezzerDTO updatedBeezzer = beezzerService.getBeezzer(clara.getId());
        assertEquals("newAlexUsername", updatedBeezzer.getUsername());
    }

    @Test
    void testGetBeezzerLocation() {
        printMethodName();

        // Assert if the correction location is return using getBeezzerLocation()
        LocationDTO locationDTO = beezzerService.getBeezzerLocation(dafne.getId());
        assertNotNull(locationDTO);
        assertEquals("1260", locationDTO.getNPA());

        // Assert that location is null for an invalid beezzer ID
        assertNull(beezzerService.getBeezzerLocation(999L));
    }
}