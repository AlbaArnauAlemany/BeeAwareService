package ch.unil.doplab.beeaware.service;

import ch.unil.doplab.beeaware.Domain.Beezzer;
import ch.unil.doplab.beeaware.Domain.Pollen;
import lombok.Getter;
import lombok.Setter;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@Getter
@Setter
public class AllergenService {
    private Long idAllergen = 0L;
    private final Map<Long, Pollen> allergens = new HashMap<>();
    private Logger logger = Logger.getLogger(AllergenService.class.getName());

    // TODO : Change to a table of allergen and specify an ID
    /**
     * Adds a specific pollen allergen to the Beezzer's list of allergens
     * This method checks if the provided pollen and beezzer are not null and if it is part of
     * the predefined pollens available in the Beezzer's country. If both conditions
     * are met, the pollen id and the Beezzer id are added to the allergens set. If the pollen is null
     * or not available, an IllegalArgumentException is thrown.
     *
     * @param pollen The pollen allergen to be added. It must be a predefined pollen available in the Beezzer's country.
     * @param beezzer The beezzer initiator of this operation
     * @throws IllegalArgumentException If the pollen is null or not available in the Beezzer's country.
     */
    public void addAllergen(@NonNull Pollen pollen, @NonNull Beezzer beezzer) {
        if (!Pollen.getPredefinedPollens().contains(pollen)) {
            throw new IllegalArgumentException("This pollen is not available in your country.");
        }
        if (beezzer.getAllergens().containsKey(pollen.getId())) {
            throw new IllegalArgumentException("This allergen is already saved to your list.");
        }
        beezzer.getAllergens().put(pollen.getId(), pollen);
    }
}
