package ch.unil.doplab.beeaware.service;

import ch.unil.doplab.beeaware.DTO.AllergenDTO;
import ch.unil.doplab.beeaware.DTO.BeezzerDTO;
import ch.unil.doplab.beeaware.DTO.LocationDTO;
import ch.unil.doplab.beeaware.DTO.PollenDTO;
import ch.unil.doplab.beeaware.Domain.Beezzer;
import ch.unil.doplab.beeaware.Domain.Pollen;
import lombok.Getter;
import lombok.Setter;
import org.checkerframework.checker.nullness.qual.NonNull;
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
    private Long idBeezzer = 0L;
    private final Map<Long, Beezzer> beezzers = new HashMap<>();
    private Logger logger = Logger.getLogger(BeezzerService.class.getName());

    public void addBeezzer(@NotNull Beezzer beezzer){
        logger.log( Level.INFO, "Adding Beezzer...");
        for (Map.Entry<Long, Beezzer> bee: beezzers.entrySet()) {
            if (beezzer.getUsername() != null &&
                    bee.getValue().getUsername() != null &&
                    beezzer.getUsername().equals(bee.getValue().getUsername())) {
                logger.log( Level.WARNING, "Username {0} already used. Please try a new one.", beezzer.getUsername());
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
        logger.log( Level.INFO, "New Beezzer added : {0}", beezzer.getUsername());
    }

    public BeezzerDTO getBeezzer(Long idBeezzer) {
        var beezzer = beezzers.get(idBeezzer);
        logger.log( Level.INFO, "Searching for Beezzer...");
        if (beezzer == null) {
            logger.log( Level.WARNING, "Beezzer with id {0} doesn't exist.", idBeezzer);
            return null;
        }
        return new BeezzerDTO(beezzer);
    }

    public List<BeezzerDTO> getAllBeezzers() {
        logger.log( Level.INFO, "Searching for all registered Beezzers...");
        List<BeezzerDTO> allBeezzers = new ArrayList<>();
        for (Map.Entry<Long, Beezzer> beezzer : beezzers.entrySet()){
            allBeezzers.add(new BeezzerDTO(beezzer.getValue()));
        }
        return allBeezzers;
    }

    public void setBeezzer(@NotNull Beezzer beezzer) {
        logger.log( Level.INFO, "Setting Beezzer...", beezzer.getUsername());
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
        logger.log( Level.INFO, "Searching location for Beezzer...");
        if (beezzer == null) {
            logger.log( Level.WARNING, "Beezzer with id {0} doesn't exist.", beezzerId);
            return null;
        }
        return new LocationDTO(beezzer.getLocation());
    }

    public void addAllergen(String stringPollen, Long idBeezzer) {
        var pollen = Pollen.getPollenByName(stringPollen);
        PollenDTO pollenDTO = new PollenDTO(pollen);
        var beezzer = beezzers.get(idBeezzer);
        logger.log( Level.INFO, "Adding Allergen " + pollenDTO + " for Beezzer id " + idBeezzer + "...");
        if (beezzer == null) {
            logger.log( Level.WARNING, "Beezzer with id {0} doesn't exist.", idBeezzer);
            return;
        }
        if (!Pollen.getPredefinedPollens().contains(pollen)) {
            logger.log( Level.WARNING,"This pollen is not available in your country.");
            return;
        }
        if (beezzer.getAllergens().containsKey(pollen.getId())) {
            logger.log( Level.WARNING,"This allergen is already saved to your list.");
            return;
        }
        beezzer.getAllergens().put(pollen.getId(), pollen);
    }

    public AllergenDTO getBeezzerAllergens(Long idBeezzer) {
        var beezzer = beezzers.get(idBeezzer);
        logger.log( Level.INFO, "Searching allergens for Beezzer...");
        AllergenDTO allergenDTO = new AllergenDTO(beezzer.getAllergens());
        if (beezzer == null) {
            logger.log( Level.WARNING, "Beezzer with id {0} doesn't exist.", idBeezzer);
            return null;
        }
        return allergenDTO;
    }

    public boolean removeAllergen(Long idAllergen, Long idBeezzer) {
        var beezzer = beezzers.get(idBeezzer);
        var allergen = beezzer.getAllergens().get(idAllergen);
        var pollenDTO = new PollenDTO(allergen);
        logger.log( Level.INFO, "Removing Allergen...", pollenDTO);
        if (beezzer == null) {
            logger.log( Level.WARNING, "Beezzer with id {0} doesn't exist.", idBeezzer);
            return false;
        }
        if (allergen == null) {
            logger.log( Level.WARNING, "Allergen with ID {0} doesn't exist.", idAllergen);
            return false;

        }
        beezzer.getAllergens().remove(idAllergen);
        logger.log( Level.INFO, "Allergen deleted: {0}", pollenDTO);
        return true;
    }
}
