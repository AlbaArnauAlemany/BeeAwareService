package ch.unil.doplab.beeaware.DTO;

import ch.unil.doplab.beeaware.Domain.Pollen;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PollenDTO {
    private String pollenNameEN;

    public PollenDTO(Pollen pollen) {
        this.pollenNameEN = pollen.getPollenNameEN();
    }

}
