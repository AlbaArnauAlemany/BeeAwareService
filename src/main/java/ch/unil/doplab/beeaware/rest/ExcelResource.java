package ch.unil.doplab.beeaware.rest;

import ch.unil.doplab.beeaware.DTO.BeezzerDTO;
import ch.unil.doplab.beeaware.domain.ApplicationState;
import ch.unil.doplab.beeaware.domain.ExcelWriting;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("/excel")
public class ExcelResource {
    @Inject
    private ApplicationState state;
    private Logger logger = Logger.getLogger(ExcelResource.class.getName());
    @GET
    @Path("/download/{id}")
    @Produces("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public Response downloadExcelFile(@PathParam("id") Long id) {
        ExcelWriting excelWriting = new ExcelWriting(state.getSymptomService().getSymptoms(id));

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            excelWriting.getWorkbook().write(out);
            excelWriting.getWorkbook().close();

            byte[] excelData = out.toByteArray();
            return Response.ok(excelData)
                    .header("Content-Disposition", "attachment; filename=symptoms.xlsx")
                    .build();

        } catch (IOException e) {
            logger.log(Level.SEVERE, "{0}", e.getMessage());
            return Response.serverError().entity("Excel creation error").build();
        }
    }
}