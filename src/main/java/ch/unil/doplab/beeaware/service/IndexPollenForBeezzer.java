package ch.unil.doplab.beeaware.service;

import ch.unil.doplab.beeaware.Domain.DTO.PollenInfoDTO;
import ch.unil.doplab.beeaware.Domain.Beezzer;
import ch.unil.doplab.beeaware.Domain.Pollen;
import ch.unil.doplab.beeaware.Domain.PollenLocationIndex;
import ch.unil.doplab.beeaware.Utilis.Utils;
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

import static ch.unil.doplab.beeaware.Utilis.Utils.parseDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class IndexPollenForBeezzer {
    private BeezzerService beezzerService;
    private ForeCastService foreCastService;
    private PollenLocationIndexService pollenLocationIndexService;
    private final Logger logger = Logger.getLogger(IndexPollenForBeezzer.class.getName());

    private List<PollenInfoDTO> pollenInfoDTOList(Long beezzerId, Date date){
        logger.log(Level.INFO, "Retrieving pollen for a specific Beezzer {0}", beezzerId);
        Beezzer beezzer = beezzerService.getBeezzers().get(beezzerId);
        List<PollenInfoDTO> pollenShortDTOs = new ArrayList<>();
        for (PollenLocationIndex pollenLocationIndex : pollenLocationIndexService.getPollenLocationIndexMap().values()) {
            if (pollenLocationIndex.getLocation().equals(beezzer.getLocation())) {
                if (date == null || Utils.isSameDate(pollenLocationIndex.getDate(), date)) {
                    for (Pollen pollen : beezzer.getAllergens().values()) {
                        if (pollenLocationIndex.getDisplayName().equals(pollen.getPollenNameEN())) {
                            pollenShortDTOs.add(new PollenInfoDTO(pollenLocationIndex));
                        }
                    }
                }
            }
        }
        return pollenShortDTOs;
    }

    // getIndex for a specific beezzer
    public List<PollenInfoDTO> getIndex(@NotNull Long beezzerId) {
        return pollenInfoDTOList(beezzerId, null);
    }

    // getIndex for a specific beezzer and date
    public List<PollenInfoDTO> getIndexForDate(@NotNull Long beezzerId, String stringDate) {
        Date dateParsed = null;
        try{
            dateParsed = parseDate(stringDate);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Unable to parse date");
            return new ArrayList<>();
        }
        logger.log(Level.INFO, "Retrieving pollen for a specific date {0}", stringDate);
        return pollenInfoDTOList(beezzerId, dateParsed);
    }
}
