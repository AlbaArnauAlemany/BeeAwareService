package ch.unil.doplab.beeaware.service;

import ch.unil.doplab.beeaware.Domain.Coordinate;
import ch.unil.doplab.beeaware.Domain.Location;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.AddressComponent;
import com.google.maps.model.AddressComponentType;
import com.google.maps.model.ComponentFilter;
import com.google.maps.model.GeocodingResult;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
public class GeoApiService {

    private String APIKEY = ResourceBundle.getBundle("application").getString("API_KEY");
    @PostConstruct
    public void init() {
        if (APIKEY == null || APIKEY.isEmpty()) {
            logger.log(Level.SEVERE, "API key is not configured!");
        }
    }


    private final Logger logger = Logger.getLogger(GeoApiService.class.getName());

    private GeoApiContext getGeoApiContext() {
        return new GeoApiContext.Builder().apiKey(APIKEY).build();
    }

    /**
     * Retrieves the latitude and longitude coordinates for a specified location using
     * the Google Geocoding API.
     *
     * @throws ApiException         If the API encounters an error while processing the request.
     * @throws InterruptedException If the API request is interrupted.
     * @throws IOException          If an input or output exception occurs.
     */
    public Coordinate getCoordinates(@NotNull Location location) {
        int NPA = location.getNPA();
        String country = location.getCountry();
        try {
            GeocodingResult result = GeocodingApi.geocode(getGeoApiContext(), String.valueOf(NPA)).components(ComponentFilter.country(country)).language("fr").await()[0];
            double lat = Math.round(result.geometry.location.lat * 100000.0) / 100000.0;
            double lng = Math.round(result.geometry.location.lng * 100000.0) / 100000.0;
            logger.log(Level.INFO, "Coordinate : {0}, Country : {1}, Latitude : {2}, Longitude : {3}", new Object[]{String.valueOf(NPA), country, String.valueOf(lat), String.valueOf(lng)});

            String cityName = null;
            logger.log(Level.INFO, "Retrieving City Name...");
            for (AddressComponent component : result.addressComponents) {
                for (AddressComponentType type : component.types) {
                    if (type == AddressComponentType.LOCALITY) {
                        cityName = component.longName;
                        logger.log(Level.INFO, "City Name retrieved : {0}", cityName);
                        break;
                    }
                }
                if (cityName != null) {
                    break;
                }
            }

            location.setCoordinate(new Coordinate(lat, lng));
            location.setCityName(cityName);
            return location.getCoordinate();
        } catch (ApiException | InterruptedException | IOException e) {
            logger.log(Level.WARNING, "Error while fetching coordinates for {1}, {2}: {3}", new Object[]{NPA, country, e.getMessage()});
            return null;
        }
    }
}