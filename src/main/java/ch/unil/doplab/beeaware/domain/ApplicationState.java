package ch.unil.doplab.beeaware.domain;

import ch.unil.doplab.beeaware.Domain.DTO.PollenInfoDTO;
import ch.unil.doplab.beeaware.Domain.DTO.SymptomsDTO;
import ch.unil.doplab.beeaware.Domain.*;
import ch.unil.doplab.beeaware.repository.*;
import ch.unil.doplab.beeaware.service.*;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Initialized;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
@Getter
@Setter
public class ApplicationState {
    private static final Logger logger = Logger.getLogger(ApplicationState.class.getName());

    @Inject
    private LocationService locationService;

    @Inject
    private BeezzerService beezzerService;

    @Inject
    private SymptomService symptomService;

    @Inject
    private PollenLocationIndexService pollenLocationIndexService;

    @Inject
    private ForeCastService foreCastService;

    @Inject
    private IndexPollenForBeezzer indexPollenForBeezzer;

    @Inject
    private TokenService tokenService;

    @Inject
    private DailyTaskService dailyTaskService;

    @Inject
    private BeezzerRepository beezzerRepository;

    @Inject
    private LocationRepository locationRepository;

    @Inject
    private PollenLocationIndexRepository pollenLocationIndexRepository;

    @Inject
    private PollenRepository pollenRepository;

    @Inject
    private TokenRepository tokenRepository;

    @PostConstruct
    public void init() {
        try {
            logger.log(Level.INFO, "Initializing application state...");
            populateApplicationState();
            logger.log(Level.INFO, "Application state initialized successfully.");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error during application state initialization", e);
        }
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


            Random random = new Random();

            List<Date> dates = new ArrayList<>();

            for (int i = 0; i < 10; i++){
                dates.add(new GregorianCalendar(2024, Calendar.NOVEMBER, 20 + i).getTime());
            }


            for (int i = 0; i < 10; i++){
                symptomService.addSymptom(new Symptom(ony, random.nextInt(6), random.nextInt(4)%3 == 0, dates.get(i)));
            }

            Date now = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(now);
            calendar.add(Calendar.HOUR, 2);
            Date plusTwoHour = calendar.getTime();
            Token token = new Token("u7u6m0f9rhvvtml0ibssscagoe", plusTwoHour, beezzerRepository.findById(0L), Role.ADMIN);
            tokenService.addToken(token);


            for (SymptomsDTO symptom : symptomService.getSymptom(ony.getId())) {
                System.out.println(symptom);
                logger.log(Level.INFO, "{0}", symptom);
            }

            logger.log(Level.INFO, "Forecasting pollen information for all locations...");


            for (int i = 0; i < 10; i++){
                pollenLocationIndexService.addPollenLocationIndex( new PollenLocationIndex("Grasses", random.nextInt(6), dates.get(i), location, "recommendation example", "crossReaction example", "indexDescription example"));
                pollenLocationIndexService.addPollenLocationIndex( new PollenLocationIndex("Mugwort", random.nextInt(6), dates.get(i), location, "recommendation example", "crossReaction example", "indexDescription example"));
                pollenLocationIndexService.addPollenLocationIndex( new PollenLocationIndex("Oak", random.nextInt(6), dates.get(i), location, "recommendation example", "crossReaction example", "indexDescription example"));
            }


            foreCastService.forecastAllLocation(locationRepository.findAll());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error during populate users");
            logger.log(Level.SEVERE, "{0}", e.getStackTrace()[0]);
            logger.log(Level.SEVERE, e.getMessage());
        }
    }
}
