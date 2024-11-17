package ch.unil.doplab.beeaware.rest;

import ch.unil.doplab.beeaware.DTO.LocationDTO;
import ch.unil.doplab.beeaware.Domain.Location;
import ch.unil.doplab.beeaware.Domain.Role;
import ch.unil.doplab.beeaware.Utilis.RoleRequired;
import ch.unil.doplab.beeaware.Utilis.Secured;
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
    public boolean addLocation(Location location) {
        Location locationCreated =  state.getLocationService().addOrCreateLocation(location);
        return locationCreated != null;
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
