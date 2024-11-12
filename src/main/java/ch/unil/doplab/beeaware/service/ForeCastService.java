package ch.unil.doplab.beeaware.service;

import ch.unil.doplab.beeaware.DTO.PollenInfoDTO;
import ch.unil.doplab.beeaware.Domain.Beezzer;
import ch.unil.doplab.beeaware.Domain.Location;
import ch.unil.doplab.beeaware.Domain.PollenLocationIndex;
import ch.unil.doplab.beeaware.domain.ApplicationState;
import ch.unil.doplab.beeaware.domain.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import jakarta.inject.Inject;
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

import static ch.unil.doplab.beeaware.domain.Utils.formatDate;
import static org.apache.http.client.utils.DateUtils.parseDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ForeCastService {

    @Inject
    private ApplicationState state;

    private String APIKEY;
    private PollenLocationIndexService pollenLocationIndexService;
    private final Logger logger = Logger.getLogger(ForeCastService.class.getName());

    public ForeCastService(String apiKEy, PollenLocationIndexService pollenLocationIndexService) {
        this.pollenLocationIndexService = pollenLocationIndexService;
        this.APIKEY = apiKEy;
    }

    public void forecastAllLocation(Map<Long, Location> locations) {
        logger.log(Level.INFO, "Retrieving pollen per locations....");
        for (Map.Entry<Long, Location> loc : locations.entrySet()) {
            logger.log(Level.INFO, "Location : {0}", loc);
            pollenForecast(loc.getValue(), 1);
        }
    }

    // getIndex for a specific beezzer
    public List<PollenInfoDTO> getIndex(@NotNull Long beezzerId) {
        Beezzer beezzer = state.getBeezzerService().getBeezzers().get(beezzerId);
        logger.log(Level.INFO, "Retrieving pollen for a specific Beezzer...");
        List<PollenInfoDTO> PollenShortDTOs = new ArrayList<>();
        for (Map.Entry<Long, PollenLocationIndex> pollenLocationIndex : pollenLocationIndexService.getPollenLocationIndexMap().entrySet()) {
            if (pollenLocationIndex.getValue().getLocation().equals(beezzer.getLocation())) {

                for (PollenLocationIndex.DailyInfo dailyInfo : pollenLocationIndex.getValue().getDailyInfo()) {
                    Utils.addPollenInfo(PollenShortDTOs, dailyInfo, beezzer);
                }
            }
        }
        return PollenShortDTOs;
    }

    // getIndex for a specific beezzer and date
    public List<PollenInfoDTO> getIndex(String stringDate, @NotNull Long beezzerId) {
        Beezzer beezzer = state.getBeezzerService().getBeezzers().get(beezzerId);
        Date date = parseDate(stringDate);
        logger.log(Level.INFO, "Retrieving pollen for a specific Beezzer for the following day: {0}...", date);
        List<PollenInfoDTO> PollenShortDTOs = new ArrayList<>();
        for (Map.Entry<Long, PollenLocationIndex> pollenLocationIndex : pollenLocationIndexService.getPollenLocationIndexMap().entrySet()) {
            if (pollenLocationIndex.getValue().getLocation().equals(beezzer.getLocation())) {

                for (PollenLocationIndex.DailyInfo dailyInfo : pollenLocationIndex.getValue().getDailyInfo()) {

                    if (Utils.isSameDay(formatDate(dailyInfo.getDate()), date)) {
                        Utils.addPollenInfo(PollenShortDTOs, dailyInfo, beezzer);
                    }
                }
            }
        }
        return PollenShortDTOs;
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
                PollenLocationIndex pollenInfo = objectMapper.readValue(jsonResponse, PollenLocationIndex.class);
                pollenInfo.setLocation(location);
                pollenLocationIndexService.addPollenLocationIndex(pollenInfo);
            } else {
                logger.log(Level.SEVERE, "Error, coordinate null");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
