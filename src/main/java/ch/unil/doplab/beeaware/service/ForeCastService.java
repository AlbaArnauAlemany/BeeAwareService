package ch.unil.doplab.beeaware.service;

import ch.unil.doplab.beeaware.DTO.PollenDTO;
import ch.unil.doplab.beeaware.Domain.Beezzer;
import ch.unil.doplab.beeaware.DTO.PollenInfoDTO;
import ch.unil.doplab.beeaware.domain.ApplicationState;
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
import java.util.*;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.apache.http.client.utils.DateUtils.parseDate;

public class ForeCastService {
    private String APIKEY;
    private PollenLocationIndexService pollenLocationIndexService;
    private Logger logger = Logger.getLogger(ForeCastService.class.getName());
    public ForeCastService(String apiKEy, PollenLocationIndexService pollenLocationIndexService){
        this.pollenLocationIndexService = pollenLocationIndexService;
        this.APIKEY = apiKEy;
    }

    public void forecastAllLocationPrivate(Map<Long, Location> locations) {
        logger.log( Level.INFO, "Retrieving pollen per locations....");
        for (Map.Entry<Long, Location> loc: locations.entrySet()) {
            logger.log( Level.INFO, "Location : {0}", loc);
            pollenForecast(loc.getValue(), 1);
        }
    }

    // TODO: Comment faire que qu'on puisse accéder à une instance de LocationService
//    public void forecastAllLocation() {
//        logger.log( Level.INFO, "Retrieving pollen per locations....");
//        for (Location loc: LocationService.getAllRegisteredLocationsPrivate()) {
//            logger.log( Level.INFO, "Location : {0}", loc);
//            pollenForecast(loc, 1);
//        }
//    }

    //TODO : Change for an ID
    // getIndex for a specific beezzer
    public List<PollenInfoDTO> getIndex(@NotNull Beezzer beezzer){
        logger.log( Level.INFO, "Retrieving pollen for a specific Beezzer...");
        List<PollenInfoDTO> PollenShortDTOs = new ArrayList<>();
        for (Map.Entry<Long, PollenLocationIndex> pollenLocationIndex : pollenLocationIndexService.getPollenLocationIndexMap().entrySet()) {
            if(pollenLocationIndex.getValue().getLocation().equals(beezzer.getLocation())){

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

    //TODO : Change for an ID
    // getIndex for a specific beezzer and date
    public List<PollenInfoDTO> getIndex(String stringDate, @NotNull Beezzer beezzer) {
        Date date = parseDate(stringDate);
        logger.log( Level.INFO, "Retrieving pollen for a specific Beezzer for the following day: {0}...", date);
        Calendar calendar = Calendar.getInstance();
        List<PollenInfoDTO> PollenShortDTOs = new ArrayList<>();
        for (Map.Entry<Long, PollenLocationIndex> pollenLocationIndex : pollenLocationIndexService.getPollenLocationIndexMap().entrySet()) {
            if(pollenLocationIndex.getValue().getLocation().equals(beezzer.getLocation())){

                for (PollenLocationIndex.DailyInfo dailyInfo : pollenLocationIndex.getValue().getDailyInfo()) {
                    int year = dailyInfo.getDate().getYear();
                    int month = dailyInfo.getDate().getMonth() - 1;
                    int day = dailyInfo.getDate().getDay();
                    calendar.set(year, month, day);
                    Date dailyDate = calendar.getTime();

                    if (ApplicationState.isSameDay(dailyDate, date)) {

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
        }
        return PollenShortDTOs;
    }

    //TODO : Change for an ID
    // getIndex for a specific Location and date
    public List<PollenInfoDTO> getIndex(String stringDate, @NotNull Location location) {
        Date date = parseDate(stringDate);
        logger.log( Level.INFO, "Retrieving pollen for a specific Location for the following day: {0}...", date);
        Calendar calendar = Calendar.getInstance();
        List<PollenInfoDTO> PollenShortDTOs = new ArrayList<>();
        for (Map.Entry<Long, PollenLocationIndex> pollenLocationIndex : pollenLocationIndexService.getPollenLocationIndexMap().entrySet()) {
            if(pollenLocationIndex.getValue().getLocation().equals(location)){

                for (PollenLocationIndex.DailyInfo dailyInfo : pollenLocationIndex.getValue().getDailyInfo()) {
                    int year = dailyInfo.getDate().getYear();
                    int month = dailyInfo.getDate().getMonth() - 1;
                    int day = dailyInfo.getDate().getDay();
                    calendar.set(year, month, day);
                    Date dailyDate = calendar.getTime();

                    if (ApplicationState.isSameDay(dailyDate, date)) {
                        for (PollenLocationIndex.PollenTypeInfo pollenTypeDailyInfo : dailyInfo.getPollenTypeInfo()) {
                            if (pollenTypeDailyInfo.getIndexInfo() != null) {
                                        PollenShortDTOs.add(new PollenInfoDTO(pollenTypeDailyInfo));
                            }
                        }

                        for (PollenLocationIndex.PlantInfo pollenDailyInfo : dailyInfo.getPlantInfo()) {
                            if (pollenDailyInfo.getIndexInfo() != null) {
                                        PollenShortDTOs.add(new PollenInfoDTO(pollenDailyInfo));
                            }
                        }
                    }
                }
            }
        }
        return PollenShortDTOs;
    }

    //TODO : Change for an ID
    // getIndex for a specific Pollen, Location and date
    public List<PollenInfoDTO> getIndex(String stringDate, @NotNull Location location, @NotNull Pollen pollen) {
        Date date = parseDate(stringDate);
        logger.log( Level.INFO, "Retrieving info on " + new PollenDTO(pollen) + " for the following day: {0}...", date);
        Calendar calendar = Calendar.getInstance();
        List<PollenInfoDTO> PollenShortDTOs = new ArrayList<>();
        for (Map.Entry<Long, PollenLocationIndex> pollenLocationIndex : pollenLocationIndexService.getPollenLocationIndexMap().entrySet()) {
            if(pollenLocationIndex.getValue().getLocation().equals(location)){

                for (PollenLocationIndex.DailyInfo dailyInfo : pollenLocationIndex.getValue().getDailyInfo()) {
                    int year = dailyInfo.getDate().getYear();
                    int month = dailyInfo.getDate().getMonth() - 1;
                    int day = dailyInfo.getDate().getDay();
                    calendar.set(year, month, day);
                    Date dailyDate = calendar.getTime();

                    if (ApplicationState.isSameDay(dailyDate, date)) {
                        for (PollenLocationIndex.PollenTypeInfo pollenTypeDailyInfo : dailyInfo.getPollenTypeInfo()) {
                            if (pollen.getPollenNameEN().equals(pollenTypeDailyInfo.getDisplayName())) {
                                if (pollenTypeDailyInfo.getIndexInfo() != null) {
                                    PollenShortDTOs.add(new PollenInfoDTO(pollenTypeDailyInfo));
                                }
                            }
                        }

                        for (PollenLocationIndex.PlantInfo pollenDailyInfo : dailyInfo.getPlantInfo()) {
                            if (pollen.getPollenNameEN().equals(pollenDailyInfo.getDisplayName())) {
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
