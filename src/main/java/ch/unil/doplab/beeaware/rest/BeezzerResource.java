package ch.unil.doplab.beeaware.rest;

import ch.unil.doplab.beeaware.Domain.DTO.AllergenDTO;
import ch.unil.doplab.beeaware.Domain.DTO.BeezzerDTO;
import ch.unil.doplab.beeaware.Domain.DTO.LocationDTO;
import ch.unil.doplab.beeaware.Domain.Beezzer;
import ch.unil.doplab.beeaware.Domain.Role;
import ch.unil.doplab.beeaware.Utilis.RoleRequired;
import ch.unil.doplab.beeaware.Utilis.SameID;
import ch.unil.doplab.beeaware.Utilis.Secured;
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

    @GET
    @Secured
    @RoleRequired({Role.ADMIN})
    @Path("/set/{id}")
    public Long countBeezzerRole() {
        return state.getBeezzerService().countBeezzerRole();
    }

    @PUT
    @Secured
    @SameID
    @Path("/set/{id}")
    public boolean setBeezzer(@PathParam("id") Long id, Beezzer beezzer) {
        return state.getBeezzerService().setBeezzer(id, beezzer);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public BeezzerDTO addBeezzer(String beezzerJson) {
        return new BeezzerDTO(state.getBeezzerService().createBeezzerFromJSON(beezzerJson));
    }

    @DELETE
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
    public boolean addAllergen(@PathParam("id") Long idBeezzer, @QueryParam("stringPollen") String stringPollen) {
        return state.getBeezzerService().addAllergen(stringPollen, idBeezzer);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured
    @SameID
    @Path("/{id}/allergensset")
    public boolean addAllergenSet(@PathParam("id") Long idBeezzer, String stringPollens) {
        return state.getBeezzerService().addAllergenSet(stringPollens, idBeezzer);
    }
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured
    @SameID
    @Path("/{id}/password")
    public boolean changePassword(@PathParam("id") Long idBeezzer, @QueryParam("password") String password) {
        return state.getBeezzerService().changePassword(password, idBeezzer);
    }


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/username")
    public boolean checkBeezzerUsername(@QueryParam("username") String username) {
        return state.getBeezzerService().isBeezzerExistByUsername(username);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured
    @SameID
    @Path("/{id}/setallergensset")
    public boolean setAllergenSet(@PathParam("id") Long idBeezzer, String stringPollens) {
        return state.getBeezzerService().setAllergenSet(stringPollens, idBeezzer);
    }


    @DELETE
    @Secured
    @SameID
    @Path("/{id}/allergens")
    public boolean removeAllergen(@PathParam("id") Long idBeezzer, @QueryParam("stringPollen") String stringPollen) {
        return state.getBeezzerService().removeAllergen(stringPollen, idBeezzer);
    }
}
