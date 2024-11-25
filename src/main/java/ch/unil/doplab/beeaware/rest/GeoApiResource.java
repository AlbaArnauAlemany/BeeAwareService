package ch.unil.doplab.beeaware.rest;

import ch.unil.doplab.beeaware.Domain.Coordinate;
import ch.unil.doplab.beeaware.Domain.Location;
import ch.unil.doplab.beeaware.Utilis.Secured;
import ch.unil.doplab.beeaware.domain.ApplicationState;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

@Path("/geo")
public class GeoApiResource {
    @Inject
    private ApplicationState state;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Coordinate getCoordinate(@QueryParam("npa") int npa, @QueryParam("country") String country) {
        System.out.println("npa" + npa + ", country : " + country);
        Location locationCreated = new Location(npa, country);
        return state.getLocationService().getGeoApiService().getCoordinates(locationCreated);
    }
}
