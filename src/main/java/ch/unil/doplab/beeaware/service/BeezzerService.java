package ch.unil.doplab.beeaware.service;

import ch.unil.doplab.beeaware.DTO.AllergenDTO;
import ch.unil.doplab.beeaware.DTO.BeezzerDTO;
import ch.unil.doplab.beeaware.DTO.LocationDTO;
import ch.unil.doplab.beeaware.DTO.PollenDTO;
import ch.unil.doplab.beeaware.Domain.*;
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
    private SymptomService symptomService;

    /**
     * Constructs a new instance of BeezzerService with the provided LocationService.
     *
     * @param locationService The LocationService to be used for location-related operations.
     */
    public BeezzerService(LocationService locationService, SymptomService symptomService){
        this();
        this.locationService = locationService;
        this.symptomService = symptomService;

    }

    /**
     * Adds a new Beezzer to the system if the username is unique.
     *
     * @param beezzer The Beezzer object to be added. Must not be null.
     */
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

    /**
     * Searches for a location in the existing locations. If the location
     * is found, it returns the found location. If not, it creates and adds
     * the new location.
     *
     * @param location The location to search for and add if not found. (The location should not be null.)
     * @return The found or newly created location.
     */
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
            foundLocation = locationService.addOrCreateLocation(location);
        }
        return foundLocation;
    }

    /**
     * Creates a new Beezzer from a JSON string. This method processes the provided JSON,
     * sets the username and email, hashes the password, sets the location and role,
     * adds allergens, and finally adds the Beezzer to the system and adds a new ID.
     *
     * @param jsonBeezzer The JSON string containing the Beezzer details (must not be null).
     * @return The created Beezzer object, or null if an error occurs during the creation process.
     */
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
            logger.log(Level.WARNING, "Unable to create new beezzer");
            logger.log(Level.SEVERE, "{0}", e.getStackTrace());
            return null;
        }
    }

    /**
     * Retrieves a BeezzerDTO (an object with the beezzer's main information
     * correctly displayed) by its unique identifier.
     *
     * @param idBeezzer The unique identifier of the Beezzer to retrieve.
     * @return The BeezzerDTO corresponding to the specified idBeezzer, or throws a RuntimeException if the Beezzer does not exist.
     */
    public BeezzerDTO getBeezzer(Long idBeezzer) {
        logger.log(Level.INFO, "Searching for Beezzer...");
        try {
            return new BeezzerDTO(getBeezzerIfExist(idBeezzer));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Retrieves a list of all BeezzerDTO objects (each beezzer's main information
     * correctly displayed) representing all registered Beezzers.
     *
     * @return A list of BeezzerDTO objects, each containing the main information of a Beezzer.
     */
    public List<BeezzerDTO> getAllBeezzers() {
        logger.log(Level.INFO, "Searching for all registered Beezzers...");
        List<BeezzerDTO> allBeezzers = new ArrayList<>();
        for (Map.Entry<Long, Beezzer> beezzer : beezzers.entrySet()) {
            allBeezzers.add(new BeezzerDTO(beezzer.getValue()));
        }
        return allBeezzers;
    }

    /**
     * Updates the provided Beezzer to the system.
     *
     * @param beezzer The Beezzer object to be set (must not be null).
     */
    public void setBeezzer(@NotNull Beezzer beezzer) {
        logger.log(Level.INFO, "Setting Beezzer {0}...", beezzer.getUsername());
        beezzers.put(beezzer.getId(), beezzer);
    }

    /**
     * Retrieves a Beezzer by its unique identifier if it exists.
     *
     * @param id The unique identifier of the Beezzer to retrieve.
     * @return The Beezzer object corresponding to the specified ID.
     * @throws Exception If no Beezzer exists with the specified ID.
     */
    public Beezzer getBeezzerIfExist(Long id) throws Exception {
        Beezzer beezzer = beezzers.get(id);
        if (beezzer == null) {
            logger.log(Level.WARNING, "Beezzer with ID {0} doesn't exist.", id);
            throw new Exception("Beezzer doesn't exist");
        }
        return beezzer;
    }

    /**
     * Checks if a Beezzer exists in the system based on its unique identifier.
     *
     * @param id The unique identifier of the Beezzer to check.
     * @return True if the Beezzer exists, false otherwise.
     */
    public boolean beezzerExist(Long id){
        Beezzer beezzer = beezzers.get(id);
        if (beezzer == null) {
            logger.log(Level.WARNING, "Beezzer with ID {0} doesn't exist.", id);
            return false;
        }
        return true;
    }


    /**
     * Removes a Beezzer from the system based on the provided beezzer's unique identifier.
     * Removing a Beezzer also removes all its registered symptoms.
     *
     * @param id The unique identifier of the Beezzer to be removed.
     * @return true if the Beezzer was successfully removed, false otherwise.
     */
    public boolean removeBeezzer(Long id) {
        logger.log(Level.INFO, "Removing Beezzer...");
        if(!beezzerExist(id)){
            return false;
        }
        symptomService.removeSymptomsForBeezzer(id);
        beezzers.remove(id);
        logger.log(Level.INFO, "Beezzer deleted : {0}", id);
        return true;
    }

    /**
     * Retrieves the location information of a Beezzer given its unique identifier.
     *
     * @param beezzerId The unique identifier of the Beezzer whose location needs to be retrieved.
     * @return The LocationDTO object containing the location details of the specified Beezzer, or null if an error occurs.
     */
    public LocationDTO getBeezzerLocation(Long beezzerId) {
        logger.log(Level.INFO, "Searching location for Beezzer...");
        try {
            return new LocationDTO(getBeezzerIfExist(beezzerId).getLocation());
        } catch (Exception e){
            logger.log(Level.WARNING, "Error getting location for beezzer : \n{0}", e.getStackTrace());
            return null;
        }
    }

    /**
     * Updates the location of a given Beezzer based on a JSON string with the location details.
     *
     * @param beezzerId The unique identifier of the Beezzer whose location needs to be updated.
     * @param jsonLocation A JSON string with the location details.
     * @return true if the location was successfully added, false otherwise.
     */
    public boolean setBeezzerLocation(Long beezzerId, String jsonLocation) {
        try {
            logger.log(Level.INFO, "Set new Location for Beezzer... {0}", jsonLocation);
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

    /**
     * Adds a specified allergen to a Beezzer's allergy list.
     *
     * @param stringPollen The Json String of the name of the pollen to be added as an allergen.
     * @param idBeezzer The unique identifier of the Beezzer to whom the allergen is being added.
     */

    public void addAllergen(@NotNull String stringPollen, Long idBeezzer) {
        logger.log( Level.INFO, "Trying to add allergen {0} for Beezzer id {1}...", new Object[]{stringPollen, String.valueOf(idBeezzer)});
        try {
            Pollen pollen = Pollen.getPollenByName(stringPollen);
            Beezzer beezzer = getBeezzerIfExist(idBeezzer);
            if (beezzer.getAllergens().containsKey(pollen.getId())) {
                logger.log(Level.WARNING, "This allergen is already saved to your list.");
                return;
            }
            beezzer.getAllergens().put(pollen.getId(), pollen);
            logger.log( Level.INFO, "Allergen {0} for Beezzer {1} correctly added.", new Object[]{pollen.getPollenNameEN(), beezzer.getUsername()});

        } catch (Exception e){
            logger.log(Level.WARNING, "Error adding allergen");
        }
    }

    /**
     * Adds a set of allergens to a Beezzer's allergy list.
     *
     * @param allergens A JSON string representing a list of Pollen objects to be added as allergens.
     * @param idBeezzer The unique identifier of the Beezzer to whom the allergens are being added.
     * @return true if the allergens were successfully added, false otherwise.
     */
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

    /**
     * Retrieves the list of allergens associated with the specified Beezzer.
     *
     * @param idBeezzer The unique identifier of the Beezzer whose allergens are to be retrieved.
     * @return The AllergenDTO object containing the list of allergens associated with the specified Beezzer, or null if an error occurs.
     */
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

    /**
     * Removes an allergen from the specified Beezzer's list of allergens.
     *
     * @param stringPollen The Json String of the name of the pollen to be removed.
     * @param idBeezzer The unique identifier of the Beezzer from whom the allergen will be removed.
     * @return True if the allergen was successfully removed, false if the allergen does not exist or an error occurs.
     */
    public boolean removeAllergen(String stringPollen, Long idBeezzer) {
        logger.log( Level.INFO, "Removing Allergen...");
        try {
            Beezzer beezzer = getBeezzerIfExist(idBeezzer);
            Pollen pollen = Pollen.getPollenByName(stringPollen);
            if (pollen == null) {
                logger.log(Level.WARNING, "This allergen doesn't exist.");
                return false;
            }
            if (beezzer.getAllergens().containsKey(pollen.getId())) {
                beezzer.getAllergens().remove(pollen.getId());
                var pollenDTO = new PollenDTO(pollen);
                logger.log(Level.INFO, "Allergen deleted: {0}", pollenDTO);
                return true;
            }
            logger.log(Level.WARNING, "Error remove allergen");
            return false;
        } catch (Exception e){
            logger.log( Level.INFO, "Error remove allergen {0}\n{1}\n", new Object[]{e.getMessage(), e.getStackTrace()});
            return false;
        }
    }
}
