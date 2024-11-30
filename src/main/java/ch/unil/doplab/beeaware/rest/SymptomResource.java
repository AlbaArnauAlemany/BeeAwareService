package ch.unil.doplab.beeaware.rest;

import ch.unil.doplab.beeaware.Domain.DTO.SymptomsDTO;
import ch.unil.doplab.beeaware.Domain.Role;
import ch.unil.doplab.beeaware.Domain.Symptom;
import ch.unil.doplab.beeaware.Utilis.RoleRequired;
import ch.unil.doplab.beeaware.Utilis.SameID;
import ch.unil.doplab.beeaware.Utilis.Secured;
import ch.unil.doplab.beeaware.domain.ApplicationState;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/symptom")
public class SymptomResource {
    @Inject
    private ApplicationState state;

    @POST
    @Secured
    @Path("/add")
    public boolean addSymptom(Symptom symptom) {
        return state.getSymptomService().addSymptom(symptom);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/{id}")
    public List<SymptomsDTO> getSymptom(@PathParam("id") Long id) {
        return state.getSymptomService().getSymptom(id);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @SameID
    @Path("/{id}/date")
    public SymptomsDTO getSymptomForDate(@PathParam("id") Long id, @QueryParam("date") String stringDate) {
        return state.getSymptomService().getSymptomForDate(id, stringDate);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @SameID
    @Path("/{id}/date/{datefrom}/{dateto}")
    public List<SymptomsDTO> getSymptomForRange(@PathParam("id") Long id, @PathParam("datefrom") String stringDateFrom, @PathParam("dateto") String stringDateTo) {
        return state.getSymptomService().getSymptomForRange(id, stringDateFrom, stringDateTo);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @SameID
    @Path("/{id}/{idSymptom}")
    public SymptomsDTO getSymptomWithId(@PathParam("id") Long id, @PathParam("idSymptom") Long idSymptom) {
        return state.getSymptomService().getSymptom(id, idSymptom);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @RoleRequired(Role.ADMIN)
    public List<SymptomsDTO> getAllSymptoms() {
        return state.getSymptomService().getAllSymptoms();
    }


    @DELETE
    @Secured
    @RoleRequired(Role.ADMIN)
    @Path("/{id}")
    public boolean removeSymptom(@PathParam("id") Long idSymptom) {
        return state.getSymptomService().removeSymptom(idSymptom);
    }
}
