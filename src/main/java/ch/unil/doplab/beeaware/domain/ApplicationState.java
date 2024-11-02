package ch.unil.doplab.beeaware.domain;

import ch.unil.doplab.beeaware.Domain.*;
import ch.unil.doplab.beeaware.Domain.DTO.PollenInfoDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
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

@ApplicationScoped
public class ApplicationState {

    @Getter
    @Setter
    private List<PollenLocationIndex> PollenLocationIndexArray;
    private String APIKEY = ResourceBundle.getBundle("application").getString("API_KEY");
    private Map<Long, Beezzer> beezzers;
    private Map<Long, Location> locations;
    private Map<Long, Symptom> symptoms;
    private Map<Long, Long> allergens;

    private Long idBeezzer;
    private Long idLocation;
    private Long idPollenIndex;
    private Long idSymptom;

    @PostConstruct
    public void init() {
        PollenLocationIndexArray = new ArrayList<>();
        beezzers = new HashMap<>();
        locations = new HashMap<>();
        symptoms = new HashMap<>();
        // allergens = new HashMap<>();

        idBeezzer = 0L;
        idLocation = 0L;
        idPollenIndex = 0L;
        idSymptom = 0L;

//        populateApplicationState();
    }

    public void addBeezzer(Beezzer beezzer){
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
        } else { beezzers.put(beezzer.getId(), beezzer); }
    }

    public Map<Long, Beezzer> getBeezzers(){
        return beezzers;
    }

    /**
     * Adds a specific pollen allergen to the Beezzer's list of allergens
     * This method checks if the provided pollen is not null and if it is part of
     * the predefined pollens available in the Beezzer's country. If both conditions
     * are met, the pollen is added to the allergens set. If the pollen is null
     * or not available, an IllegalArgumentException is thrown.
     *
     * @param pollen The pollen allergen to be added. It must be a predefined pollen available in the Beezzer's country.
     * @throws IllegalArgumentException If the pollen is null or not available in the Beezzer's country.
     */
    public void addAllergen(Pollen pollen, Beezzer beezzer) {
        if (pollen != null && Pollen.getPredefinedPollens().contains(pollen)) {
            beezzer.setAllergens(pollen.getId(), beezzer.getId());
        } else {
            throw new IllegalArgumentException("This pollen is not available in your country.");
        }
    }
