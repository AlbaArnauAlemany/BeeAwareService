package ch.unil.doplab.beeaware.rest;

import ch.unil.doplab.beeaware.Domain.DTO.PollenInfoDTO;
import ch.unil.doplab.beeaware.Utilis.SameID;
import ch.unil.doplab.beeaware.Utilis.Secured;
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
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @SameID
    @Path("/beezzer/{id}")
    public List<PollenInfoDTO> getIndex(@PathParam("id") Long beezzerId) {
        return state.getIndexPollenForBeezzer().getIndex(beezzerId);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @SameID
    @Path("/{id}/date/{datefrom}/{dateto}")
    public List<PollenInfoDTO> getIndexForRangeDate(@PathParam("id") Long id, @PathParam("datefrom") String stringDateFrom, @PathParam("dateto") String stringDateTo) {
        return state.getIndexPollenForBeezzer().getIndexForRangeDate(id, stringDateFrom, stringDateTo);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @SameID
    @Path("/date/beezzer/{id}")
    public List<PollenInfoDTO> getIndexForDate(@QueryParam("date") String date, @PathParam("id") Long beezzerId) {
        return state.getIndexPollenForBeezzer().getIndexForDate(beezzerId, date);
    }
}
