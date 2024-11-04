package ch.unil.doplab.beeaware.rest;

import ch.unil.doplab.beeaware.Domain.Beezzer;
import ch.unil.doplab.beeaware.Domain.Symptom;
import ch.unil.doplab.beeaware.domain.ApplicationState;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.checkerframework.checker.units.qual.C;

import java.awt.*;

@Path("/symptoms")
public class SymptomResource {
    @Inject
    private ApplicationState state;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Symptom addSymptom(Symptom symptom, Beezzer beezzer) {
        return state.addSymptom(symptom, beezzer);
    }

}
