package ch.unil.doplab.beeaware.rest;

import ch.unil.doplab.beeaware.DTO.PollenInfoDTO;
import ch.unil.doplab.beeaware.Domain.PollenLocationIndex;
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

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void addPollenLocationIndex(PollenLocationIndex pollenLocationIndex) {
        state.getPollenLocationIndexService().addPollenLocationIndex(pollenLocationIndex);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<PollenInfoDTO> getPollenLocationIndex() {
        List<PollenInfoDTO> pollenInfoDTOs = new ArrayList<>();
        for (Map.Entry<Long, PollenLocationIndex> pollenLocationIndex : state.getPollenLocationIndexService().getPollenLocationIndexMap().entrySet()) {
            pollenInfoDTOs.add(new PollenInfoDTO(pollenLocationIndex.getValue()));
        }
        return pollenInfoDTOs;
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public boolean removePollenLocationIndex(@PathParam("id") Long idPollenLocationIndex) {
        return state.getPollenLocationIndexService().removePollenLocationIndex(idPollenLocationIndex);
    }
}
