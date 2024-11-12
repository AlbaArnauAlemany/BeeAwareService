package ch.unil.doplab.beeaware.domain;

import ch.unil.doplab.beeaware.DTO.SymptomsDTO;
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
    // private AllergenService allergenService;
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
        // allergenService = new AllergenService();
        geoApiService = new GeoApiService(APIKEY);
        foreCastService = new ForeCastService(APIKEY, pollenLocationIndexService);
        tokenService = new TokenService();

        populateApplicationState();
    }

    private void populateApplicationState() {
        // Alba: Utils.testModeOn(); used in StudyBuddy!!
        try {
            logger.log( Level.SEVERE, "Populating application");
            Location location = new Location(1024, "CH");
            location.setCoordinate(geoApiService.getCoordinates(location.getNPA(), location.getCountry()));
            locationService.addLocation(location);
            Beezzer ony = new Beezzer("Ony", "o@unil.ch", "Q.-wDw124", location, Role.BEEZZER);
            beezzerService.addBeezzer(ony);
            beezzerService.addAllergen("Grasses", ony.getId());
            beezzerService.addAllergen("Weed", ony.getId());

            Location locationAlb = new Location(1020, "CH");
            location.setCoordinate(geoApiService.getCoordinates(location.getNPA(), location.getCountry()));
            locationService.addLocation(locationAlb);
            Beezzer alb = new Beezzer("alb", "alb@unil.ch", "Q.-wDw123", location, Role.ADMIN);
            beezzerService.addBeezzer(alb);

            for (Map.Entry<Long, Beezzer> beezzer: beezzerService.getBeezzers().entrySet()) {
                logger.log(Level.INFO, beezzer.toString());
            }
            Symptom symptom1 = new Symptom(ony.getId(), ch.unil.doplab.beeaware.Domain.Level.HIGH_REACTION, false);
            Symptom symptom2 = new Symptom(ony.getId(), ch.unil.doplab.beeaware.Domain.Level.MODERATE_REACTION, false);
            Symptom symptom3 = new Symptom(ony.getId(), ch.unil.doplab.beeaware.Domain.Level.LOW_REACTION, true);
            symptomService.addSymptom(symptom1);
            symptomService.addSymptom(symptom2);

            Date d1 = new GregorianCalendar(2024, Calendar.FEBRUARY, 11).getTime();
            Date d2 = new GregorianCalendar(2024, Calendar.FEBRUARY, 21).getTime();

            symptomService.addSymptom(symptom1, d1);
            symptomService.addSymptom(symptom3, d2);
            symptomService.addSymptom(symptom2);

            for (SymptomsDTO symptom:symptomService.getSymptoms(ony.getId())) {
                System.out.println(symptom);
            }

            foreCastService.forecastAllLocation(locationService.getLocations());

            List<PollenInfoDTO> pollenShortDTOs = foreCastService.getIndex(ony);
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
