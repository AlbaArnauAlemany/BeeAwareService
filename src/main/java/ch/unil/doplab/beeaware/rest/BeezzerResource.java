package ch.unil.doplab.beeaware.rest;

import ch.unil.doplab.beeaware.DTO.AllergenDTO;
import ch.unil.doplab.beeaware.DTO.BeezzerDTO;
import ch.unil.doplab.beeaware.DTO.LocationDTO;
import ch.unil.doplab.beeaware.Domain.Beezzer;
import ch.unil.doplab.beeaware.Domain.Role;
import ch.unil.doplab.beeaware.Utilis.RoleRequired;
import ch.unil.doplab.beeaware.Utilis.SameID;
import ch.unil.doplab.beeaware.Utilis.Secured;
import ch.unil.doplab.beeaware.domain.ApplicationState;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.LinkedList;
import java.util.List;

@Path("/beezzers")
public class BeezzerResource {
    @Inject
    private ApplicationState state;

    @GET
    @Secured
    @RoleRequired({Role.ADMIN})
    @Produces(MediaType.APPLICATION_JSON)
    public List<BeezzerDTO> getAllBeezzers() {
        return new LinkedList<>(state.getBeezzerService().getAllBeezzers());
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @SameID
    @Path("/{id}")
    public BeezzerDTO getBeezzer(@PathParam("id") Long id) {
        return state.getBeezzerService().getBeezzer(id);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured
    @RoleRequired({Role.ADMIN})
    @Path("/setBeezzer")
    public Response setBeezzer(Beezzer beezzer) {
        state.getBeezzerService().setBeezzer(beezzer);
        return Response.ok().build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public BeezzerDTO addBeezzer(String beezzerJson) {
        return new BeezzerDTO(state.getBeezzerService().createBeezzerFromJSON(beezzerJson));
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @RoleRequired({Role.ADMIN})
    @Path("/{id}")
    public boolean removeBeezzer(@PathParam("id") Long id) {
        return state.getBeezzerService().removeBeezzer(id);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @RoleRequired({Role.ADMIN})
    @Path("/location/{id}")
    public LocationDTO getBeezzerLocation(@PathParam("id") Long id) {
        return state.getBeezzerService().getBeezzerLocation(id);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured
    @SameID
    @Path("/location/{id}")
    public boolean setBeezzerLocation(@PathParam("id") Long idBeezzer, String locationJson) {
        return state.getBeezzerService().setBeezzerLocation(idBeezzer, locationJson);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @SameID
    @Path("/{id}/allergens")
    public AllergenDTO getBeezzerAllergens(@PathParam("id") Long idBeezzer) {
        return state.getBeezzerService().getBeezzerAllergens(idBeezzer);
    }

    @POST
    @Secured
    @SameID
    @Path("/{id}/allergens")
    public Response addAllergen(@PathParam("id") Long idBeezzer, @QueryParam("stringPollen") String stringPollen) {
        state.getBeezzerService().addAllergen(stringPollen, idBeezzer);
        return Response.ok().build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured
    @SameID
    @Path("/{id}/allergensset")
    public Response addAllergenSet(@PathParam("id") Long idBeezzer, String stringPollens) {
        state.getBeezzerService().addAllergenSet(stringPollens, idBeezzer);
        return Response.ok().build();
    }

    @DELETE
    @Secured
    @SameID
    @Path("/{id}/allergens")
    public boolean removeAllAllergen(@PathParam("id") Long idBeezzer, @QueryParam("stringPollen") String stringPollen) {
        return state.getBeezzerService().removeAllergen(stringPollen, idBeezzer);
    }
}
