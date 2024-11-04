package ch.unil.doplab.beeaware.rest;

import ch.unil.doplab.beeaware.Domain.Beezzer;
import ch.unil.doplab.beeaware.Domain.Symptom;
import ch.unil.doplab.beeaware.domain.ApplicationState;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
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
        return new LinkedList<>(state.getAllBeezzers().values());
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public Beezzer getBeezzer(@PathParam("id") Long id) {
        return state.getBeezzer(id);
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public boolean setBeezzer(@PathParam("id") Long id, Beezzer beezzer) {
        return state.setBeezzer(id, beezzer);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Beezzer addBeezzer(Beezzer beezzer) {
        state.addBeezzer(beezzer);
        return beezzer;
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public boolean removeBeezzer(@PathParam("id") Long id) {
        return state.removeBeezzer(id);
    }

}
