package ch.unil.doplab.beeaware.domain;

import ch.unil.doplab.beeaware.Domain.*;
import ch.unil.doplab.beeaware.DTO.PollenInfoDTO;
import ch.unil.doplab.beeaware.service.*;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.Getter;
import lombok.Setter;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
@Getter
@Setter
public class ApplicationState {
    private Map<Long, PollenLocationIndex> PollenLocationIndexArray;
    private String APIKEY = ResourceBundle.getBundle("application").getString("API_KEY");
    private Map<Long, Long> allergens;
    private BeezzerService beezzerService;
    private LocationService locationService;
    private SymptomService symptomService;
    private PollenLocationIndexService pollenLocationIndexService;
    private AllergenService allergenService;
    private GeoApiService geoApiService;
    private ForeCastService foreCastService;
    private TokenService tokenService;

    private Logger logger = Logger.getLogger(ApplicationState.class.getName());

    @PostConstruct
    public void init() {
        beezzerService = new BeezzerService();
        locationService = new LocationService();
        symptomService = new SymptomService();
        pollenLocationIndexService = new PollenLocationIndexService();
        allergenService = new AllergenService();
        geoApiService = new GeoApiService(APIKEY);
        foreCastService = new ForeCastService(APIKEY, pollenLocationIndexService);
        tokenService = new TokenService();

        populateApplicationState();
    }


    // TODO: GOOD addBeezzer
    public Beezzer addBeezzer(Beezzer beezzer){
        for (Map.Entry<Long, Beezzer> bee: beezzers.entrySet()) {
            if (beezzer.getUsername() != null &&
                    bee.getValue().getUsername() != null &&
                    beezzer.getUsername().equals(bee.getValue().getUsername())) {
                throw new IllegalArgumentException("Username " + beezzer.getUsername() + " already used. Please try a new one.");
            }
        }
        if (beezzer.getId() == null) {
            Long newId = idBeezzer++;
            beezzers.put(newId, beezzer);
            beezzer.setId(newId);
        } else {
            beezzers.put(beezzer.getId(), beezzer);
        }
        return beezzer;
    }

    // TODO: GOOD getBeezzer
    public Beezzer getBeezzer(Long id) { return beezzers.get(id); }

    // TODO: GOOD getAllBeezzers
    public Map<Long, Beezzer> getAllBeezzers() { return beezzers; }

    // TODO: GOOD setBeezzer
    public boolean setBeezzer(Long id, Beezzer beezzer) {
        var theBeezzer = beezzers.get(id);
        if (theBeezzer == null) {
            return false;
        }
        var username = beezzer.getUsername();
        if (!theBeezzer.getUsername().equals(username)) {
            throw new IllegalArgumentException("A user named '" + beezzer.getUsername() + "' already exists");
        }
        theBeezzer.replaceWith(beezzer);
        return true;
    }

    // TODO: GOOD removeBeezzer
    public boolean removeBeezzer(Long id) {
        var beezzer = beezzers.get(id);
        if (beezzer == null) {
            return false;
        }
        beezzers.remove(id);
        return true;
    }

    private void populateApplicationState() {
        // Alba: Utils.testModeOn(); used in StudyBuddy!!
        try {
            Location location = new Location(1024, "CH");
            location.setCoordinate(geoApiService.getCoordinates(location.getNPA(), location.getCountry()));
            locationService.addLocation(location);
            Beezzer ony = new Beezzer("Ony", "o@unil.ch", "Q.-wDw124", location);
            allergenService.addAllergen(Pollen.getPollenByName("Grasses"), ony);
            allergenService.addAllergen(Pollen.getPollenByName("Weed"), ony);

            beezzerService.addBeezzer(ony);
            for (Map.Entry<Long, Beezzer> beezer: beezzerService.getBeezzers().entrySet()) {
                logger.log( Level.INFO, beezer.toString());
            }
            Symptom symptom1 = new Symptom(ony.getId(), 8, false);
            Symptom symptom2 = new Symptom(ony.getId(), 5, false);
            Symptom symptom3 = new Symptom(ony.getId(), 3, true);
            symptomService.addSymptom(symptom1);
            symptomService.addSymptom(symptom2);

            Date d1 = new GregorianCalendar(2024, Calendar.FEBRUARY, 11).getTime();
            Date d2 = new GregorianCalendar(2024, Calendar.FEBRUARY, 21).getTime();

            symptomService.addSymptom(symptom1, d1);
            symptomService.addSymptom(symptom3, d2);
            symptomService.addSymptom(symptom2);

            for (Symptom symptom:symptomService.getSymptomsForASpecificBeezzer(ony.getId())) {
                System.out.println(symptom);
            }

            foreCastService.forecastAllLocation(locationService.getLocations());

            List<PollenInfoDTO> pollenShortDTOs = foreCastService.getIndexForSpecificBeezzer(ony);
            for (PollenInfoDTO pollen : pollenShortDTOs) {
                logger.log( Level.INFO, pollen.toString());
            }

            for (Map.Entry<Long, PollenLocationIndex> pollenLocationIndex: pollenLocationIndexService.getPollenLocationIndexMap().entrySet()) {
                logger.log( Level.INFO, pollenLocationIndex.toString());
            }

            // Utils.testModeOff(); used in StudyBuddy!!
        } catch (Exception e){
            logger.log( Level.SEVERE, "Error during populate users");
            logger.log( Level.SEVERE, e.getMessage());
        }
    }

}
