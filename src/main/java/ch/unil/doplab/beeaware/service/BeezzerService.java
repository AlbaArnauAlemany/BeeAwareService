package ch.unil.doplab.beeaware.service;

import ch.unil.doplab.beeaware.Domain.Beezzer;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Getter
@Setter
public class BeezzerService {
    private Long idBeezzer = 0L;
    private final Map<Long, Beezzer> beezzers = new HashMap<>();
    private Logger logger = Logger.getLogger(BeezzerService.class.getName());

    public void addBeezzer(Beezzer beezzer){
        for (Map.Entry<Long, Beezzer> bee: beezzers.entrySet()) {
            if (beezzer.getUsername() != null &&
                    bee.getValue().getUsername() != null &&
                    beezzer.getUsername().equals(bee.getValue().getUsername())) {
                logger.log( Level.WARNING, "Beezzer already exists: {0}", beezzer);
                return;
            }
        }
        beezzer.setId(idBeezzer++);
        beezzers.put(beezzer.getId(), beezzer);
        logger.log( Level.INFO, "New beezzer added : {0}", beezzer);
    }
}
