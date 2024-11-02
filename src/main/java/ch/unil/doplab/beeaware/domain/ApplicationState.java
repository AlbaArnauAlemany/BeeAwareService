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
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
@Getter
@Setter
public class ApplicationState {

    private List<PollenLocationIndex> PollenLocationIndexArray;
    private String APIKEY = ResourceBundle.getBundle("application").getString("API_KEY");
    @Getter
    private Map<Long, Beezzer> beezzers;
    private Map<Long, Location> locations;
    private Map<Long, Symptom> symptoms;
    private Map<Long, Long> allergens;

    private Long idBeezzer;
    private Long idLocation;
    private Long idPollenIndex;
    private Long idSymptom;

    private Logger logger = Logger.getLogger(ApplicationState.class.getName());

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

        populateApplicationState();
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

    /**
     * Adds a specific pollen allergen to the Beezzer's list of allergens
     * This method checks if the provided pollen and beezzer are not null and if it is part of
     * the predefined pollens available in the Beezzer's country. If both conditions
     * are met, the pollen id and the Beezzer id are added to the allergens set. If the pollen is null
     * or not available, an IllegalArgumentException is thrown.
     *
     * @param pollen The pollen allergen to be added. It must be a predefined pollen available in the Beezzer's country.
     * @param beezzer The beezzer initiator of this operation
     * @throws IllegalArgumentException If the pollen is null or not available in the Beezzer's country.
     */
    public void addAllergen(@NonNull Pollen pollen, @NonNull Beezzer beezzer) {
        if (!Pollen.getPredefinedPollens().contains(pollen)) {
            throw new IllegalArgumentException("This pollen is not available in your country.");
        }
        if (beezzer.getAllergens().containsKey(pollen.getId())) {
            throw new IllegalArgumentException("This allergen is already saved to your list.");
        }
        beezzer.getAllergens().put(pollen.getId(), pollen);
    }

    // Alba: Est-ce que le Beezzer est nécessaire alors que pour construire un Symptom if
    // faut de toute façon un beezzer id?
    public void addSymptom(@NotNull Symptom symptom, @NonNull Beezzer beezzer){
        Date todayDate = new Date();
        symptom.setDate(todayDate);
        for (Map.Entry<Long, Symptom> sym: symptoms.entrySet()) {
            if (beezzer.getId().equals(sym.getValue().getBeezzerId()) &&
                    isSameDay(sym.getValue().getDate(), todayDate)) {
                symptom.setId(sym.getValue().getId());
                // Alba: Est-ce que ça overwrite le symptom sur le symptom avec le même id dans notre liste de symptoms?
                symptoms.put(sym.getValue().getId(), symptom);
                return;
            }
        }
        symptom.setId(idSymptom++);
        symptoms.put(symptom.getBeezzerId(), symptom);
    }

    // Alba: On peut overwrite les méthodes, pourquoi pas overwrite addsymptoms quand une date est  passée?
    public void addSymptomForASpecificDate(@NotNull Symptom symptom, @NotNull Beezzer beezzer, @NotNull Date date){
        symptom.setDate(date);
        for (Map.Entry<Long, Symptom> sym: symptoms.entrySet()) {
            if (beezzer.getId().equals(sym.getValue().getBeezzerId()) && isSameDay(sym.getValue().getDate(), date)) {
                symptom.setId(sym.getValue().getId());
                symptoms.put(sym.getValue().getId(), symptom);
                return;
            }
        }
        symptom.setId(idSymptom++);
        symptoms.put(symptom.getBeezzerId(), symptom);
    }


    public List<Symptom> getSymptomsForASpecificBeezzer(@NotNull Beezzer beezzer) {
        List<Symptom> symptomsBeezzer = new ArrayList<>();
        for (Map.Entry<Long, Symptom> sym: symptoms.entrySet()) {
            if (beezzer.getId().equals(sym.getValue().getBeezzerId())) {
                symptomsBeezzer.add(sym.getValue());
            }
        }
        return symptomsBeezzer;
    }

    public List<Symptom> getSymptomsForASpecificDate(@NotNull Beezzer beezzer, @NotNull Date date){
        List<Symptom> symptomsDate = new ArrayList<>();
        for (Map.Entry<Long, Symptom> sym: symptoms.entrySet()) {
            if (beezzer.getId().equals(sym.getValue().getBeezzerId())
                    && isSameDay(sym.getValue().getDate(), date)) {
                symptomsDate.add(sym.getValue());
            }
        }
        return symptomsDate;
    }

