package ch.unil.doplab.beeaware.rest;

import ch.unil.doplab.beeaware.DTO.SymptomsDTO;
import ch.unil.doplab.beeaware.Domain.Symptom;
import ch.unil.doplab.beeaware.domain.ApplicationState;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.Date;
import java.util.List;

import static org.apache.http.client.utils.DateUtils.parseDate;

@Path("/symptoms")
public class SymptomResource {
    @Inject
    private ApplicationState state;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public void addSymptom(Symptom symptom) {
        state.getSymptomService().addSymptom(symptom);
    }

//    @POST
//    @Produces(MediaType.APPLICATION_JSON)
//    @Consumes(MediaType.APPLICATION_JSON)
//    public void addSymptom(Symptom symptom, Date date) {
//        state.getSymptomService().addSymptom(symptom, date);
//    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public List<SymptomsDTO> getSymptoms(@PathParam("id") Long id) {
        return state.getSymptomService().getSymptoms(id);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}&{date}")
    public List<SymptomsDTO> getSymptoms(@PathParam("id") Long id, @PathParam("date") String stringDate) {
        Date date = parseDate(stringDate);
        return state.getSymptomService().getSymptoms(id, date);
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public boolean removeSymptom(@PathParam("id") Long idSymptom) {
        return state.getSymptomService().removeSymptom(idSymptom);
    }
}
