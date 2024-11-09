package ch.unil.doplab.beeaware.rest;

import ch.unil.doplab.beeaware.Domain.PollenLocationIndex;
import ch.unil.doplab.beeaware.domain.ApplicationState;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.LocalDateTime;
import java.util.*;

@Path("/service")
public class ServiceResource {
    @Inject
    private ApplicationState state;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/reset")
    public Response reset() {
        state.init();
        return Response.ok("BeeAware Service was reset at " + LocalDateTime.now()).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/pollenlocationsindex")
    public Map<Long, PollenLocationIndex> getPollenLocationIndex() {
        return  state.getPollenLocationIndexService().getPollenLocationIndexMap();
    }

}