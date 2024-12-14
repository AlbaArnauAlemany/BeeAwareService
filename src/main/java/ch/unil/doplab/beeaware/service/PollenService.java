package ch.unil.doplab.beeaware.service;


import ch.unil.doplab.beeaware.Domain.DTO.PollenDTO;
import ch.unil.doplab.beeaware.Domain.Pollen;
import ch.unil.doplab.beeaware.Utilis.Utils;
import ch.unil.doplab.beeaware.repository.PollenRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static ch.unil.doplab.beeaware.Utilis.Utils.isDateBefore;
import static ch.unil.doplab.beeaware.Utilis.Utils.parseDate;

@Getter
@Setter
@ApplicationScoped
@NoArgsConstructor
public class PollenService {
    private Logger logger = Logger.getLogger(PollenService.class.getName());
    @Inject
    private PollenRepository pollenRepository;

    public void addPollen(@NotNull Pollen pollen) {
        pollenRepository.addPollen(pollen);
    }

    public List<PollenDTO> getAllPollens() {
        logger.log( Level.INFO, "Searching for all registered pollens...");
        List<Pollen> pollens = pollenRepository.findAll();
        List<PollenDTO> pollensList = new ArrayList<>();
        for (Pollen sym: pollens) {
            pollensList.add(new PollenDTO(sym));
        }
        return pollensList;
    }
}