    public void addPollenIndexLocation(@NotNull PollenLocationIndex pollenLocationIndex) {
        for (PollenLocationIndex pil: PollenLocationIndexArray) {
            if (pil.getLocation() != null && pil.getLocation().getNPA() == pollenLocationIndex.getLocation().getNPA() &&
                    pil.getLocation().getCountry() == pollenLocationIndex.getLocation().getCountry()) {
                return;
            }
        }
        pollenLocationIndex.setId(idPollenIndex++);
        PollenLocationIndexArray.add(pollenLocationIndex);
    }

    // Alba: le NPA ET le Country doivent être pareil dans le if car 2 pays
    // peuvent avoir le même NPA?
    public void addLocation(@NotNull Location location) {
        for (Map.Entry<Long, Location> loc: locations.entrySet()) {
            if (loc.getValue().getNPA() == location.getNPA() &&
                    loc.getValue().getCountry() == location.getCountry()) {
                return;
            }
        }
        location.setId(idLocation++);;
        locations.put(idLocation, location);
    }

    public void forecastAllLocation(){
        for (Map.Entry<Long, Location> loc: locations.entrySet()) {
            pollenForecast(loc.getValue(), 1);
        }
    }

    public List<PollenInfoDTO> getIndexForSpecificBeezzer(@NotNull Beezzer beezzer){
        List<PollenInfoDTO> PollenShortDTOs = new ArrayList<>();
        for (PollenLocationIndex pollenLocationIndex : PollenLocationIndexArray) {
            if(pollenLocationIndex.getLocation().getNPA() == beezzer.getLocation().getNPA() &&
                    pollenLocationIndex.getLocation().getCountry() == beezzer.getLocation().getCountry()){

                for (PollenLocationIndex.DailyInfo dailyInfo : pollenLocationIndex.getDailyInfo()) {

                    for (PollenLocationIndex.PollenTypeInfo pollenTypeDailyInfo : dailyInfo.getPollenTypeInfo()) {
                        for (Map.Entry<Long, Pollen> pollen : beezzer.getAllergens().entrySet()) {
                            if (pollen.getValue().getPollenNameEN().equals(pollenTypeDailyInfo.getDisplayName())) {
                                if (pollenTypeDailyInfo.getIndexInfo() != null) {
                                    PollenShortDTOs.add(new PollenInfoDTO(pollenTypeDailyInfo.getDisplayName(), pollenTypeDailyInfo.getIndexInfo().getValue(), pollenTypeDailyInfo.getIndexInfo().getIndexDescription(), ""));
                                }
                            }
                        }
                    }

                    for (PollenLocationIndex.PlantInfo pollenDailyInfo : dailyInfo.getPlantInfo()) {
                        for (Map.Entry<Long, Pollen> pollen : beezzer.getAllergens().entrySet()) {
                            if (pollen.getValue().getPollenNameEN().equals(pollenDailyInfo.getDisplayName())) {
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

    public void pollenForecast(Location location, int days) {
        String url = String.format(
                "https://pollen.googleapis.com/v1/forecast:lookup?key=%s&location.longitude=%s&location.latitude=%s&days=%s",
                APIKEY, location.getLongitude(), location.getLatitude(), days);

        try {
            NetHttpTransport httpTransport = new NetHttpTransport();
            HttpRequestFactory requestFactory = httpTransport.createRequestFactory();
            HttpRequest request = requestFactory.buildGetRequest(new com.google.api.client.http.GenericUrl(url));
            HttpResponse response = request.execute();

            String jsonResponse = response.parseAsString();
            ObjectMapper objectMapper = new ObjectMapper();
            PollenLocationIndex pollenInfo = objectMapper.readValue(jsonResponse, PollenLocationIndex.class);
            pollenInfo.setLocation(location);
            pollenInfo.setId(idPollenIndex++);
            addPollenIndexLocation(pollenInfo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void populateApplicationState() {
        // Alba: Utils.testModeOn(); used in StudyBuddy!!

        try {
            Beezzer ony = new Beezzer("Ony", "o@unil.ch", "Q.-wDw124", 1024, "CH");
            addAllergen(Pollen.getPollenByName("Grasses"), ony);
            addAllergen(Pollen.getPollenByName("Weed"), ony);

            addBeezzer(ony);
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
        } catch (Exception e){
            logger.log( Level.SEVERE, "Error during populate users");
            logger.log( Level.SEVERE, e.getMessage());
        }
    }

}
