package ch.unil.doplab.beeaware.rest;

import ch.unil.doplab.beeaware.DTO.PollenInfoDTO;
import ch.unil.doplab.beeaware.Domain.Beezzer;
import ch.unil.doplab.beeaware.Domain.Location;
import ch.unil.doplab.beeaware.Domain.Pollen;
import ch.unil.doplab.beeaware.domain.ApplicationState;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;
import java.util.Map;

@Path("/forecast")
public class ForeCastServiceResource {
    @Inject
    private ApplicationState state;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void forecastAllLocationPrivate(Map<Long, Location> locations) {
        state.getForeCastService().forecastAllLocationPrivate(locations);
    }

    // TODO: Use IDs
//    @GET
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Produces(MediaType.APPLICATION_JSON)
//    @Path("/{beezzer}")
//    public List<PollenInfoDTO> getIndex(@PathParam("beezzer") Beezzer beezzer) {
//        return state.getForeCastService().getIndex(beezzer);
//    }
//
//    @GET
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Produces(MediaType.APPLICATION_JSON)
//    @Path("/{date}/{beezzer}")
//    public List<PollenInfoDTO> getIndex(@PathParam("date") String date, @PathParam("beezzer") Beezzer beezzer) {
//        return state.getForeCastService().getIndex(date, beezzer);
//    }
//
//    @GET
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Produces(MediaType.APPLICATION_JSON)
//    @Path("/{date}/{location}")
//    public List<PollenInfoDTO> getIndex(@PathParam("date") String date, @PathParam("location") Location location) {
//        return state.getForeCastService().getIndex(date, location);
//    }
//
//    @GET
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Produces(MediaType.APPLICATION_JSON)
//    @Path("/{date}/{location}/{pollen}")
//    public List<PollenInfoDTO> getIndex(@PathParam("date") String date, @PathParam("location") Location location, @PathParam("pollen") Pollen pollen) {
//        return state.getForeCastService().getIndex(date, location, pollen);
//    }
}
