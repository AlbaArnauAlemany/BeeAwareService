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
            beezzerService.addAllergen("Mugwort", ony.getId());

            Location locationAlb = new Location(41001, "ES");
            locationService.addOrCreateLocation(locationAlb);
            Beezzer alb = new Beezzer("alb", "alb@unil.ch", PasswordUtilis.hashPassword("Q.-wDw123"), location, Role.ADMIN);
            beezzerService.addBeezzer(alb);

            for (Map.Entry<Long, Beezzer> beezzer : beezzerService.getBeezzers().entrySet()) {
                logger.log(Level.INFO, beezzer.toString());
            }

            Random random = new Random();

            List<Date> dates = new ArrayList<>();

            for (int i = 0; i < 10; i++){
                dates.add(new GregorianCalendar(2024, Calendar.NOVEMBER, 20 + i).getTime());
            }

            List<Symptom> symptoms = new ArrayList<>();
            for (int i = 0; i < 10; i++){
                symptoms.add( new Symptom(ony.getId(), random.nextInt(5), random.nextInt(4)%3 == 0, dates.get(i)));
            }

            Date now = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(now);
            calendar.add(Calendar.HOUR, 2);
            Date plusTwoHour = calendar.getTime();
            Token token = new Token("u7u6m0f9rhvvtml0ibssscagoe", plusTwoHour, 1L, Role.ADMIN);
            tokenService.addToken(token);

            for (int i = 0; i < 10; i++){
                symptomService.addSymptom(symptoms.get(i));
            }

            for (SymptomsDTO symptom : symptomService.getSymptom(ony.getId())) {
                System.out.println(symptom);
                logger.log(Level.INFO, "{0}", symptom);
            }

            logger.log(Level.INFO, "Forecasting pollen information for all locations...");


            for (int i = 0; i < 10; i++){
                pollenLocationIndexService.addPollenLocationIndex( new PollenLocationIndex("Grasses", random.nextInt(5), dates.get(i), location, List.of("recommendation example"), "crossReaction example", "indexDescription example"));
                pollenLocationIndexService.addPollenLocationIndex( new PollenLocationIndex("Mugwort", random.nextInt(5), dates.get(i), location, List.of("recommendation example"), "crossReaction example", "indexDescription example"));
                pollenLocationIndexService.addPollenLocationIndex( new PollenLocationIndex("Oak", random.nextInt(5), dates.get(i), location, List.of("recommendation example"), "crossReaction example", "indexDescription example"));
            }

            List<PollenInfoDTO> pollenShortDTOs = indexPollenForBeezzer.getIndex(ony.getId());
            for (PollenInfoDTO pollen : pollenShortDTOs) {
                logger.log(Level.INFO, pollen.toString());
            }

            for (Map.Entry<Long, PollenLocationIndex> pollenLocationIndex : pollenLocationIndexService.getPollenLocationIndexMap().entrySet()) {
                logger.log(Level.INFO, pollenLocationIndex.toString());
            }

            foreCastService.forecastAllLocation(locationService.getLocations());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error during populate users");
            logger.log(Level.SEVERE, "{0}", e.getStackTrace()[0]);
            logger.log(Level.SEVERE, e.getMessage());
        }
    }
}
