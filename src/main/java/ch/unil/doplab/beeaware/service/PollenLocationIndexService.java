package ch.unil.doplab.beeaware.service;

import ch.unil.doplab.beeaware.DTO.PollenInfoDTO;
import ch.unil.doplab.beeaware.Domain.PollenLocationIndex;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Getter
@Setter
public class PollenLocationIndexService {
    private Long idPollenLocationIndex = 0L;
    private Map<Long, PollenLocationIndex> pollenLocationIndexMap = new HashMap<>();
    private Logger logger = Logger.getLogger(PollenLocationIndexService.class.getName());

    public void addPollenLocationIndex(@NotNull PollenLocationIndex pollenLocationIndex) {
        for (Map.Entry<Long, PollenLocationIndex>  pil: pollenLocationIndexMap.entrySet()) {
            if (pil.getValue().getLocation() != null && pil.getValue().getLocation().equals(pollenLocationIndex.getLocation())) {
                logger.log( Level.WARNING, "pollenLocationIndex already exists: {0}", pollenLocationIndex);
                return;
            }
        }
        pollenLocationIndex.setId(idPollenLocationIndex++);
        pollenLocationIndexMap.put(idPollenLocationIndex, pollenLocationIndex);
        logger.log( Level.INFO, "New pollenLocationIndex added : {0}", pollenLocationIndex);
    }
}

