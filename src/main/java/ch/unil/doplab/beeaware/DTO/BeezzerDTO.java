package ch.unil.doplab.beeaware.DTO;

import ch.unil.doplab.beeaware.Domain.Pollen;
import ch.unil.doplab.beeaware.Domain.Location;
import lombok.Getter;
import lombok.Setter;
import java.util.Map;

@Getter
@Setter
public class BeezzerDTO {
    private String username;
    private String email;
    private Location location;
    private Map<Long, Pollen> allergens;

    public BeezzerDTO(String username, String email, Location location, Map<Long, Pollen> allergens){
        this.username = username;
        this.email = email;
        this.location = location;
        this.allergens = allergens;
    }
}
