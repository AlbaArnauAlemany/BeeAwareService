package ch.unil.doplab.beeaware.domain;

import ch.unil.doplab.beeaware.Domain.*;
import ch.unil.doplab.beeaware.Domain.DTO.PollenInfoDTO;
import com.google.maps.errors.ApiException;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import static ch.unil.doplab.beeaware.Domain.PasswordUtilis.checkPassword;
import static ch.unil.doplab.beeaware.Domain.PollenLocationIndex.pollenForecast;

@ApplicationScoped
public class ApplicationState {

    private List<PollenLocationIndex> PollenLocationIndexArray;
    @Getter
    @Setter
    // Est-ce que ça devrait être des maps de beezzer et leur id?
    private Set<Beezzer> beezzers;
    private Set<Location> locations;
    private List<Symptom> symptoms;

    private Long idBeezzer;
    private Long idLocation;
    private Long idPollenIndex;
    private Long idSymptom;

    @PostConstruct
    public void init() {
        PollenLocationIndexArray = new ArrayList<>();
        beezzers = new HashSet<>();
        locations = new HashSet<>();
        symptoms = new ArrayList<>();

        idBeezzer = 0L;
        idLocation = 0L;
        idPollenIndex = 0L;
        idSymptom = 0L;

        populateApplicationState();
    }

    public Beezzer addBeezzer(Beezzer beezzer){
        for (Beezzer bee: beezzers) {
            if (beezzer.getUsername() != null && bee.getUsername() != null && beezzer.getUsername().equals(bee.getUsername())) {
                throw new IllegalArgumentException("Username " + beezzer.getUsername() + " already used. Please try a new one.");
            }
        }
        beezzer.setId(idBeezzer++);
        beezzers.add(beezzer);
        return beezzer;
    }

    public void addSymptom(@NotNull Symptom symptom, Beezzer beezzer){
        // est-ce que ça rends bien la date d'aujourd'hui
        Date todayDate = new Date();
        symptom.setDate(todayDate);
        for (Symptom sym: symptoms) {
            if (beezzer.getId().equals(sym.getUserId()) && isSameDay(sym.getDate(), todayDate)) {
                symptom.setId(sym.getId());
                symptoms.set(symptoms.indexOf(sym), symptom);
                return;
            }
        }
        symptom.setId(idSymptom++);
        symptoms.add(symptom);
    }

    // StudyBuddy return quelque chose à chaque fois!! (Symptom)
    public void addSymptomForASpecificDate(@NotNull Symptom symptom, Beezzer beezzer, Date date){
        symptom.setDate(date);
        for (Symptom sym: symptoms) {
            if (beezzer.getId().equals(sym.getUserId()) && isSameDay(sym.getDate(), date)) {
                symptom.setId(sym.getId());
                symptoms.set(symptoms.indexOf(sym), symptom);
                return;
            }
        }
        symptom.setId(idSymptom++);
        symptoms.add(symptom);
    }

    @NotNull
    public List<Symptom> getSymptomsForASpecificBeezzer(Beezzer beezzer){
        List<Symptom> symptomsBeezzer = new ArrayList<>();
        for (Symptom sym: symptoms) {
            if (beezzer.getId().equals(sym.getUserId())) {
                symptomsBeezzer.add(sym);
            }
        }
        return symptomsBeezzer;
    }

    public List<Symptom> getSymptomsForASpecificDate(Beezzer beezzer, Date date){
        List<Symptom> symptomsDate = new ArrayList<>();
        for (Symptom sym: symptoms) {
            if (beezzer.getId() == sym.getUserId()) {
                if (beezzer.getId() == sym.getUserId() && isSameDay(sym.getDate(), date)) {
                    symptomsDate.add(sym);
                }
            }
        }
        return symptomsDate;
    }

    public void addPollenIndexLocation(PollenLocationIndex pollenLocationIndex) {
        for (PollenLocationIndex pil: PollenLocationIndexArray) {
            if (pil.getLocation() != null && pil.getLocation().getNPA() == pollenLocationIndex.getLocation().getNPA()) {
                return;
            }
        }
        pollenLocationIndex.setId(idPollenIndex++);
        PollenLocationIndexArray.add(pollenLocationIndex);
    }

