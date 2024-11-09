package ch.unil.doplab.beeaware.rest;

import ch.unil.doplab.beeaware.DTO.LocationDTO;
import ch.unil.doplab.beeaware.Domain.Beezzer;
import ch.unil.doplab.beeaware.Domain.Location;
import ch.unil.doplab.beeaware.domain.ApplicationState;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.LinkedList;
import java.util.List;

@Path("/locations")
public class LocationResource {
    @Inject
    private ApplicationState state;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void addLocation(Location location) {
        state.getLocationService().addLocation(location);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<LocationDTO> getAllRegisteredLocations() {
        return new LinkedList<>(state.getLocationService().getAllRegisteredLocations());
    }

}
