package ch.unil.doplab.beeaware.rest;

import ch.unil.doplab.beeaware.DTO.PollenInfoDTO;
import ch.unil.doplab.beeaware.Domain.PollenLocationIndex;
import ch.unil.doplab.beeaware.Domain.Role;
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
        List<PollenInfoDTO> pollenInfoDTOs = new ArrayList<>();
        for (Map.Entry<Long, PollenLocationIndex> pollenLocationIndex : state.getPollenLocationIndexService().getPollenLocationIndexMap().entrySet()) {
            pollenInfoDTOs.add(new PollenInfoDTO(pollenLocationIndex.getValue()));
        }
        return pollenInfoDTOs;
    }

    @POST
    @Secured
    @RoleRequired(Role.ADMIN)
    @Consumes(MediaType.APPLICATION_JSON)
    public boolean addPollenLocationIndex(PollenLocationIndex pollenLocationIndex) {
        return state.getPollenLocationIndexService().addPollenLocationIndex(pollenLocationIndex);
    }

    @DELETE
    @Secured
    @RoleRequired(Role.ADMIN)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public boolean removePollenLocationIndex(@PathParam("id") Long idPollenLocationIndex) {
        return state.getPollenLocationIndexService().removePollenLocationIndex(idPollenLocationIndex);
    }

    //TODO : AJOUTER DES VOID BOOLEENS POUR LES REPONSES
}
