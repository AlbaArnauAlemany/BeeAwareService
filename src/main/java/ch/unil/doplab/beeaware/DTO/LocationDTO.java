package ch.unil.doplab.beeaware.DTO;

import ch.unil.doplab.beeaware.Domain.Location;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LocationDTO {
    private String country;
    private int NPA;
    public LocationDTO(Location location) {
        this.NPA = location.getNPA();
        this.country = location.getCountry();
    }
}
