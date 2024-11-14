package ch.unil.doplab.beeaware.service;

import ch.unil.doplab.beeaware.DTO.AllergenDTO;
import ch.unil.doplab.beeaware.DTO.BeezzerDTO;
import ch.unil.doplab.beeaware.DTO.LocationDTO;
import ch.unil.doplab.beeaware.DTO.PollenDTO;
import ch.unil.doplab.beeaware.Domain.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BeezzerService {
    private final Map<Long, Beezzer> beezzers = new HashMap<>();
    private Long idBeezzer = 0L;
    private Logger logger = Logger.getLogger(BeezzerService.class.getName());
    private ObjectMapper objectMapper = new ObjectMapper();
    private LocationService locationService;

    public BeezzerService(LocationService locationService){
        this();
        this.locationService = locationService;
    }

    public void addBeezzer(@NotNull Beezzer beezzer) {
        logger.log(Level.INFO, "Adding Beezzer {0}...", beezzer.getUsername());
        for (Map.Entry<Long, Beezzer> bee : beezzers.entrySet()) {
            if (beezzer.getUsername() != null &&
                    bee.getValue().getUsername() != null &&
                    beezzer.getUsername().equals(bee.getValue().getUsername())) {
                logger.log(Level.WARNING, "Username {0} already used. Please try a new one.", beezzer.getUsername());
                return;
            }
        }
        if (beezzer.getId() == null) {
            Long newId = idBeezzer++;
            beezzers.put(newId, beezzer);
            beezzer.setId(newId);
        } else {
            beezzers.put(beezzer.getId(), beezzer);
        }
        logger.log(Level.INFO, "New Beezzer added : {0}", beezzer.getUsername());
    }

    public Location searchForLocationAndAddIt(Location location){
        Location foundLocation = null;
        for (Map.Entry<Long, Location> loc: locationService.getLocations().entrySet()) {
            if(loc.getValue().getNPA() == location.getNPA() && loc.getValue().getCountry().equals(location.getCountry())){
                logger.log(Level.INFO, "Location {0} already exist", location);
                foundLocation = loc.getValue();
                break;
            }
        }
        if (foundLocation == null) {
            logger.log(Level.INFO, "Create new location : {0}", location);
            foundLocation = locationService.createLocation(location);
        }
        return foundLocation;
    }

    public Beezzer createBeezzerFromJSON(@NotNull String jsonBeezzer) {
        try {
            logger.log(Level.INFO, "Adding Beezzer {0}...", jsonBeezzer);
            Beezzer beezzer = objectMapper.readValue(jsonBeezzer, Beezzer.class);
            beezzer.setPassword(PasswordUtilis.hashPassword(beezzer.getPassword()));

            beezzer.setLocation(searchForLocationAndAddIt(beezzer.getLocation()));
            beezzer.setRole(Role.BEEZZER);

            for (Map.Entry<Long, Pollen> pollen: beezzer.getAllergens().entrySet()) {
                addAllergen(pollen.getValue().getPollenNameEN(), idBeezzer);
            }
            beezzer.setId(null);
            addBeezzer(beezzer);

            return beezzer;
        } catch (Exception e){
            logger.log(Level.WARNING, "Unable to create new beezer");
            logger.log(Level.SEVERE, "{0}", e.getStackTrace());
            return null;
        }
    }

    public BeezzerDTO getBeezzer(Long idBeezzer) {
        logger.log(Level.INFO, "Searching for Beezzer...");
        var beezzer = beezzers.get(idBeezzer);
        if (beezzer == null) {
            logger.log(Level.WARNING, "Beezzer with id {0} doesn't exist.", idBeezzer);
            return null;
        }
        return new BeezzerDTO(beezzer);
    }

    public List<BeezzerDTO> getAllBeezzers() {
        logger.log(Level.INFO, "Searching for all registered Beezzers...");
        List<BeezzerDTO> allBeezzers = new ArrayList<>();
        for (Map.Entry<Long, Beezzer> beezzer : beezzers.entrySet()) {
            allBeezzers.add(new BeezzerDTO(beezzer.getValue()));
        }
        return allBeezzers;
    }

    public void setBeezzer(@NotNull Beezzer beezzer) {
        logger.log(Level.INFO, "Setting Beezzer {0}...", beezzer.getUsername());
        beezzers.put(beezzer.getId(), beezzer);
    }

    public Beezzer getBeezzerIfExist(Long id) throws Exception {
        Beezzer beezzer = beezzers.get(id);
        if (beezzer == null) {
            logger.log(Level.WARNING, "Beezzer with ID {0} doesn't exist.", id);
            throw new Exception("Beezzer does'nt exist");
        }
        return beezzer;
    }

    public boolean isBeezzerExist(Long id){
        Beezzer beezzer = beezzers.get(id);
        if (beezzer == null) {
            logger.log(Level.WARNING, "Beezzer with ID {0} doesn't exist.", id);
            return false;
        }
        return true;
    }


    public boolean removeBeezzer(Long id) {
        logger.log(Level.INFO, "Removing Beezzer...");
        if(!isBeezzerExist(id)){
            return false;
        }
        beezzers.remove(id);
        logger.log(Level.INFO, "Beezzer deleted : {0}", id);
        return true;
    }

    public LocationDTO getBeezzerLocation(Long beezzerId) {
        logger.log(Level.INFO, "Searching location for Beezzer...");
        try {
            return new LocationDTO(getBeezzerIfExist(beezzerId).getLocation());
        } catch (Exception e){
            logger.log(Level.WARNING, "Error getting location for beezzer : \n{0}", e.getStackTrace());
            return new LocationDTO();
        }
    }

    public boolean setBeezzerLocation(Long beezzerId, String jsonLocation) {
        try {
            logger.log(Level.INFO, "Set new Loation for Beezzer... {0}", jsonLocation);
            Beezzer beezzer = getBeezzerIfExist(beezzerId);
            Location location = objectMapper.readValue(jsonLocation, Location.class);
            beezzer.setLocation(searchForLocationAndAddIt(location));
            beezzers.put(beezzer.getId(), beezzer);
            return true;
        } catch (Exception e){
            logger.log(Level.INFO, "Error set location for beezzer : \n{0}", e.getStackTrace());
            return false;
        }
    }

    public void addAllergen(String stringPollen, Long idBeezzer) {
        logger.log( Level.INFO, "Trying to add allergen {0} for Beezzer id {1}...", new Object[]{stringPollen, String.valueOf(idBeezzer)});
        try {
            Pollen pollen = Pollen.getPollenByName(stringPollen);

            Beezzer beezzer = getBeezzerIfExist(idBeezzer);
            if (beezzer.getAllergens().containsKey(pollen.getId())) {
                logger.log(Level.WARNING, "This allergen is already saved to your list.");
                return;
            }
            beezzer.getAllergens().put(pollen.getId(), pollen);
        } catch (Exception e){
            logger.log(Level.WARNING, "Error adding allergen");
        }
    }

    public boolean addAllergenSet(String allergens, Long idBeezzer){
        logger.log(Level.INFO, "Apply allergens set {0}...", allergens);
        try {
            Beezzer beezzer = getBeezzerIfExist(idBeezzer);
            List<Pollen> pollens = objectMapper.readValue(
                    allergens, new TypeReference<List<Pollen>>() {}
            );
            for (Pollen pollen : pollens) {
                addAllergen(pollen.getPollenNameEN(), beezzer.getId());
            }
            return true;
        } catch (Exception e){
            logger.log(Level.WARNING, "Error adding new list of allergens");
            return false;
        }
    }

    public AllergenDTO getBeezzerAllergens(Long idBeezzer) {
        logger.log(Level.INFO, "Searching allergens for Beezzer with id {0}...", idBeezzer);
        try {
            Beezzer beezzer = getBeezzerIfExist(idBeezzer);
            AllergenDTO allergenDTO = new AllergenDTO(beezzer.getAllergens());
            return allergenDTO;
        } catch (Exception e){
            logger.log(Level.WARNING, "Error getting allergen");
            return null;
        }
    }

    public boolean removeAllergen(Long idAllergen, Long idBeezzer) {
        logger.log( Level.INFO, "Removing Allergen...");
        try {
            Beezzer beezzer = getBeezzerIfExist(idBeezzer);
            var allergen = beezzer.getAllergens().get(idAllergen);
            var pollenDTO = new PollenDTO(allergen);
            if (allergen == null) {
                logger.log(Level.WARNING, "Allergen with ID {0} doesn't exist.", idAllergen);
                return false;
            }
            beezzer.getAllergens().remove(idAllergen);
            logger.log(Level.INFO, "Allergen deleted: {0}", pollenDTO);
            return true;
        } catch (Exception e){
            logger.log(Level.WARNING, "Error remove allergen");
            return false;
        }
    }
}
