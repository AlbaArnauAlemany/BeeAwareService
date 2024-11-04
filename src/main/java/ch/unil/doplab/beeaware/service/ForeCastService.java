package ch.unil.doplab.beeaware.service;

import ch.unil.doplab.beeaware.Domain.Beezzer;
import ch.unil.doplab.beeaware.DTO.PollenInfoDTO;
import ch.unil.doplab.beeaware.Domain.Location;
import ch.unil.doplab.beeaware.Domain.Pollen;
import ch.unil.doplab.beeaware.Domain.PollenLocationIndex;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ForeCastService {
    private String APIKEY;
    private PollenLocationIndexService pollenLocationIndexService;
    private Logger logger = Logger.getLogger(ForeCastService.class.getName());
    public ForeCastService(String apiKEy, PollenLocationIndexService pollenLocationIndexService){
        this.pollenLocationIndexService = pollenLocationIndexService;
        this.APIKEY = apiKEy;
    }
    public void forecastAllLocation(Map<Long, Location> locations){
        logger.log( Level.INFO, "Retriving pollen per locations....");
        for (Map.Entry<Long, Location> loc: locations.entrySet()) {
            logger.log( Level.INFO, "Location : {0}", loc);
            pollenForecast(loc.getValue(), 1);
        }
    }


    //TODO : Add date and change for an ID
    public List<PollenInfoDTO> getIndexForSpecificBeezzer(@NotNull Beezzer beezzer){
        List<PollenInfoDTO> PollenShortDTOs = new ArrayList<>();
        for (Map.Entry<Long, PollenLocationIndex> pollenLocationIndex : pollenLocationIndexService.getPollenLocationIndexMap().entrySet()) {
            if(pollenLocationIndex.getValue().getLocation().getNPA() == beezzer.getLocation().getNPA() &&
                    pollenLocationIndex.getValue().getLocation().getCountry() == beezzer.getLocation().getCountry()){

                for (PollenLocationIndex.DailyInfo dailyInfo : pollenLocationIndex.getValue().getDailyInfo()) {

                    for (PollenLocationIndex.PollenTypeInfo pollenTypeDailyInfo : dailyInfo.getPollenTypeInfo()) {
                        for (Map.Entry<Long, Pollen> pollen : beezzer.getAllergens().entrySet()) {
                            if (pollen.getValue().getPollenNameEN().equals(pollenTypeDailyInfo.getDisplayName())) {
                                if (pollenTypeDailyInfo.getIndexInfo() != null) {
                                    PollenShortDTOs.add(new PollenInfoDTO(pollenTypeDailyInfo));
                                }
                            }
                        }
                    }

                    for (PollenLocationIndex.PlantInfo pollenDailyInfo : dailyInfo.getPlantInfo()) {
                        for (Map.Entry<Long, Pollen> pollen : beezzer.getAllergens().entrySet()) {
                            if (pollen.getValue().getPollenNameEN().equals(pollenDailyInfo.getDisplayName())) {
                                if (pollenDailyInfo.getIndexInfo() != null) {
                                    PollenShortDTOs.add(new PollenInfoDTO(pollenDailyInfo));
                                }
                            }
                        }
                    }
                }
            }
        }
        return PollenShortDTOs;
    }


    public void pollenForecast(Location location, int days) {
        String url = String.format(
                "https://pollen.googleapis.com/v1/forecast:lookup?key=%s&location.longitude=%s&location.latitude=%s&days=%s",
                APIKEY, location.getCoordinate().getLongitude(), location.getCoordinate().getLatitude(), days);

        try {
            NetHttpTransport httpTransport = new NetHttpTransport();
            HttpRequestFactory requestFactory = httpTransport.createRequestFactory();
            HttpRequest request = requestFactory.buildGetRequest(new com.google.api.client.http.GenericUrl(url));
            HttpResponse response = request.execute();

            String jsonResponse = response.parseAsString();
            ObjectMapper objectMapper = new ObjectMapper();
            PollenLocationIndex pollenInfo = objectMapper.readValue(jsonResponse, PollenLocationIndex.class);
            pollenInfo.setLocation(location);
            pollenLocationIndexService.addPollenLocationIndex(pollenInfo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
