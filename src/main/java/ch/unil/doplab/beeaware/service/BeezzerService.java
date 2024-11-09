package ch.unil.doplab.beeaware.service;

import ch.unil.doplab.beeaware.DTO.BeezzerDTO;
import ch.unil.doplab.beeaware.DTO.LocationDTO;
import ch.unil.doplab.beeaware.Domain.Beezzer;
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
    private Long idBeezzer = 0L;
    private final Map<Long, Beezzer> beezzers = new HashMap<>();
    private Logger logger = Logger.getLogger(BeezzerService.class.getName());

    public Beezzer addBeezzer(@NotNull Beezzer beezzer){
        logger.log( Level.INFO, "Adding Beezzer...", beezzer);
        for (Map.Entry<Long, Beezzer> bee: beezzers.entrySet()) {
            if (beezzer.getUsername() != null &&
                    bee.getValue().getUsername() != null &&
                    beezzer.getUsername().equals(bee.getValue().getUsername())) {
                logger.log( Level.WARNING, "Username {0} already used. Please try a new one.", beezzer.getUsername());
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
        return beezzer;
    }

    public BeezzerDTO getBeezzer(Long beezzerId) {
        var beezzer = beezzers.get(beezzerId);
        logger.log( Level.INFO, "Searching for Beezzer...", beezzer.getUsername());
        if (beezzer == null) {
            logger.log( Level.WARNING, "Beezzer with id {0} doesn't exist.", beezzerId);
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
        logger.log( Level.INFO, "Setting Beezzer...", beezzer);
        beezzers.put(beezzer.getId(), beezzer);
    }

    public boolean removeBeezzer(Long id) {
        var beezzer = beezzers.get(id);
        logger.log( Level.INFO, "Removing Beezzer...", beezzer.getUsername());
        if (beezzer == null) {
            logger.log( Level.WARNING, "Beezzer with id {0} doesn't exist.", id);
            return false;

        }
        beezzers.remove(id);
        logger.log( Level.INFO, "Beezzer deleted : {0}", beezzer.getUsername());
        return true;
    }

    public LocationDTO getBeezzerLocation(Long beezzerId) {
        var beezzer = beezzers.get(beezzerId);
        logger.log( Level.INFO, "Searching location for Beezzer...", beezzer);
        if (beezzer == null) {
            logger.log( Level.WARNING, "Beezzer with id {0} doesn't exist.", beezzerId);
            return null;
        }
        return new LocationDTO(beezzer.getLocation());

    }
}
