package ch.unil.doplab.beeaware.service;

import ch.unil.doplab.beeaware.DTO.PollenInfoDTO;
import ch.unil.doplab.beeaware.Domain.Beezzer;
import ch.unil.doplab.beeaware.Domain.Pollen;
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

import static ch.unil.doplab.beeaware.domain.Utils.parseDate;

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
        for (Map.Entry<Long, PollenLocationIndex> pollenLocationIndex : pollenLocationIndexService.getPollenLocationIndexMap().entrySet()) {
            if (pollenLocationIndex.getValue().getLocation().equals(beezzer.getLocation())) {
                if (date == null || Utils.isSameDate(pollenLocationIndex.getValue().getDate(), date)) {
                    for (Map.Entry<Long, Pollen> pollen : beezzer.getAllergens().entrySet()) {
                        if (pollenLocationIndex.getValue().getDisplayName().equals(pollen.getValue().getPollenNameEN())) {
                            pollenShortDTOs.add(new PollenInfoDTO(pollenLocationIndex.getValue()));
                        }
                    }
                }
            }
        }
        return pollenShortDTOs;
    }

    private List<PollenInfoDTO> pollenInfoDTOList(Long beezzerId){
        return pollenInfoDTOList(beezzerId, (Date) null);
    }

    private List<PollenInfoDTO> pollenInfoDTOList(Long beezzerId, String date) {
        Date dateParsed = null;
        try{
            dateParsed = parseDate(date);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Unable to parse date");
        }
        return pollenInfoDTOList(beezzerId, dateParsed);
    }

    // getIndex for a specific beezzer
    public List<PollenInfoDTO> getIndex(@NotNull Long beezzerId) {
        return pollenInfoDTOList(beezzerId);
    }

    // getIndex for a specific beezzer and date
    public List<PollenInfoDTO> getIndex(String stringDate, @NotNull Long beezzerId) {
        logger.log(Level.INFO, "Retrieving pollen for a specific date {0}", stringDate);
        return pollenInfoDTOList(beezzerId, stringDate);
    }
}
