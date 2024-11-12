package ch.unil.doplab.beeaware.DTO;

import ch.unil.doplab.beeaware.Domain.Pollen;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AllergenDTO {
    List<PollenDTO> pollenList = new ArrayList<>();

    // TODO: if you want a specific allergen use PollenDTO
    public AllergenDTO(Map<Long, Pollen> allergens) {
        for (Map.Entry<Long, Pollen> pollen : allergens.entrySet()) {
            PollenDTO pollenDTO = new PollenDTO(pollen.getValue());
            pollenList.add(pollenDTO);
        }
    }
}
