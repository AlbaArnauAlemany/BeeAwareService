package ch.unil.doplab.beeaware.service;

import ch.unil.doplab.beeaware.DTO.SymptomsDTO;
import ch.unil.doplab.beeaware.Domain.Beezzer;
import ch.unil.doplab.beeaware.Domain.Symptom;
import ch.unil.doplab.beeaware.domain.ApplicationState;
import ch.unil.doplab.beeaware.domain.Utilis;
import lombok.Getter;
import lombok.Setter;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@Getter
@Setter
public class SymptomService {
    private Long idSymptom = 0L;
    private static final Map<Long, Symptom> symptoms = new HashMap<>();
    private Logger logger = Logger.getLogger(SymptomService.class.getName());

    public void addSymptom(@NotNull Symptom symptom){
        SymptomsDTO symptomsDTO = new SymptomsDTO(symptom);
        logger.log( Level.INFO, "Adding symptom....", symptomsDTO);
        for (Map.Entry<Long, Symptom> sym: symptoms.entrySet()) {
            if (Utilis.isSameDay(sym.getValue().getDate(), symptom.getDate()) && sym.getValue().getBeezzerId() == symptom.getBeezzerId()) {
                symptom.setId(sym.getValue().getId());
                symptoms.put(sym.getValue().getId(), symptom);
                logger.log(Level.INFO, "Symptom replaced : {0}", symptomsDTO);
                return;
            }
        }
        symptom.setId(idSymptom);
        symptoms.put(idSymptom++, symptom);
        logger.log(Level.INFO, "Symptom added : {0}", symptomsDTO);
    }

    public List<SymptomsDTO> getSymptoms(@NotNull Long beezzerId) {
        logger.log( Level.INFO, "Searching for Beezzer {0} symptoms...", beezzerId);
        List<SymptomsDTO> symptomsBeezzer = new ArrayList<>();
        for (Map.Entry<Long, Symptom> sym: symptoms.entrySet()) {
            if (beezzerId.equals(sym.getValue().getBeezzerId())) {
                symptomsBeezzer.add(new SymptomsDTO(sym.getValue()));
                logger.log( Level.INFO, "Symptom : {0}", symptomsBeezzer.get(symptomsBeezzer.size()-1));
            }
        }
        return symptomsBeezzer;
    }

    public List<SymptomsDTO> getSymptoms(@NotNull Long beezzerId, @NotNull Date date){
        logger.log( Level.INFO, "Searching for Beezzer " + beezzerId + " for following day: " + date + "...");
        List<SymptomsDTO> symptomsDate = new ArrayList<>();
        for (Map.Entry<Long, Symptom> sym: symptoms.entrySet()) {
            if (beezzerId.equals(sym.getValue().getBeezzerId())
                    && Utilis.isSameDay(sym.getValue().getDate(), date)) {
                symptomsDate.add(new SymptomsDTO(sym.getValue()));
            }
        }
        return symptomsDate;
    }

    public boolean removeSymptom(Long idSymptom) {
        var symptom = symptoms.get(idSymptom);
        var symptomDTO = new SymptomsDTO(symptom);
        logger.log( Level.INFO, "Removing Symptom...");
        if (symptom == null) {
            logger.log( Level.WARNING, "Symptom with ID {0} doesn't exist.", idSymptom);
            return false;

        }
        symptoms.remove(idSymptom);
        logger.log( Level.INFO, "Symptom deleted : {0}", symptomDTO);
        return true;
    }
}
