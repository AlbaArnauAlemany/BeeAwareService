package ch.unil.doplab.beeaware.service;

import ch.unil.doplab.beeaware.DTO.AllergenDTO;
import ch.unil.doplab.beeaware.DTO.BeezzerDTO;
import ch.unil.doplab.beeaware.DTO.LocationDTO;
import ch.unil.doplab.beeaware.DTO.PollenDTO;
import ch.unil.doplab.beeaware.Domain.Beezzer;
import ch.unil.doplab.beeaware.Domain.Pollen;
import lombok.Getter;
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
public class BeezzerService {
    private final Map<Long, Beezzer> beezzers = new HashMap<>();
    private Long idBeezzer = 0L;
    private Logger logger = Logger.getLogger(BeezzerService.class.getName());

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

    public BeezzerDTO getBeezzer(Long idBeezzer) {
        var beezzer = beezzers.get(idBeezzer);
        logger.log(Level.INFO, "Searching for Beezzer...");
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

    public boolean removeBeezzer(Long id) {
        var beezzer = beezzers.get(id);
        logger.log(Level.INFO, "Removing Beezzer...");
        if (beezzer == null) {
            logger.log(Level.WARNING, "Beezzer with ID {0} doesn't exist.", id);
            return false;

        }
        beezzers.remove(id);
        logger.log(Level.INFO, "Beezzer deleted : {0}", id);
        return true;
    }

    public LocationDTO getBeezzerLocation(Long beezzerId) {
        var beezzer = beezzers.get(beezzerId);
        logger.log(Level.INFO, "Searching location for Beezzer...");
        if (beezzer == null) {
            logger.log(Level.WARNING, "Beezzer with id {0} doesn't exist.", beezzerId);
            return null;
        }
        return new LocationDTO(beezzer.getLocation());
    }

    public void addAllergen(String stringPollen, Long idBeezzer) {
        logger.log( Level.INFO, "Trying to add allergen {0} for Beezzer id {1}...", new Object[]{stringPollen, String.valueOf(idBeezzer)});
        var pollen = Pollen.getPollenByName(stringPollen);
        var beezzer = beezzers.get(idBeezzer);
        if (beezzer == null) {
            logger.log(Level.WARNING, "Beezzer with id {0} doesn't exist.", idBeezzer);
            return;
        }
        if (beezzer.getAllergens().containsKey(pollen.getId())) {
            logger.log(Level.WARNING, "This allergen is already saved to your list.");
            return;
        }
        beezzer.getAllergens().put(pollen.getId(), pollen);
    }

    public AllergenDTO getBeezzerAllergens(Long idBeezzer) {
        var beezzer = beezzers.get(idBeezzer);
        logger.log( Level.INFO, "Searching allergens for Beezzer with id {0}...", idBeezzer);
        if (beezzer == null) {
            logger.log(Level.WARNING, "Beezzer with id {0} doesn't exist.", idBeezzer);
            return null;
        }
        AllergenDTO allergenDTO = new AllergenDTO(beezzer.getAllergens());
        return allergenDTO;
    }

    public boolean removeAllergen(Long idAllergen, Long idBeezzer) {
        var beezzer = beezzers.get(idBeezzer);
        logger.log( Level.INFO, "Removing Allergen...");
        if (beezzer == null) {
            logger.log(Level.WARNING, "Beezzer with id {0} doesn't exist.", idBeezzer);
            return false;
        }
        var allergen = beezzer.getAllergens().get(idAllergen);
        var pollenDTO = new PollenDTO(allergen);
        if (allergen == null) {
            logger.log(Level.WARNING, "Allergen with ID {0} doesn't exist.", idAllergen);
            return false;

        }
        beezzer.getAllergens().remove(idAllergen);
        logger.log(Level.INFO, "Allergen deleted: {0}", pollenDTO);
        return true;
    }
}
