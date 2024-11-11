package ch.unil.doplab.beeaware.rest;

import ch.unil.doplab.beeaware.DTO.AllergenDTO;
import ch.unil.doplab.beeaware.DTO.BeezzerDTO;
import ch.unil.doplab.beeaware.DTO.LocationDTO;
import ch.unil.doplab.beeaware.Domain.Beezzer;
import ch.unil.doplab.beeaware.domain.ApplicationState;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.util.LinkedList;
import java.util.List;

@Path("/beezzers")
public class BeezzerResource {
    @Inject
    private ApplicationState state;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<BeezzerDTO> getAllBeezzers() {
        return new LinkedList<>(state.getBeezzerService().getAllBeezzers());
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public BeezzerDTO getBeezzer(@PathParam("id") Long id) {
        return state.getBeezzerService().getBeezzer(id);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/setBeezzer")
    public void setBeezzer(Beezzer beezzer) {
        state.getBeezzerService().setBeezzer(beezzer);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Beezzer addBeezzer(Beezzer beezzer) {
        state.getBeezzerService().addBeezzer(beezzer);
        return beezzer;
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public boolean removeBeezzer(@PathParam("id") Long id) {
        return state.getBeezzerService().removeBeezzer(id);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/location/{id}")
    public LocationDTO getBeezzerLocation(@PathParam("id") Long id) {
        return state.getBeezzerService().getBeezzerLocation(id);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/allergens/{pollen}&{beezzerID}")
    public void addAllergen(@PathParam("pollen") String stringPollen, @PathParam("beezzerID") Long idBeezzer) {
        state.getBeezzerService().addAllergen(stringPollen, idBeezzer);
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/allergens/{allergenid}&{beezzerid}")
    public boolean removeAllergen(@PathParam("allergenid") Long idAllergen, @PathParam("beezzerid") Long idBeezzer) {
        return state.getBeezzerService().removeAllergen(idAllergen, idBeezzer);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/allergens{beezzerID}")
    public AllergenDTO getBeezzerAllergens(@PathParam("beezzerID") Long idBeezzer) {
        return state.getBeezzerService().getBeezzerAllergens(idBeezzer);
    }
}
