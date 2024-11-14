package ch.unil.doplab.beeaware.service;

import ch.unil.doplab.beeaware.DTO.PollenInfoDTO;
import ch.unil.doplab.beeaware.Domain.Beezzer;
import ch.unil.doplab.beeaware.Domain.Location;
import ch.unil.doplab.beeaware.Domain.PollenLocationIndex;
import ch.unil.doplab.beeaware.Domain.PollenLocationInfo;
import ch.unil.doplab.beeaware.domain.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static ch.unil.doplab.beeaware.domain.Utils.transformPollenInfoInPollenIndex;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ForeCastService {
    private String APIKEY;
    private PollenLocationIndexService pollenLocationIndexService;
    private final Logger logger = Logger.getLogger(ForeCastService.class.getName());

    public void forecastAllLocation(Map<Long, Location> locations) {
        logger.log(Level.INFO, "Retrieving pollen per locations....");
        for (Map.Entry<Long, Location> loc : locations.entrySet()) {
            logger.log(Level.INFO, "Location : {0}", loc);
            pollenForecast(loc.getValue(), 1);
        }
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
                logger.log(Level.INFO, "{0}", pollenInfo);
                List<PollenLocationIndex> newPollenLocationInfo = transformPollenInfoInPollenIndex(pollenInfo, location);
                for (PollenLocationIndex polLocIndex: newPollenLocationInfo) {
                    pollenLocationIndexService.addPollenLocationIndex(polLocIndex);
                }
            } else {
                logger.log(Level.SEVERE, "Error, coordinate null");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
