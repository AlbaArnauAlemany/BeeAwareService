package ch.unil.doplab.beeaware.rest;

import ch.unil.doplab.beeaware.DTO.SymptomsDTO;
import ch.unil.doplab.beeaware.Domain.Role;
import ch.unil.doplab.beeaware.Domain.Symptom;
import ch.unil.doplab.beeaware.domain.ApplicationState;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/symptoms")
public class SymptomResource {
    @Inject
    private ApplicationState state;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured
    @SameID
    public void addSymptom(Symptom symptom) {
        state.getSymptomService().addSymptom(symptom);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @SameID
    @Path("/{id}")
    public List<SymptomsDTO> getSymptoms(@PathParam("id") Long id) {
        return state.getSymptomService().getSymptoms(id);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @SameID
    @Path("/{id}")
    public List<SymptomsDTO> getSymptoms(@PathParam("id") Long id, @QueryParam("date") String stringDate) {
        return state.getSymptomService().getSymptoms(id, stringDate);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @RoleRequired(Role.ADMIN)
    public List<SymptomsDTO> getAllSymptoms() {
        return state.getSymptomService().getAllSymptoms();
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @RoleRequired(Role.ADMIN)
    @Path("/{id}")
    public boolean removeSymptom(@PathParam("id") Long idSymptom) {
        return state.getSymptomService().removeSymptom(idSymptom);
    }
}
