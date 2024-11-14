package ch.unil.doplab.beeaware.DTO;

import ch.unil.doplab.beeaware.Domain.Reaction;
import ch.unil.doplab.beeaware.Domain.Symptom;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SymptomsDTO {
    private Reaction level;
    private boolean antihistamine;
    private Date date;

    public SymptomsDTO(Symptom symptom) {
        this.level = symptom.getReaction();
        this.antihistamine = symptom.isAntihistamine();
        this.date = symptom.getDate();
    }

    @Override
    public String toString() {
        return "Date : " + date + ", Level of reaction: " + level + "\n" + "Antihistamine taken? " + antihistamine;
    }
}
