package ch.unil.doplab.beeaware.service;

import ch.unil.doplab.beeaware.Domain.Location;
import ch.unil.doplab.beeaware.Domain.PollenLocationIndex;
import ch.unil.doplab.beeaware.Domain.PollenLocationInfo;
import ch.unil.doplab.beeaware.repository.PollenLocationIndexRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import static ch.unil.doplab.beeaware.Utilis.Utils.transformPollenInfoInPollenIndex;

@Data
@NoArgsConstructor
@ApplicationScoped
public class ForeCastService {
    private String APIKEY;
    @Inject
    private PollenLocationIndexService pollenLocationIndexService;
    @Inject
    private PollenLocationIndexRepository pollenLocationIndexRepository;
    private final Logger logger = Logger.getLogger(ForeCastService.class.getName());

    public void forecastAllLocation(List<Location> locations) {
        try {
            APIKEY = ResourceBundle.getBundle("application").getString("API_KEY");

            if (APIKEY == null || APIKEY.isEmpty()) {
                logger.log(Level.SEVERE, "API key is not configured!");
                throw new IllegalStateException("API key is missing. Please configure it in application.properties.");
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to load API key: {0}", e.getMessage());
            throw new IllegalStateException("API key configuration failed.", e);
        }

        logger.log(Level.INFO, "Retrieving pollen per locations....");
        for (Location loc : locations) {
            logger.log(Level.INFO, "Location : {0}", loc);
            pollenForecast(loc, 1);
        }
    }

    public ForeCastService(String APIKEY, PollenLocationIndexRepository pollenLocationIndexRepository, PollenLocationIndexService pollenLocationIndexService){
        this.APIKEY = APIKEY;
        this.pollenLocationIndexService = pollenLocationIndexService;
        this.pollenLocationIndexRepository = pollenLocationIndexRepository;
    }

    public void pollenForecast(Location location, int days) {
        try {
            if (location.getCoordinate() != null) {
                String url = String.format(
                        "https://pollen.googleapis.com/v1/forecast:lookup?key=%s&location.longitude=%s&location.latitude=%s&days=%s",
                        APIKEY, location.getCoordinate().getLongitude(), location.getCoordinate().getLatitude(), days);
                NetHttpTransport httpTransport = new NetHttpTransport();
                HttpRequestFactory requestFactory = httpTransport.createRequestFactory();
                HttpRequest request = requestFactory.buildGetRequest(new com.google.api.client.http.GenericUrl(url));
                HttpResponse response = request.execute();

                String jsonResponse = response.parseAsString();
                ObjectMapper objectMapper = new ObjectMapper();
                PollenLocationInfo pollenInfo = objectMapper.readValue(jsonResponse, PollenLocationInfo.class);
                List<PollenLocationIndex> newPollenLocationInfo = transformPollenInfoInPollenIndex(pollenInfo, location);
                if(newPollenLocationInfo != null) {
                    for (PollenLocationIndex polLocIndex : newPollenLocationInfo) {
                        if (polLocIndex != null) {
                            pollenLocationIndexService.addPollenLocationIndex(polLocIndex);
                        }
                    }
                }
            } else {
                logger.log(Level.SEVERE, "Error, coordinate null");
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error pollen Forecasting");
            logger.log(Level.SEVERE,  e.getMessage());
        }
    }
}
