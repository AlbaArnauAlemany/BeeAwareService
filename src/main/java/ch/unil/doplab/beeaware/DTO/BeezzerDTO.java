package ch.unil.doplab.beeaware.DTO;

import ch.unil.doplab.beeaware.Domain.Beezzer;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BeezzerDTO {
    private Long id;
    private String username;
    private String email;
    private LocationDTO location;
    private AllergenDTO allergens;

    public BeezzerDTO(Beezzer beezzer) {
        if (beezzer.getId() != null) {
            this.id = beezzer.getId();
        }
        if (beezzer.getUsername() != null) {
            this.username = beezzer.getUsername();
        }
        if (beezzer.getEmail() != null) {
            this.email = beezzer.getEmail();
        }
        if (beezzer.getLocation() != null) {
            this.location = new LocationDTO(beezzer.getLocation());
        }
        if (beezzer.getAllergens() != null) {
            this.allergens = new AllergenDTO(beezzer.getAllergens());
        }
    }
}
