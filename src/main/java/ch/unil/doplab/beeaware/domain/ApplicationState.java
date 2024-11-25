package ch.unil.doplab.beeaware.domain;

import ch.unil.doplab.beeaware.Domain.DTO.PollenInfoDTO;
import ch.unil.doplab.beeaware.Domain.DTO.SymptomsDTO;
import ch.unil.doplab.beeaware.Domain.*;
import ch.unil.doplab.beeaware.service.*;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Initialized;
import jakarta.inject.Inject;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
@Getter
@Setter
public class ApplicationState {
    private Map<Long, PollenLocationInfo> PollenLocationIndexArray;
    private String APIKEY = ResourceBundle.getBundle("application").getString("API_KEY");
    private Map<Long, Long> allergens;
    private LocationService locationService;
    private GeoApiService geoApiService;
    private BeezzerService beezzerService;
    private SymptomService symptomService;
    private PollenLocationIndexService pollenLocationIndexService;
    private ForeCastService foreCastService;
    private IndexPollenForBeezzer indexPollenForBeezzer;
    private TokenService tokenService;
    private DailyTaskService dailyTaskService;

    private Logger logger = Logger.getLogger(ApplicationState.class.getName());

    @PostConstruct
    public void init() {
        geoApiService = new GeoApiService(APIKEY);
        locationService = new LocationService(geoApiService);
        symptomService = new SymptomService();
        beezzerService = new BeezzerService(locationService, symptomService);
        pollenLocationIndexService = new PollenLocationIndexService();
        foreCastService = new ForeCastService(APIKEY, pollenLocationIndexService);
        indexPollenForBeezzer = new IndexPollenForBeezzer(beezzerService, foreCastService, pollenLocationIndexService);
        tokenService = new TokenService();
        dailyTaskService = new DailyTaskService(foreCastService, pollenLocationIndexService, locationService);

        populateApplicationState();
    }

    private void populateApplicationState() {
        try {
            logger.log(Level.SEVERE, "Populating application");
            Location location = new Location(41001, "ES");
            locationService.addOrCreateLocation(location);
            Beezzer ony = new Beezzer("Ony", "o@unil.ch", PasswordUtilis.hashPassword("Q.-wDw124"), location, Role.BEEZZER);
            beezzerService.addBeezzer(ony);
            beezzerService.addAllergen("Grasses", ony.getId());
            beezzerService.addAllergen("Weed", ony.getId());

            Location locationAlb = new Location(41389, "ES");
            locationService.addOrCreateLocation(locationAlb);
            Beezzer alb = new Beezzer("alb", "alb@unil.ch", PasswordUtilis.hashPassword("Q.-wDw123"), location, Role.ADMIN);
            beezzerService.addBeezzer(alb);

            for (Map.Entry<Long, Beezzer> beezzer : beezzerService.getBeezzers().entrySet()) {
                logger.log(Level.INFO, beezzer.toString());
            }

            Date d3 = new GregorianCalendar(2024, Calendar.FEBRUARY, 11).getTime();
            Date d4 = new GregorianCalendar(2024, Calendar.FEBRUARY, 15).getTime();

            Symptom symptom1 = new Symptom(ony.getId(), Reaction.HIGH_REACTION, false);
            Symptom symptom2 = new Symptom(ony.getId(), Reaction.MODERATE_REACTION, false);
            Symptom symptom3 = new Symptom(ony.getId(), Reaction.LOW_REACTION, true);
            Symptom symptom4 = new Symptom(ony.getId(), ch.unil.doplab.beeaware.Domain.Reaction.LOW_REACTION, true);

            symptom3.setDate(d3);
            symptom4.setDate(d4);

            Date now = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(now);
            calendar.add(Calendar.HOUR, 2);
            Date plusTwoHour = calendar.getTime();
            Token token = new Token("u7u6m0f9rhvvtml0ibssscagoe", plusTwoHour, 1L, Role.ADMIN);
            tokenService.addToken(token);

            symptomService.addSymptom(symptom1);
            symptomService.addSymptom(symptom2);
            symptomService.addSymptom(symptom3);
            symptomService.addSymptom(symptom4);

            for (SymptomsDTO symptom : symptomService.getSymptom(ony.getId())) {
                System.out.println(symptom);
                logger.log(Level.INFO, "{0}", symptom);
            }

            logger.log(Level.INFO, "Forecasting pollen information for all locations...");
            foreCastService.forecastAllLocation(locationService.getLocations());

            List<PollenInfoDTO> pollenShortDTOs = indexPollenForBeezzer.getIndex(ony.getId());
            for (PollenInfoDTO pollen : pollenShortDTOs) {
                logger.log(Level.INFO, pollen.toString());
            }

            for (Map.Entry<Long, PollenLocationIndex> pollenLocationIndex : pollenLocationIndexService.getPollenLocationIndexMap().entrySet()) {
                logger.log(Level.INFO, pollenLocationIndex.toString());
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error during populate users");
            logger.log(Level.SEVERE, "{0}", e.getStackTrace()[0]);
            logger.log(Level.SEVERE, e.getMessage());
        }
    }
}
