package ch.unil.doplab.beeaware.service;

import ch.unil.doplab.beeaware.DTO.LocationDTO;
import ch.unil.doplab.beeaware.Domain.Location;
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
public class LocationService {
    private final Map<Long, Location> locations = new HashMap<>();
    private Long idLocation = 0L;
    private Logger logger = Logger.getLogger(LocationService.class.getName());
    private GeoApiService geoApiService;

    public LocationService(GeoApiService geoApiService){
        this();
        this.geoApiService = geoApiService;
    }

    public Location addOrCreateLocation(@NotNull Location location) {
        LocationDTO locationDTO = new LocationDTO(location);
        logger.log(Level.INFO, "Adding location {0}...", locationDTO);

        for (Map.Entry<Long, Location> loc : locations.entrySet()) {
            if (loc.getValue().equals(location)) {
                logger.log(Level.WARNING, "Location already exists: {0}", location);
                return loc.getValue();
            }
        }

        try {
            geoApiService.getCoordinates(location);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error getting coordinates for location: {0}, the location might not exist!", location);
            logger.log(Level.SEVERE, "{0}", e.getStackTrace());
            return null;
        }

        location.setId(idLocation);
        locations.put(idLocation++, location);
        logger.log(Level.INFO, "New location added : {0}", location);
        return location;
    }

    public List<LocationDTO> getAllRegisteredLocations() {
        logger.log(Level.INFO, "Searching for all registered locations (returns LocationDTO objects)...");
        List<LocationDTO> allLocations = new ArrayList<>();
        for (Map.Entry<Long, Location> loc : locations.entrySet()) {
            allLocations.add(new LocationDTO(loc.getValue()));
        }
        return allLocations;
    }

    public boolean removeLocation(Long idLocation) {
        var location = locations.get(idLocation);
        logger.log(Level.INFO, "Removing Location...");
        if (location == null) {
            logger.log(Level.WARNING, "Location with ID {0} doesn't exist.", idLocation);
            return false;

        }
        var locationDTO = new LocationDTO(location);
        locations.remove(idLocation);
        logger.log(Level.INFO, "Location deleted : {0}", locationDTO);
        return true;
    }
}
