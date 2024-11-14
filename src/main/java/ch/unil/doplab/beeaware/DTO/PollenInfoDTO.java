package ch.unil.doplab.beeaware.DTO;

import ch.unil.doplab.beeaware.Domain.Location;
import ch.unil.doplab.beeaware.Domain.PollenLocationIndex;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PollenInfoDTO {
    private String displayName;
    private int index;
    private Date date;
    private int NPA;

    public PollenInfoDTO(PollenLocationIndex pollenLocationIndex) {
        this.displayName = pollenLocationIndex.getDisplayName();
        this.index = pollenLocationIndex.getIndex();
        this.date = pollenLocationIndex.getDate();
        this.NPA = pollenLocationIndex.getLocation().getNPA();
    }

    @Override
    public String toString() {
        return "Name : " + displayName + ", Index : " + index + ", Date : " + date + ", NPA : " + NPA;
    }
}
