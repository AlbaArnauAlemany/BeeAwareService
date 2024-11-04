package ch.unil.doplab.beeaware.DTO;

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
    private String recommendation;
    private String crossReaction;
    private Date date;

    public PollenInfoDTO(PollenLocationIndex.PollenTypeInfo pollenTypeDailyInfo){
        this.displayName = pollenTypeDailyInfo.getDisplayName();
        this.index = pollenTypeDailyInfo.getIndexInfo().getValue();
        this.recommendation = pollenTypeDailyInfo.getIndexInfo().getIndexDescription();
        this.crossReaction = "";
    }

    public PollenInfoDTO(PollenLocationIndex.PlantInfo pollenDailyInfo){
        this.displayName = pollenDailyInfo.getDisplayName();
        this.index = pollenDailyInfo.getIndexInfo().getValue();
        this.recommendation = pollenDailyInfo.getIndexInfo().getIndexDescription();
        this.crossReaction = pollenDailyInfo.getPlantDescription().getCrossReaction();
    }

    @Override
    public String toString() {
        return "Name : " + displayName + ", Index : " + index + "\n" + "Recommandation : " + recommendation+ "\n" + "Cross : " + crossReaction;
    }
}
