package ch.unil.doplab.beeaware.service;

import ch.unil.doplab.beeaware.Domain.Coordinate;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.ComponentFilter;
import com.google.maps.model.GeocodingResult;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GeoApiService {
    /**
     * Creates and configures a GeoApiContext object to use the Google Geocoding API.
     *
     * @return A configured GeoApiContext instance with the API key.
     *
     */
    private String APIKEY;
    private Logger logger = Logger.getLogger(GeoApiService.class.getName());
    public GeoApiService(String apiKey){
        this.APIKEY = apiKey;
    }
    private GeoApiContext getGeoApiContext() {
        return new GeoApiContext.Builder()
                .apiKey(APIKEY)
                .build();
    }

    /**
     * Retrieves the latitude and longitude coordinates for a specified location using
     * the Google Geocoding API.
     *
     * @return An array containing the latitude and longitude of the {@code Location}.
     * @throws ApiException If the API encounters an error while processing the request.
     * @throws InterruptedException If the API request is interrupted.
     * @throws IOException If an input or output exception occurs.
     */
    @NotNull
    public Coordinate getCoordinates(int NPA, String country)
            throws ApiException, InterruptedException, IOException {
        GeocodingResult result = GeocodingApi
                .geocode(getGeoApiContext(), String.valueOf(NPA))
                .components(ComponentFilter.country(country))
                .language("fr")
                .await()[0];
        double lat = Math.round(result.geometry.location.lat * 100000.0) / 100000.0;
        double lng = Math.round(result.geometry.location.lng * 100000.0) / 100000.0;
        logger.log( Level.INFO, "Coordinate : {0}, Country : {1}, Latitude : {2}, Longitude : {3}", new Object[]{String.valueOf(NPA), country, String.valueOf(lat), String.valueOf(lng)});

        return new Coordinate(lat, lng);
    }
}