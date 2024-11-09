package ch.unil.doplab.beeaware.service;

import ch.unil.doplab.beeaware.DTO.LocationDTO;
import ch.unil.doplab.beeaware.Domain.Location;
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
public class LocationService {
    private Long idLocation = 0L;
    private final Map<Long, Location> locations = new HashMap<>();
    private Logger logger = Logger.getLogger(LocationService.class.getName());

    public void addLocation(@NotNull Location location) {
        logger.log( Level.INFO, "Adding location...", location);
        for (Map.Entry<Long, Location> loc: locations.entrySet()) {
            if (loc.getValue().equals(location)) {
                logger.log( Level.WARNING, "Location already exists: {0}", location);
                return;
            }
        }
        location.setId(idLocation++);;
        locations.put(idLocation, location);
        logger.log( Level.INFO, "New location added : {0}", location);
    }

    public List<LocationDTO> getAllRegisteredLocations() {
        logger.log( Level.INFO, "Searching for all registered locations (returns LocationDTO objects)...");
        List <LocationDTO> allLocations = new ArrayList<>();
        for (Map.Entry<Long, Location> loc: locations.entrySet()) {
            allLocations.add(new LocationDTO(loc.getValue()));
        }
        return allLocations;
    }

    public List<Location> getAllRegisteredLocationsPrivate() {
        logger.log( Level.INFO, "Searching for all registered locations (returns Location objects)...");
        List <Location> allLocations = new ArrayList<>();
        for (Map.Entry<Long, Location> loc: locations.entrySet()) {
            allLocations.add(loc.getValue());
        }
        return allLocations;
    }
}
