// TODO: Is this service necessary?
//package ch.unil.doplab.beeaware.rest;
//
//import ch.unil.doplab.beeaware.Domain.PollenLocationIndex;
//import ch.unil.doplab.beeaware.domain.ApplicationState;
//import jakarta.inject.Inject;
//import jakarta.ws.rs.*;
//import jakarta.ws.rs.core.MediaType;
//
//import java.awt.*;
//
//@Path("/pollenLocationIndex")
//public class PollenLocationIndexResource {
//    @Inject
//    private ApplicationState state;
//
//    @POST
//    @Consumes(MediaType.APPLICATION_JSON)
//    public void addPollenLocationIndex(PollenLocationIndex pollenLocationIndex) {
//        state.getPollenLocationIndexService().addPollenLocationIndex(pollenLocationIndex);
//    }
//}
