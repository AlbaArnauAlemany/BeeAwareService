package ch.unil.doplab.beeaware.rest;

import ch.unil.doplab.beeaware.DTO.BeezzerDTO;
import ch.unil.doplab.beeaware.Domain.Beezzer;
import ch.unil.doplab.beeaware.Domain.Location;
import ch.unil.doplab.beeaware.Domain.PollenLocationIndex;
import ch.unil.doplab.beeaware.domain.*;
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
        for (Map.Entry<Long, Beezzer> beezzer : state.getBeezzerService().getBeezzers().entrySet()){
            beezzers.add(new BeezzerDTO(beezzer.getValue()));
        }
        return beezzers;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/pollenlocationsindex")
    public Map<Long, PollenLocationIndex> getPollenLocationIndex() {
        return  state.getPollenLocationIndexService().getPollenLocationIndexMap();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/locations")
    public Map<Long,Location> getLocations() {
        return  state.getLocationService().getLocations();
    }
}