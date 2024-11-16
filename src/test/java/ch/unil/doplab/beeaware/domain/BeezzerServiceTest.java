package ch.unil.doplab.beeaware.domain;

import ch.unil.doplab.beeaware.DTO.AllergenDTO;
import ch.unil.doplab.beeaware.DTO.BeezzerDTO;
import ch.unil.doplab.beeaware.Domain.*;
import ch.unil.doplab.beeaware.service.*;
import ch.unil.doplab.beeaware.DTO.LocationDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;

import static ch.unil.doplab.beeaware.domain.Utils.printMethodName;
import static org.junit.jupiter.api.Assertions.*;

public class BeezzerServiceTest {

    private LocationService locationsList;
    private GeoApiService geoApiService;
    private BeezzerService beezzerService;
    private ForeCastService foreCastService;
    private SymptomService symptomService;
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
        geoApiService = new GeoApiService(APIKEY);
        locationsList = new LocationService(geoApiService);
        symptomService = new SymptomService();
        beezzerService = new BeezzerService(locationsList, symptomService);
        pollenLocationIndexService = new PollenLocationIndexService();
        foreCastService = new ForeCastService(APIKEY, pollenLocationIndexService);
        ecublens = new Location(1040, "CH");
        nyon = new Location(1260, "CH");
        vevey = new Location(1800, "CH");
        payerne = new Location(1530, "CH");

        alex = new Beezzer("alex", "alex@unil.ch", "A-.yKo321", ecublens, Role.BEEZZER);
        dafne = new Beezzer("dafne", "dafne@unilch", "B-.lPe654", nyon, Role.BEEZZER);
        paul = new Beezzer("paul", "paul@unilc.h", "C-.rBx987", vevey, Role.BEEZZER);
        clara = new Beezzer("clara", "clara@unil.ch", "D-.gAf741", payerne, Role.BEEZZER);

        // ADD locations to LOCATIONS LIST
        locationsList.addOrCreateLocation(ecublens);
        locationsList.addOrCreateLocation(nyon);
        locationsList.addOrCreateLocation(vevey);
        locationsList.addOrCreateLocation(payerne);

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

    @SneakyThrows
    @Test
    void testGetBeezzerIfExist() {

        // Assert that the correct existing beezzer is returned
        Long alexId = alex.getId();
        assertNotNull(beezzerService.getBeezzerIfExist(alexId));
        assertEquals(alexId, beezzerService.getBeezzerIfExist(alexId).getId());

        // Assert that exception is thrown when beezzer doesn't exist
        assertThrows(Exception.class, () -> beezzerService.getBeezzerIfExist(999L));
    }

    @SneakyThrows
    @Test
    void testBeezzerExist() {

        // Assert that BeezzerExist return true when the Beezzer exists
        assertTrue(beezzerService.beezzerExist(alex.getId()));

        // Assert that BeezzerExist return false when the Beezzer doesn't exist
        assertFalse(beezzerService.beezzerExist(999L));
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

        // Assert that exception is thrown when beezzer doesn't exist
        assertThrows(Exception.class, () -> beezzerService.getBeezzer(999L));
        // assertNull(beezzerService.getBeezzer(999L));
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

        // Assert remove beezzer for valid id
        Long alexId = alex.getId();
        assertTrue(beezzerService.removeBeezzer(alexId));

        // Assert that exception is thrown when beezzer doesn't exist
        assertThrows(Exception.class, () -> beezzerService.getBeezzerIfExist(999L));

        // Assert remove beezzer for non-existent id returns false
        assertFalse(beezzerService.removeBeezzer(999L));
    }

    @Test
    void testAddAllergen() {
        printMethodName();

        // Assert that one allergen was added to the previously empty allergen list of a Beezzer
        beezzerService.addAllergen("Oak", clara.getId());
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
        String stringAllergen = "Weed";
        boolean removed = beezzerService.removeAllergen(stringAllergen, paul.getId());
        assertTrue(removed);

        // Try to remove a non-existing allergen
        String stringNonAllergen = "cactus";
        assertFalse(beezzerService.removeAllergen(stringNonAllergen, paul.getId()));
    }

    @Test
    void testSetBeezzer() {
        printMethodName();

        // Assert if a change in username is correctly set using setBeezzer
        dafne.setUsername("newDafne");
        beezzerService.setBeezzer(dafne);
        BeezzerDTO updatedBeezzer = beezzerService.getBeezzer(dafne.getId());
        assertNotNull(updatedBeezzer);
        assertEquals("newDafne", updatedBeezzer.getUsername());
    }

