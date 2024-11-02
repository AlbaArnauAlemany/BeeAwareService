package ch.unil.doplab.beeaware.rest;

import ch.unil.doplab.beeaware.DTO.BeezzerDTO;
import ch.unil.doplab.beeaware.Domain.Beezzer;
import ch.unil.doplab.beeaware.domain.*;
import com.google.maps.errors.ApiException;
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

    @Secured
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/beezzers")
    public List<BeezzerDTO> getBeezzers() {
        List<BeezzerDTO> beezzers = new ArrayList<>();
        for (Map.Entry<Long, Beezzer> beezzer : state.getBeezzers().entrySet()){
            beezzers.add(new BeezzerDTO(beezzer.getValue().getUsername(), beezzer.getValue().getEmail(), beezzer.getValue().getLocation(), beezzer.getValue().getAllergens()));
        }
        return beezzers;
    }
}