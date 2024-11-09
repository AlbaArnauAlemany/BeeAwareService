package ch.unil.doplab.beeaware.DTO;

import ch.unil.doplab.beeaware.Domain.Beezzer;
import ch.unil.doplab.beeaware.Domain.Pollen;
import lombok.Getter;
import lombok.Setter;
import java.util.Map;

@Getter
@Setter
public class BeezzerDTO {
    private String username;
    private String email;
    private LocationDTO location;
    private AllergenDTO allergens;

    public BeezzerDTO(Beezzer beezzer){
        this.username = beezzer.getUsername();
        this.email = beezzer.getEmail();
        this.location = new LocationDTO(beezzer.getLocation());
        this.allergens = new AllergenDTO(beezzer.getAllergens());
    }
}