    public void addLocation(Location location) {
        for (Location loc: locations) {
            if (location != null && loc.getNPA() == location.getNPA()) {
                return;
            }
        }
        location.setId(idLocation++);;
        locations.add(location);
    }

    public void forecastAllLocation(){
        for (Location location : locations) {
            pollenForecast(location, 1);
        }
    }

    public List<PollenInfoDTO> getIndexForSpecificBeezzer(Beezzer beezzer){
        List<PollenInfoDTO> PollenShortDTOs = new ArrayList<>();
        for (PollenLocationIndex pollenLocationIndex : PollenLocationIndexArray) {
            if(pollenLocationIndex.getLocation().getNPA() == beezzer.getLocation().getNPA()){

                for (PollenLocationIndex.DailyInfo dailyInfo : pollenLocationIndex.getDailyInfo()) {

                    for (PollenLocationIndex.PollenTypeInfo pollenTypeDailyInfo : dailyInfo.getPollenTypeInfo()) {
                        for (Pollen pollen : beezzer.getAllergens()) {
                            if (pollen.getPollenNameEN().equals(pollenTypeDailyInfo.getDisplayName())) {
                                if (pollenTypeDailyInfo.getIndexInfo() != null) {
                                    PollenShortDTOs.add(new PollenInfoDTO(pollenTypeDailyInfo.getDisplayName(), pollenTypeDailyInfo.getIndexInfo().getValue(), pollenTypeDailyInfo.getIndexInfo().getIndexDescription(), ""));
                                }
                            }
                        }
                    }

                    for (PollenLocationIndex.PlantInfo pollenDailyInfo : dailyInfo.getPlantInfo()) {
                        for (Pollen pollen : beezzer.getAllergens()) {
                            if (pollen.getPollenNameEN().equals(pollenDailyInfo.getDisplayName())) {
                                if (pollenDailyInfo.getIndexInfo() != null) {
                                    PollenShortDTOs.add(new PollenInfoDTO(pollenDailyInfo.getDisplayName(), pollenDailyInfo.getIndexInfo().getValue(), pollenDailyInfo.getIndexInfo().getIndexDescription(), pollenDailyInfo.getPlantDescription().getCrossReaction()));
                                }
                            }
                        }
                    }
                }
            }
        }
        return PollenShortDTOs;
    }

    public static boolean isSameDay(@NotNull Date date1, @NotNull Date date2) {
        LocalDate localDate1 = date1.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        LocalDate localDate2 = date2.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        return localDate1.isEqual(localDate2);
    }

    public Long authenticate(String username, String password) {
        var bee = beezzers.stream()
                .filter(beezzer -> beezzer.getUsername().equals(username))
                .findFirst()
                .orElse(null);
        if (bee == null || !checkPassword(password, bee.getPassword())) {
            return null;
        }
        return bee.getId();
    }

    private void populateApplicationState() throws IOException, InterruptedException, ApiException {
        // Utils.testModeOn(); used in StudyBuddy!!

        Beezzer ony = new Beezzer("Ony", "o@unil.ch", "Q.-wDw124", 1024, "CH");
        ony.addAllergen(Pollen.getPollenByName("Grasses"));
        ony.addAllergen(Pollen.getPollenByName("Weed"));
        var test = addBeezzer(ony);

        Symptom symptom1 = new Symptom(ony.getId(), 8, false);
        Symptom symptom2 = new Symptom(ony.getId(), 5, false);
        Symptom symptom3 = new Symptom(ony.getId(), 3, true);
        addSymptom(symptom1, ony);
        addSymptom(symptom2, ony);

        Date d1 = new GregorianCalendar(2024, Calendar.FEBRUARY, 11).getTime();
        Date d2 = new GregorianCalendar(2024, Calendar.FEBRUARY, 21).getTime();

        addSymptomForASpecificDate(symptom1, ony, d1);
        addSymptomForASpecificDate(symptom3, ony, d2);
        addSymptom(symptom2, ony);

        for (Symptom symptom:getSymptomsForASpecificBeezzer(ony)) {
            System.out.println(symptom);
        };

        forecastAllLocation();

        List<PollenInfoDTO> PollenShortDTOs = getIndexForSpecificBeezzer(ony);
        for (PollenInfoDTO pollen : PollenShortDTOs) {
            System.out.println(pollen);
        }

        // Utils.testModeOff(); used in StudyBuddy!!
    }
}
