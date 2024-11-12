package ch.unil.doplab.beeaware.rest;

import ch.unil.doplab.beeaware.Domain.Role;
import ch.unil.doplab.beeaware.domain.ApplicationState;
import ch.unil.doplab.beeaware.service.ExcelService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("/excel")
public class ExcelResource {
    private final Logger logger = Logger.getLogger(ExcelResource.class.getName());
    ExcelService excelService = new ExcelService();
    @Inject
    private ApplicationState state;

    @GET
    @Secured
    @SameID
    @Path("/download/{id}")
    @Produces("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public Response downloadExcelFile(@PathParam("id") Long id) {
        try {
            return Response.ok(excelService.excelWrite(state.getSymptomService().getSymptoms(id))).header("Content-Disposition", "attachment; filename=symptoms.xlsx").build();

        } catch (IOException e) {
            logger.log(Level.SEVERE, "{0}", e.getMessage());
            return Response.serverError().entity("Excel creation error").build();
        }
    }
}