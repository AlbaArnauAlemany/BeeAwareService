package ch.unil.doplab.beeaware.service;

import ch.unil.doplab.beeaware.Domain.Location;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Getter
@Setter
public class LocationService {
    private Long idLocation = 0L;
    private final Map<Long, Location> locations = new HashMap<>();
    private Logger logger = Logger.getLogger(LocationService.class.getName());

    public void addLocation(@NotNull Location location) {
        logger.log( Level.INFO, "Adding location...", location);
        for (Map.Entry<Long, Location> loc: locations.entrySet()) {
            if (loc.getValue().getNPA() == location.getNPA() && loc.getValue().getCountry() == location.getCountry()) {
                logger.log( Level.WARNING, "Location already exists: {0}", location);
                return;
            }
        }
        location.setId(idLocation++);;
        locations.put(idLocation, location);
        logger.log( Level.INFO, "New location added : {0}", location);
    }
}