//
//    public void addSymptom(@NotNull Symptom symptom, Beezzer beezzer){
//        // est-ce que ça rends bien la date d'aujourd'hui
//        Date todayDate = new Date();
//        symptom.setDate(todayDate);
//        for (Symptom sym: symptoms) {
//            if (beezzer.getId().equals(sym.getUserId()) && isSameDay(sym.getDate(), todayDate)) {
//                symptom.setId(sym.getId());
//                symptoms.set(symptoms.indexOf(sym), symptom);
//                return;
//            }
//        }
//        symptom.setId(idSymptom++);
//        symptoms.add(symptom);
//    }
//
//    // StudyBuddy return quelque chose à chaque fois!! (Symptom)
//    public void addSymptomForASpecificDate(@NotNull Symptom symptom, Beezzer beezzer, Date date){
//        symptom.setDate(date);
//        for (Symptom sym: symptoms) {
//            if (beezzer.getId().equals(sym.getUserId()) && isSameDay(sym.getDate(), date)) {
//                symptom.setId(sym.getId());
//                symptoms.set(symptoms.indexOf(sym), symptom);
//                return;
//            }
//        }
//        symptom.setId(idSymptom++);
//        symptoms.add(symptom);
//    }
//
//    @NotNull
//    public List<Symptom> getSymptomsForASpecificBeezzer(Beezzer beezzer){
//        List<Symptom> symptomsBeezzer = new ArrayList<>();
//        for (Symptom sym: symptoms) {
//            if (beezzer.getId().equals(sym.getUserId())) {
//                symptomsBeezzer.add(sym);
//            }
//        }
//        return symptomsBeezzer;
//    }
//
//    public List<Symptom> getSymptomsForASpecificDate(Beezzer beezzer, Date date){
//        List<Symptom> symptomsDate = new ArrayList<>();
//        for (Symptom sym: symptoms) {
//            if (beezzer.getId() == sym.getUserId()) {
//                if (beezzer.getId() == sym.getUserId() && isSameDay(sym.getDate(), date)) {
//                    symptomsDate.add(sym);
//                }
//            }
//        }
//        return symptomsDate;
//    }
//
//    public void addPollenIndexLocation(PollenLocationIndex pollenLocationIndex) {
//        for (PollenLocationIndex pil: PollenLocationIndexArray) {
//            if (pil.getLocation() != null && pil.getLocation().getNPA() == pollenLocationIndex.getLocation().getNPA()) {
//                return;
//            }
//        }
//        pollenLocationIndex.setId(idPollenIndex++);
//        PollenLocationIndexArray.add(pollenLocationIndex);
//    }
//
//    public void addLocation(Location location) {
//        for (Location loc: locations) {
//            if (location != null && loc.getNPA() == location.getNPA()) {
//                return;
//            }
//        }
//        location.setId(idLocation++);;
//        locations.add(location);
//    }
//
//    public void forecastAllLocation(){
//        for (Location location : locations) {
//            pollenForecast(location, 1);
//        }
//    }
//
//    public List<PollenInfoDTO> getIndexForSpecificBeezzer(Beezzer beezzer){
//        List<PollenInfoDTO> PollenShortDTOs = new ArrayList<>();
//        for (PollenLocationIndex pollenLocationIndex : PollenLocationIndexArray) {
//            if(pollenLocationIndex.getLocation().getNPA() == beezzer.getLocation().getNPA()){
//
//                for (PollenLocationIndex.DailyInfo dailyInfo : pollenLocationIndex.getDailyInfo()) {
//
//                    for (PollenLocationIndex.PollenTypeInfo pollenTypeDailyInfo : dailyInfo.getPollenTypeInfo()) {
//                        for (Pollen pollen : beezzer.getAllergens()) {
//                            if (pollen.getPollenNameEN().equals(pollenTypeDailyInfo.getDisplayName())) {
//                                if (pollenTypeDailyInfo.getIndexInfo() != null) {
//                                    PollenShortDTOs.add(new PollenInfoDTO(pollenTypeDailyInfo.getDisplayName(), pollenTypeDailyInfo.getIndexInfo().getValue(), pollenTypeDailyInfo.getIndexInfo().getIndexDescription(), ""));
//                                }
//                            }
//                        }
//                    }
//
//                    for (PollenLocationIndex.PlantInfo pollenDailyInfo : dailyInfo.getPlantInfo()) {
//                        for (Pollen pollen : beezzer.getAllergens()) {
//                            if (pollen.getPollenNameEN().equals(pollenDailyInfo.getDisplayName())) {
//                                if (pollenDailyInfo.getIndexInfo() != null) {
//                                    PollenShortDTOs.add(new PollenInfoDTO(pollenDailyInfo.getDisplayName(), pollenDailyInfo.getIndexInfo().getValue(), pollenDailyInfo.getIndexInfo().getIndexDescription(), pollenDailyInfo.getPlantDescription().getCrossReaction()));
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        return PollenShortDTOs;
//    }
//
//    public static boolean isSameDay(@NotNull Date date1, @NotNull Date date2) {
//        LocalDate localDate1 = date1.toInstant()
//                .atZone(ZoneId.systemDefault())
//                .toLocalDate();
//        LocalDate localDate2 = date2.toInstant()
//                .atZone(ZoneId.systemDefault())
//                .toLocalDate();
//        return localDate1.isEqual(localDate2);
//    }
//
//    public Long authenticate(String username, String password) {
//        var bee = beezzers.stream()
//                .filter(beezzer -> beezzer.getUsername().equals(username))
//                .findFirst()
//                .orElse(null);
//        if (bee == null || !checkPassword(password, bee.getPassword())) {
//            return null;
//        }
//        return bee.getId();
//    }
//
//
//    private void populateApplicationState() throws IOException, InterruptedException, ApiException {
//        // Utils.testModeOn(); used in StudyBuddy!!
//
//        Beezzer ony = new Beezzer("Ony", "o@unil.ch", "Q.-wDw124", 1024, "CH");
//        ony.addAllergen(Pollen.getPollenByName("Grasses"));
//        ony.addAllergen(Pollen.getPollenByName("Weed"));
//        var test = addBeezzer(ony);
//
//        Symptom symptom1 = new Symptom(ony.getId(), 8, false);
//        Symptom symptom2 = new Symptom(ony.getId(), 5, false);
//        Symptom symptom3 = new Symptom(ony.getId(), 3, true);
//        addSymptom(symptom1, ony);
//        addSymptom(symptom2, ony);
//
//        Date d1 = new GregorianCalendar(2024, Calendar.FEBRUARY, 11).getTime();
//        Date d2 = new GregorianCalendar(2024, Calendar.FEBRUARY, 21).getTime();
//
//        addSymptomForASpecificDate(symptom1, ony, d1);
//        addSymptomForASpecificDate(symptom3, ony, d2);
//        addSymptom(symptom2, ony);
//
//        for (Symptom symptom:getSymptomsForASpecificBeezzer(ony)) {
//            System.out.println(symptom);
//        };
//
//        forecastAllLocation();
//
//        List<PollenInfoDTO> PollenShortDTOs = getIndexForSpecificBeezzer(ony);
//        for (PollenInfoDTO pollen : PollenShortDTOs) {
//            System.out.println(pollen);
//        }
//
//        // Utils.testModeOff(); used in StudyBuddy!!
//
//    }
//    public void pollenForecast(Location location, int days) {
//        String url = String.format(
//                "https://pollen.googleapis.com/v1/forecast:lookup?key=%s&location.longitude=%s&location.latitude=%s&days=%s",
//                APIKEY, location.getLongitude(), location.getLatitude(), days);
//
//        try {
//            NetHttpTransport httpTransport = new NetHttpTransport();
//            HttpRequestFactory requestFactory = httpTransport.createRequestFactory();
//            HttpRequest request = requestFactory.buildGetRequest(new com.google.api.client.http.GenericUrl(url));
//            HttpResponse response = request.execute();
//
//            String jsonResponse = response.parseAsString();
//            ObjectMapper objectMapper = new ObjectMapper();
//            PollenLocationIndex pollenInfo = objectMapper.readValue(jsonResponse, PollenLocationIndex.class);
//            pollenInfo.setLocation(location);
//            pollenInfo.setId(idPollenIndex++);
//            addPollenIndexLocation(pollenInfo);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
}
