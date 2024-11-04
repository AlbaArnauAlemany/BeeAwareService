package ch.unil.doplab.beeaware.DTO;

import ch.unil.doplab.beeaware.Domain.Pollen;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class AllergenDTO {
    List<Pollen> pollenList;

    public AllergenDTO(Map<Long, Pollen> allergens){
        for (Map.Entry<Long, Pollen> pollen: allergens.entrySet()) {
            pollenList.add(pollen.getValue());
        }
    }
}
