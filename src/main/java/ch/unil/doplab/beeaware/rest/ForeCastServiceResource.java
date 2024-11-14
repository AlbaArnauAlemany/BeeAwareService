package ch.unil.doplab.beeaware.rest;

import ch.unil.doplab.beeaware.DTO.PollenInfoDTO;
import ch.unil.doplab.beeaware.Domain.Beezzer;
import ch.unil.doplab.beeaware.Domain.Location;
import ch.unil.doplab.beeaware.Domain.Pollen;
import ch.unil.doplab.beeaware.Domain.Role;
import ch.unil.doplab.beeaware.domain.ApplicationState;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;
import java.util.Map;

@Path("/forecast")
public class ForeCastServiceResource {
    @Inject
    private ApplicationState state;

    // TODO: To remove after creating CRON task
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured
    @RoleRequired(Role.ADMIN)
    public void forecastAllLocation(Map<Long, Location> locations) {
        state.getForeCastService().forecastAllLocation(locations);
    }

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @SameID
    @Path("/beezzer/{id}")
    public List<PollenInfoDTO> getIndex(@PathParam("id") Long beezzerId) {
        return state.getIndexPollenForBeezzer().getIndex(beezzerId);
    }

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @SameID
    @Path("/date/beezzer/{id}")
    public List<PollenInfoDTO> getIndex(@QueryParam("date") String date, @PathParam("id") Long beezzerId) {
        return state.getIndexPollenForBeezzer().getIndex(date, beezzerId);
    }
}
