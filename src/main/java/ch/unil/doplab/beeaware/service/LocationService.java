package ch.unil.doplab.beeaware.service;

import ch.unil.doplab.beeaware.Domain.Coordinate;
import ch.unil.doplab.beeaware.Domain.DTO.LocationDTO;
import ch.unil.doplab.beeaware.Domain.Location;
import ch.unil.doplab.beeaware.repository.LocationRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
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
@ApplicationScoped
@NoArgsConstructor
public class LocationService {
    private Logger logger = Logger.getLogger(LocationService.class.getName());
    @Inject
    private GeoApiService geoApiService;
    @Inject
    private LocationRepository locationRepository;

    @Inject
    public LocationService(LocationRepository locationRepository, GeoApiService geoApiService) {
        this.locationRepository = locationRepository;
        this.geoApiService = geoApiService;
    }

    public Location addOrCreateLocation(@NotNull Location location) {
        LocationDTO locationDTO = new LocationDTO(location);
        logger.log(Level.INFO, "Adding location {0}...", locationDTO);

        if (locationRepository.checkLocation(location.getNPA()) != null) {
            logger.log(Level.WARNING, "Location already exists: {0}", location);
            return location;
        }
        Coordinate coordinate;
        try {
            coordinate = geoApiService.getCoordinates(location);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error getting coordinates for location: {0}, the location might not exist!", location);
            logger.log(Level.SEVERE, "{0}", e.getStackTrace());
            return null;
        }

        location.setCoordinate(coordinate);
        locationRepository.addLocation(location);
        logger.log(Level.INFO, "New location added : {0}", location);
        return locationRepository.checkLocation(location.getNPA());
    }

    public List<LocationDTO> getAllRegisteredLocations() {
        logger.log(Level.INFO, "Searching for all registered locations (returns LocationDTO objects)...");
        List<LocationDTO> allLocations = new ArrayList<>();
        List<Location> locations = locationRepository.findAll();
        for (Location loc : locations) {
            allLocations.add(new LocationDTO(loc));
        }
        return allLocations;
    }

    public List<Location> getAllRegisteredLocationsL() {
        logger.log(Level.INFO, "Searching for all registered locations (returns Location objects)...");
        List<Location> allLocations = new ArrayList<>();
        List<Location> locations = locationRepository.findAll();
        allLocations.addAll(locations);
        return allLocations;
    }
}
