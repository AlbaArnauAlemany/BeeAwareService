package ch.unil.doplab.beeaware.service;

import ch.unil.doplab.beeaware.Domain.DTO.PollenInfoDTO;
import ch.unil.doplab.beeaware.Domain.PollenLocationIndex;
import ch.unil.doplab.beeaware.Domain.PollenLocationInfo;
import ch.unil.doplab.beeaware.Utilis.Utils;
import ch.unil.doplab.beeaware.repository.PollenLocationIndexRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Getter
@Setter
@ApplicationScoped
public class PollenLocationIndexService {
    @Inject
    PollenLocationIndexRepository pollenLocationIndexRepository;
    private Logger logger = Logger.getLogger(PollenLocationIndexService.class.getName());

    public void addPollenLocationIndex(@NotNull PollenLocationIndex pollenLocationIndex) {
        List<PollenLocationIndex> pollenLocationIndexList = pollenLocationIndexRepository.findAll();
        for (PollenLocationIndex pil : pollenLocationIndexList) {
            if (pil.getLocation() != null && pil.getLocation().equals(pollenLocationIndex.getLocation()) && pil.getDisplayName().equals(pollenLocationIndex.getDisplayName())) {
                if (Utils.isSameDate(pil.getDate(), pollenLocationIndex.getDate())) {
                    pollenLocationIndexRepository.updatePollenLocationIndex(pollenLocationIndex);
                    return;
                }
            }
        }
        pollenLocationIndexRepository.addPollenLocationIndex(pollenLocationIndex);
        logger.log(Level.INFO, "New pollenLocationIndex added : {0}", pollenLocationIndex);
    }

    public List<PollenInfoDTO> findAllPollenIndexLocation(){
        List<PollenLocationIndex> pollenInfo = pollenLocationIndexRepository.findAll();;
        List<PollenInfoDTO> pollenInfoDTOs = new ArrayList<>();
        for (PollenLocationIndex pollenLocationIndex : pollenInfo) {
            pollenInfoDTOs.add(new PollenInfoDTO(pollenLocationIndex));
        }
        return pollenInfoDTOs;
    }

    public boolean removePollenLocationIndex(Long idPollenLocationIndex) {
        var pollenLocationIndex = pollenLocationIndexRepository.findById(idPollenLocationIndex);
        logger.log(Level.INFO, "Removing PollenLocationIndex...");
        if (pollenLocationIndex == null) {
            logger.log(Level.WARNING, "PollenLocationIndex with ID {0} doesn't exist.", idPollenLocationIndex);
            return false;

        }
//        pollenLocationIndexMap.remove(idPollenLocationIndex);
        logger.log(Level.INFO, "PollenLocationIndex deleted: {0}", idPollenLocationIndex);
        return true;
    }
}

