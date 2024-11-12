package ch.unil.doplab.beeaware.rest;

import ch.unil.doplab.beeaware.DTO.LocationDTO;
import ch.unil.doplab.beeaware.Domain.Location;
import ch.unil.doplab.beeaware.Domain.Role;
import ch.unil.doplab.beeaware.domain.ApplicationState;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.LinkedList;
import java.util.List;

@Path("/location")
public class LocationResource {
    @Inject
    private ApplicationState state;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured
    public void addLocation(Location location) {
        state.getLocationService().addLocation(location);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @RoleRequired(Role.ADMIN)
    public List<LocationDTO> getAllLocations() {
        return new LinkedList<>(state.getLocationService().getAllRegisteredLocations());
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @RoleRequired(Role.ADMIN)
    @Path("/{id}")
    public boolean removeLocation(@PathParam("id") Long idLocation) {
        return state.getLocationService().removeLocation(idLocation);
    }
}
