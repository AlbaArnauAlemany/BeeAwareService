package ch.unil.doplab.beeaware.domain;

import ch.unil.doplab.beeaware.Domain.DTO.SymptomsDTO;
import ch.unil.doplab.beeaware.Utilis.FakeGenerator;
import ch.unil.doplab.beeaware.Domain.*;
import ch.unil.doplab.beeaware.repository.*;
import ch.unil.doplab.beeaware.service.*;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static ch.unil.doplab.beeaware.Domain.Pollen.getPredefinedPollens;

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

    private FakeGenerator fakeGenerator = new FakeGenerator();

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
            if(beezzerService.getAllBeezzers().size() < 10) {
                logger.log(Level.SEVERE, "Populating application");
                int populate = 1100;
                int offsetDays = 17;
                Random random = new Random();

                for (Pollen pollen : getPredefinedPollens()) {
                    pollenRepository.addPollen(pollen);
                }
                List<Integer> npas = new ArrayList<>(Arrays.asList(41001, 11005, 18010, 29015, 14002, 21002));

                // Creating Locations
                for (int npa : npas) {
                    locationService.addOrCreateLocation(new Location(npa, "ES"));
                }

                // Creating manually our admin beezzers
                List<Location> allLocations = locationService.getAllRegisteredLocationsL();

                Beezzer ony = new Beezzer("Ony", "o@unil.ch", PasswordUtilis.hashPassword("Q.-wDw124"), allLocations.get(random.nextInt(allLocations.size())), Role.BEEZZER);
                beezzerService.addBeezzer(ony);
                ony = beezzerService.getBeezzerByUsername(ony.getUsername());
                System.out.println(ony);

                List<Date> dates = new ArrayList<>();
                Calendar calendarTemp = Calendar.getInstance();
                calendarTemp.add(Calendar.DATE, -offsetDays); // Définir la date de départ (offset dans le passé)

                for (int i = 0; i <= offsetDays; i++) {
                    dates.add(calendarTemp.getTime()); // Ajouter la date actuelle à la liste
                    calendarTemp.add(Calendar.DATE, 1); // Incrémenter d'un jour
                }

                for (int i = 0; i < offsetDays; i++) {
                    symptomService.addSymptom(new Symptom(ony.getId(), random.nextInt(6), random.nextInt(4) % 3 == 0, dates.get(i)));
                }

                beezzerService.addAllergen("Grasses", ony.getId());
                beezzerService.addAllergen("Mugwort", ony.getId());

                Beezzer alb = new Beezzer("alb", "alb@unil.ch", PasswordUtilis.hashPassword("Q.-wDw123"), allLocations.get(random.nextInt(allLocations.size())), Role.ADMIN);
                beezzerService.addBeezzer(alb);

                // Pollen names to go through
                List<String> pollenNames = Arrays.asList(
                        "Hazel",
                        "Alder",
                        "Ash",
                        "Birch",
                        "Cottonwood",
                        "Oak",
                        "Olive",
                        "Pine",
                        "Grasses",
                        "Ragweed",
                        "Mugwort",
                        "Weed"
                );


                // Creating 1000 Beezzers
                for (int i = 0; i < populate; i++) {
                    Calendar calendarSymptom = Calendar.getInstance();
                    calendarSymptom.setTime(new java.util.Date());
                    String username = fakeGenerator.generateUsername();
                    Beezzer randomBeezzer = new Beezzer(
                            username,
                            fakeGenerator.generateEmail(username),
                            PasswordUtilis.hashPassword(fakeGenerator.generateValidPassword()),
                            allLocations.get(random.nextInt(allLocations.size())),
                            Role.BEEZZER);
                    beezzerService.addBeezzer(randomBeezzer);
                    for (int b = 0; b < 3; b++) {
                        for (int dayOffset = 1; dayOffset <= offsetDays; dayOffset++) {
                            calendarSymptom.add(Calendar.DATE, -1);
                            java.util.Date targetDate = calendarSymptom.getTime();
                            Symptom randomSymptom = new Symptom(
                                    i + 3L,
                                    random.nextInt(6),
                                    random.nextInt(4) % 3 == 0,
                                    targetDate);
                            symptomService.addSymptom(randomSymptom);
                        }
                    }
                    for (int k = 0; k < 2; k++) {
                        beezzerService.addAllergen(pollenNames.get(random.nextInt(pollenNames.size())), i + 3L);
                    }
                }

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date());
                calendar.add(Calendar.HOUR, 2);
                Date plusTwoHour = calendar.getTime();
                Token token = new Token("u7u6m0f9rhvvtml0ibssscagoe", plusTwoHour, 1L, Role.ADMIN);
                tokenService.addToken(token);


                for (SymptomsDTO symptom : symptomService.getSymptom(ony.getId())) {
                    System.out.println(symptom);
                    logger.log(Level.INFO, "{0}", symptom);
                }

                logger.log(Level.INFO, "Forecasting pollen information for all locations...");


                // Populate the PollenLocationIndex
                for (Location location : allLocations) {
                    for (String pollenName : pollenNames) {
                        Calendar calendarNow = Calendar.getInstance();
                        calendarNow.setTime(new java.util.Date());
                        for (int dayOffset = 1; dayOffset <= offsetDays; dayOffset++) {
                            calendarNow.add(Calendar.DATE, -1);
                            java.util.Date targetDate = calendarNow.getTime();

                            PollenLocationIndex randomPollenIndex = new PollenLocationIndex(
                                    pollenName,
                                    random.nextInt(6),
                                    targetDate,
                                    location,
                                    List.of(fakeGenerator.generateRecommendations()),
                                    fakeGenerator.generateRecommendations(),
                                    fakeGenerator.generateRecommendations());

                            if(!pollenLocationIndexService.existsByPollenNameAndLocationAndDate(pollenName, location, targetDate)) {
                                pollenLocationIndexService.addPollenLocationIndex(randomPollenIndex);
                            }
                        }
                    }
                }

                // Forecast All Locations
                if (allLocations != null && !allLocations.isEmpty()) {
                    foreCastService.forecastAllLocation(allLocations);
                } else {
                    logger.log(Level.WARNING, "Locations array is empty...");
                }
            }
            if(pollenLocationIndexService.countPollenToday() == 0) {
                List<Location> allLocations = locationService.getAllRegisteredLocationsL();
                // Forecast All Locations
                if (allLocations != null && !allLocations.isEmpty()) {
                    foreCastService.forecastAllLocation(allLocations);
                } else {
                    logger.log(Level.WARNING, "Locations array is empty...");
                }
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error during populate users");
            logger.log(Level.SEVERE, "{0}", e.getStackTrace()[0]);
            logger.log(Level.SEVERE, e.getMessage());
        }
    }
}
