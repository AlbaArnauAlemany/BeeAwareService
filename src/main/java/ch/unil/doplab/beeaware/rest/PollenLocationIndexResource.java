package ch.unil.doplab.beeaware.rest;

import ch.unil.doplab.beeaware.Domain.DTO.PollenInfoDTO;
import ch.unil.doplab.beeaware.Domain.PollenLocationIndex;
import ch.unil.doplab.beeaware.Domain.Role;
import ch.unil.doplab.beeaware.Utilis.RoleRequired;
import ch.unil.doplab.beeaware.Utilis.Secured;
import ch.unil.doplab.beeaware.domain.ApplicationState;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Path("/pollenLocationIndex")
public class PollenLocationIndexResource {
    @Inject
    private ApplicationState state;

    @GET
    @Secured
    @RoleRequired(Role.ADMIN)
    @Produces(MediaType.APPLICATION_JSON)
    public List<PollenInfoDTO> getAllPollenLocationIndex() {
        return state.getPollenLocationIndexService().findAllPollenIndexLocation();
    }

    @POST
    @Secured
    @RoleRequired(Role.ADMIN)
    @Consumes(MediaType.APPLICATION_JSON)
    public void addPollenLocationIndex(PollenLocationIndex pollenLocationIndex) {
        state.getPollenLocationIndexService().addPollenLocationIndex(pollenLocationIndex);
    }

    @DELETE
    @Secured
    @RoleRequired(Role.ADMIN)
    @Path("/{id}")
    public boolean removePollenLocationIndex(@PathParam("id") Long idPollenLocationIndex) {
        return state.getPollenLocationIndexService().removePollenLocationIndex(idPollenLocationIndex);
    }
}