    @Test
    void testGetBeezzerLocation() {
        printMethodName();

        // Assert if the correction location is return using getBeezzerLocation()
        LocationDTO locationDTO = beezzerService.getBeezzerLocation(dafne.getId());
        assertNotNull(locationDTO);
        assertNotNull(locationDTO);
        assertEquals(1260, locationDTO.getNPA());

        // Assert that location is null for an invalid beezzer ID
        assertNull(beezzerService.getBeezzerLocation(999L));
    }

    @Test
    void testSearchForLocationAndAddIt() {
        printMethodName();

        // Assert if an existing location is returned
        Location duplicatedLocation = new Location(1530, "CH");
        Location foundLocation = beezzerService.searchForLocationAndAddIt(duplicatedLocation);
        assertNotNull(foundLocation);
        assertEquals(payerne, foundLocation);
        assertEquals(payerne.hashCode(), foundLocation.hashCode());

        // Assert that a new location is created when it doesn't exist in the service
        Location newLocation = new Location(3000, "CH");
        Location foundLocation2 = beezzerService.searchForLocationAndAddIt(newLocation);
        assertNotNull(foundLocation2);
        assertEquals(newLocation.getNPA(), foundLocation2.getNPA());
        assertEquals(newLocation.getCountry(), foundLocation2.getCountry());
        assertEquals(newLocation.hashCode(), foundLocation2.hashCode());
    }

    @SneakyThrows
    @Test
    void testCreateBeezzerFromJSON() {

        // Assert that a valid Beezzer is created through a Json file
        InputStream inputStream = BeezzerServiceTest.class.getClassLoader().getResourceAsStream("beezzerJson");
        String beezzerJson = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        Beezzer result = beezzerService.createBeezzerFromJSON(beezzerJson);
        assertNotNull(result);
        assertEquals("Estelle", result.getUsername());
        assertNotNull(result.getId());
        assertNotNull(result.getPassword());
        assertEquals(1040, result.getLocation().getNPA());
        assertEquals("CH", result.getLocation().getCountry());
        assertEquals(Role.BEEZZER, result.getRole());

        // Assert that the Beezzer was created with the correct allergens
        InputStream inputStream4 = BeezzerServiceTest.class.getClassLoader().getResourceAsStream("beezzerJsonAllergens");
        String beezzerJson4 = new String(inputStream4.readAllBytes(), StandardCharsets.UTF_8);
        Beezzer result4 = beezzerService.createBeezzerFromJSON(beezzerJson4);
        assertNotNull(result4);
        assertEquals("Lucas", result4.getUsername());
        assertNotNull(result4.getId());
        assertNotNull(result4.getPassword());
        assertEquals(1040, result4.getLocation().getNPA());
        assertEquals("CH", result4.getLocation().getCountry());
        assertEquals(Role.BEEZZER, result4.getRole());
        assertEquals(1, result4.getAllergens().size());
        assertTrue(result4.getAllergens().containsKey(Long.valueOf(0)));
        assertEquals("Pine", result4.getAllergens().get(Long.valueOf(0)).getPollenNameEN());
    }

    @SneakyThrows
    @Test
    void testSetBeezzerLocation() {
        printMethodName();

        // Assert that the location for an existing beezzer can be updated
        InputStream inputStream = BeezzerServiceTest.class.getClassLoader().getResourceAsStream("locationJson");
        String locationJson = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        assertTrue(beezzerService.setBeezzerLocation(paul.getId(), locationJson));
        LocationDTO updatedLocation = beezzerService.getBeezzerLocation(paul.getId());
        assertNotNull(updatedLocation);
        assertEquals(1002, updatedLocation.getNPA());
        assertEquals("CH", updatedLocation.getCountry());

        // Assert the location cannot be updated if a Beezzer doesn't exist
        assertFalse(beezzerService.setBeezzerLocation(999L, locationJson));
        assertNull(beezzerService.getBeezzerLocation(999L));
    }

    @SneakyThrows
    @Test
    void testAddAllergenSet() {
        String allergens = "["
                + "{\"pollenNameEN\": \"Alder\"},"
                + "{\"pollenNameEN\": \"ragweed\"}"
                + "]";

        // Assert that allergens for the existing beezzer can be updated(set)
        assertTrue(beezzerService.addAllergenSet(allergens, dafne.getId()));
        AllergenDTO updatedAllergens = beezzerService.getBeezzerAllergens(dafne.getId());
        assertNotNull(updatedAllergens);
        assertEquals(5, updatedAllergens.getPollenList().size());

        // Assert the adding allergens will not be successful if a Beezzer doesn't exist
        assertFalse(beezzerService.addAllergenSet(allergens, 999L));
        assertNull(beezzerService.getBeezzerAllergens(999L));
    }
}
