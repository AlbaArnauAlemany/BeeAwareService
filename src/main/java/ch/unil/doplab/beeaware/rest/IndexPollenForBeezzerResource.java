package ch.unil.doplab.beeaware.rest;

import ch.unil.doplab.beeaware.DTO.PollenInfoDTO;
import ch.unil.doplab.beeaware.domain.ApplicationState;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/forecast")
public class IndexPollenForBeezzerResource {
    @Inject
    private ApplicationState state;

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
