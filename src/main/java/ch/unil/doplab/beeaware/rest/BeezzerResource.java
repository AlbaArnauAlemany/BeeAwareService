package ch.unil.doplab.beeaware.rest;

import ch.unil.doplab.beeaware.Domain.Beezzer;
import ch.unil.doplab.beeaware.domain.ApplicationState;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.util.LinkedList;
import java.util.List;

// TODO: GOOD BeezzerResource
@Path("/beezzers")
public class BeezzerResource {
    @Inject
    private ApplicationState state;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Beezzer> getAllBeezzers() {
        return new LinkedList<>(state.getBeezzerService().getAllBeezzers().values());
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public Beezzer getBeezzer(@PathParam("id") Long id) {
        return state.getBeezzerService().getBeezzer(id);
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public boolean setBeezzer(@PathParam("id") Long id, Beezzer beezzer) {
        return state.getBeezzerService().setBeezzer(id, beezzer);
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

}
