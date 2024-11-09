package ch.unil.doplab.beeaware.DTO;

import ch.unil.doplab.beeaware.Domain.Pollen;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AllergenDTO {
    List<PollenDTO> pollenList;

    public AllergenDTO(Map<Long, Pollen> allergens){
        for (Map.Entry<Long, Pollen> pollen: allergens.entrySet()) {
            PollenDTO pollenDTO = new PollenDTO((Pollen) pollen);
            pollenList.add(pollenDTO);
        }
    }
}
