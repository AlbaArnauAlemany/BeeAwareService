package ch.unil.doplab.beeaware.rest;

import ch.unil.doplab.beeaware.Domain.Location;
import ch.unil.doplab.beeaware.Domain.PollenLocationIndex;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import jakarta.inject.Inject;
import jakarta.ws.rs.Path;

import java.io.IOException;

@Path("/pollenindex")
public class PollenIndexResource {
    @Inject
    private ApplicationState state;


}


public static void pollenForecast(Location location, int days) {
    String url = String.format(
            "https://pollen.googleapis.com/v1/forecast:lookup?key=%s&location.longitude=%s&location.latitude=%s&days=%s",
            APIKEY, location.getLongitude(), location.getLatitude(), days);

    try {
        NetHttpTransport httpTransport = new NetHttpTransport();
        HttpRequestFactory requestFactory = httpTransport.createRequestFactory();
        HttpRequest request = requestFactory.buildGetRequest(new com.google.api.client.http.GenericUrl(url));
        HttpResponse response = request.execute();

        String jsonResponse = response.parseAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        PollenLocationIndex pollenInfo = objectMapper.readValue(jsonResponse, PollenLocationIndex.class);
        pollenInfo.setLocation(location);
        pollenInfo.setId(idPollenIndex++);
        addPollenIndexLocation(pollenInfo);
    } catch (IOException e) {
        e.printStackTrace();
    }
}


@Override
public String toString() {
    String pollenIndex = "";

    for (PollenLocationIndex.DailyInfo dailyInfo : dailyInfo) {
        for (PollenLocationIndex.PollenTypeInfo pollenTypeDailyInfo : dailyInfo.getPollenTypeInfo()) {
            if (pollenTypeDailyInfo.getIndexInfo() != null) {
                pollenIndex +=
                        pollenTypeDailyInfo.getDisplayName() + "\n" +
                                pollenTypeDailyInfo.getIndexInfo().getValue() + "\n" +
                                pollenTypeDailyInfo.getIndexInfo().getIndexDescription() + "\n";
            }
        }
        for (PollenLocationIndex.PlantInfo pollenDailyInfo : dailyInfo.getPlantInfo()) {
            if (pollenDailyInfo.getIndexInfo() != null) {
                pollenIndex +=
                        pollenDailyInfo.getDisplayName() + "\n" +
                                pollenDailyInfo.getIndexInfo().getValue() + "\n" +
                                pollenDailyInfo.getIndexInfo().getIndexDescription() + "\n";
            }
        }
    }

    return pollenIndex;
}
