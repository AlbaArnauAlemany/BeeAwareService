package ch.unil.doplab.beeaware.service;

import ch.unil.doplab.beeaware.DTO.PollenInfoDTO;
import ch.unil.doplab.beeaware.Domain.Beezzer;
import ch.unil.doplab.beeaware.Domain.Location;
import ch.unil.doplab.beeaware.Domain.PollenLocationIndex;
import ch.unil.doplab.beeaware.domain.Utils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.apache.http.client.utils.DateUtils.parseDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class IndexPollenForBeezzer {
    private BeezzerService beezzerService;
    private ForeCastService foreCastService;
    private PollenLocationIndexService pollenLocationIndexService;
    private final Logger logger = Logger.getLogger(IndexPollenForBeezzer.class.getName());
    public void forecastAllLocation(Map<Long, Location> locations) {
        logger.log(Level.INFO, "Retrieving pollen per locations....");
        for (Map.Entry<Long, Location> loc : locations.entrySet()) {
            logger.log(Level.INFO, "Location : {0}", loc);
            foreCastService.pollenForecast(loc.getValue(), 1);
        }
    }

    // getIndex for a specific beezzer
    public List<PollenInfoDTO> getIndex(@NotNull Long beezzerId) {
        Beezzer beezzer = beezzerService.getBeezzers().get(beezzerId);
        logger.log(Level.INFO, "Retrieving pollen for a specific Beezzer...");
        List<PollenInfoDTO> PollenShortDTOs = new ArrayList<>();
        for (Map.Entry<Long, PollenLocationIndex> pollenLocationIndex : pollenLocationIndexService.getPollenLocationIndexMap().entrySet()) {
            if (pollenLocationIndex.getValue().getLocation().equals(beezzer.getLocation())) {
                PollenShortDTOs.add(new PollenInfoDTO(pollenLocationIndex.getValue()));

            }
        }
        return PollenShortDTOs;
    }

    // getIndex for a specific beezzer and date
    public List<PollenInfoDTO> getIndex(String stringDate, @NotNull Long beezzerId) {
        Beezzer beezzer = beezzerService.getBeezzers().get(beezzerId);
        Date date = parseDate(stringDate);
        logger.log(Level.INFO, "Retrieving pollen for a specific Beezzer for the following day: {0}...", date);
        List<PollenInfoDTO> PollenShortDTOs = new ArrayList<>();
        for (Map.Entry<Long, PollenLocationIndex> pollenLocationIndex : pollenLocationIndexService.getPollenLocationIndexMap().entrySet()) {
            if (pollenLocationIndex.getValue().getLocation().equals(beezzer.getLocation())) {
                if (Utils.isSameDay(pollenLocationIndex.getValue().getDate(), date)) {
                    PollenShortDTOs.add(new PollenInfoDTO(pollenLocationIndex.getValue()));
                }
            }
        }
        return PollenShortDTOs;
    }
}
