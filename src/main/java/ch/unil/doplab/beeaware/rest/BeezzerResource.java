//package ch.unil.doplab.beeaware.rest;
//
//import ch.unil.doplab.beeaware.Domain.Beezzer;
//import ch.unil.doplab.beeaware.Domain.Symptom;
//import ch.unil.doplab.beeaware.domain.ApplicationState;
//import jakarta.inject.Inject;
//import jakarta.ws.rs.GET;
//import jakarta.ws.rs.Path;
//import jakarta.ws.rs.PathParam;
//import jakarta.ws.rs.Produces;
//import jakarta.ws.rs.core.MediaType;
//import jakarta.ws.rs.core.Response;
//
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.List;
//
//@Path("/beezzers")
//public class BeezzerResource {
//    @Inject
//    private ApplicationState state;
//
//    @GET
//    @Produces(MediaType.APPLICATION_JSON)
//    public HashSet<Beezzer> getBeezzers() {
//        HashSet<Beezzer> beezzers = new HashSet<>(state.getBeezzers());
//        return beezzers;
//    }
//
//    @GET
//    @Produces(MediaType.APPLICATION_JSON)
//    @Path("/symptomsfor{beezzerId}")
//    public List<Symptom> getSymptomsForASpecificBeezzer(@PathParam("beezzerId") Long beezzerId) {
//        return new ArrayList<>(state.getSymptomsForASpecificBeezzer(beezzerId));
//    }
//
//
//}
