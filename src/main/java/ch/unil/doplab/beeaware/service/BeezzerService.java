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

    public Beezzer addBeezzer(Beezzer beezzer){
        for (Map.Entry<Long, Beezzer> bee: beezzers.entrySet()) {
            if (beezzer.getUsername() != null &&
                    bee.getValue().getUsername() != null &&
                    beezzer.getUsername().equals(bee.getValue().getUsername())) {
                throw new IllegalArgumentException("Username " + beezzer.getUsername() + " already used. Please try a new one.");
            }
        }
        if (beezzer.getId() == null) {
            Long newId = idBeezzer++;
            beezzers.put(newId, beezzer);
            beezzer.setId(newId);
        } else {
            beezzers.put(beezzer.getId(), beezzer);
        }
        return beezzer;
    }

    public Beezzer getBeezzer(Long id) { return beezzers.get(id); }

    public Map<Long, Beezzer> getAllBeezzers() { return beezzers; }

    public void setBeezzer(Beezzer beezzer) {
        beezzers.put(beezzer.getId(), beezzer);
    }
   public boolean removeBeezzer(Long id) {
        var beezzer = beezzers.get(id);
        if (beezzer == null) {
            return false;
        }
        beezzers.remove(id);
        return true;
    }
}
