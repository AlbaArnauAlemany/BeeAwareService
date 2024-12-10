package ch.unil.doplab.beeaware.service;

import ch.unil.doplab.beeaware.Domain.DTO.PollenInfoDTO;
import ch.unil.doplab.beeaware.Domain.Beezzer;
import ch.unil.doplab.beeaware.Domain.Pollen;
import ch.unil.doplab.beeaware.Domain.PollenLocationIndex;
import ch.unil.doplab.beeaware.Utilis.Utils;
import ch.unil.doplab.beeaware.repository.PollenLocationIndexRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static ch.unil.doplab.beeaware.Utilis.Utils.isDateBefore;
import static ch.unil.doplab.beeaware.Utilis.Utils.parseDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class IndexPollenForBeezzer {
    private BeezzerService beezzerService;
    private ForeCastService foreCastService;
    private PollenLocationIndexService pollenLocationIndexService;
    private PollenLocationIndexRepository pollenLocationIndexRepository;
    private final Logger logger = Logger.getLogger(IndexPollenForBeezzer.class.getName());

    public IndexPollenForBeezzer(PollenLocationIndexRepository pollenLocationIndexRepository,BeezzerService beezzerService,ForeCastService foreCastService, PollenLocationIndexService pollenLocationIndexService){

        this.pollenLocationIndexRepository = pollenLocationIndexRepository;
        this.beezzerService = beezzerService;
        this.foreCastService = foreCastService;
        this.pollenLocationIndexService = pollenLocationIndexService;
    }

    private List<PollenInfoDTO> pollenInfoDTOList(Long beezzerId, Date dateFrom, Date dateTo) {
        logger.log(Level.INFO, "Retrieving pollen for a specific Beezzer {0}", beezzerId);
        Beezzer beezzer = beezzerService.getBeezzerRepository().findById(beezzerId);
        List<PollenInfoDTO> pollenShortDTOs = new ArrayList<>();

        for (PollenLocationIndex pollenLocationIndex : pollenLocationIndexService.getPollenLocationIndexMap().values()) {
            if (pollenLocationIndex.getLocation().equals(beezzer.getLocation())) {
                boolean dateCondition = false;

                if (dateFrom == null && dateTo == null) {
                    logger.log(Level.INFO, "No date provided");
                    dateCondition = true;
                } else if (dateFrom != null && dateTo != null) {
                    logger.log(Level.INFO, "Two dates provided : {0}", dateFrom + ", " + dateTo);
                    dateCondition = Utils.isDateAfter(pollenLocationIndex.getDate(), dateFrom) && isDateBefore(pollenLocationIndex.getDate(), dateTo);
                } else if (dateFrom != null) {
                    logger.log(Level.INFO, "One date provided : {0}", dateFrom);
                    dateCondition = Utils.isSameDate(pollenLocationIndex.getDate(), dateFrom);
                }

                logger.log(Level.INFO, "Date of pollen : {0}", pollenLocationIndex.getDate());
                logger.log(Level.INFO, "Condition : {0}", dateCondition);

                if (dateCondition) {
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
        return pollenInfoDTOList(beezzerId, null, null);
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
        return pollenInfoDTOList(beezzerId, dateParsed, null);
    }

    public List<PollenInfoDTO> getIndexForRangeDate(@NotNull Long beezzerId, String stringDate1, String stringDate2) {
        Date dateFrom = null;
        Date dateTo = null;
        try{
            dateFrom = parseDate(stringDate1);
            dateTo = parseDate(stringDate2);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Unable to parse dates");
            return new ArrayList<>();
        }
        logger.log(Level.INFO, "Retrieving pollen for a specific date {0}", stringDate1 + ", " + stringDate2);
        return pollenInfoDTOList(beezzerId, dateFrom, dateTo);
    }
}
