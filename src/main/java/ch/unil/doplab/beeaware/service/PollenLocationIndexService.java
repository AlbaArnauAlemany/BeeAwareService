package ch.unil.doplab.beeaware.service;

import ch.unil.doplab.beeaware.Domain.PollenLocationIndex;
import ch.unil.doplab.beeaware.domain.Utilis;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Getter
@Setter
// TODO: Mettre en Admin
public class PollenLocationIndexService {
    private Long idPollenLocationIndex = 0L;
    private Map<Long, PollenLocationIndex> pollenLocationIndexMap = new HashMap<>();
    private Logger logger = Logger.getLogger(PollenLocationIndexService.class.getName());

    public void addPollenLocationIndex(@NotNull PollenLocationIndex pollenLocationIndex) {
        for (Map.Entry<Long, PollenLocationIndex> pil : pollenLocationIndexMap.entrySet()) {
            if (pil.getValue().getLocation() != null && pil.getValue().getLocation().equals(pollenLocationIndex.getLocation())) {
                logger.log(Level.WARNING, "pollenLocationIndex already exists: {0}", pollenLocationIndex);
                if (Utilis.isSameDay(Utilis.formatDate(pil.getValue().getDailyInfo().get(0).getDate()), Utilis.formatDate(pollenLocationIndex.getDailyInfo().get(0).getDate()))) {
                    pollenLocationIndexMap.put(pil.getValue().getId(), pollenLocationIndex);
                    return;
                }
            }
        }
        pollenLocationIndex.setId(idPollenLocationIndex++);
        pollenLocationIndexMap.put(idPollenLocationIndex, pollenLocationIndex);
        logger.log(Level.INFO, "New pollenLocationIndex added : {0}", pollenLocationIndex);
    }

    public boolean removePollenLocationIndex(Long idPollenLocationIndex) {
        var pollenLocationIndex = pollenLocationIndexMap.get(idPollenLocationIndex);
        logger.log(Level.INFO, "Removing PollenLocationIndex...");
        if (pollenLocationIndex == null) {
            logger.log(Level.WARNING, "PollenLocationIndex with ID {0} doesn't exist.", idPollenLocationIndex);
            return false;

        }
        pollenLocationIndexMap.remove(idPollenLocationIndex);
        logger.log(Level.INFO, "PollenLocationIndex deleted: {0}", idPollenLocationIndex);
        return true;
    }
}

