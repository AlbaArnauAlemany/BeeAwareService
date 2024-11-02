package ch.unil.doplab.beeaware.rest;

import ch.unil.doplab.beeaware.Domain.Beezzer;
import ch.unil.doplab.beeaware.domain.*;
import com.google.maps.errors.ApiException;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Path("/service")
public class ServiceResource {
    @Inject
    private ApplicationState state;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/reset")
    public Response reset() throws IOException, InterruptedException, ApiException {
        state.init();
        return Response.ok("BeeAware Service was reset at " + LocalDateTime.now()).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/beezzers")
    public HashSet<Beezzer> getBeezzers() {
        HashSet<Beezzer> beezzers = new HashSet<>();
        return beezzers;
    }
}