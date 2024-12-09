package ch.unil.doplab.beeaware.service;

import ch.unil.doplab.beeaware.Domain.DTO.SymptomsDTO;
import ch.unil.doplab.beeaware.Domain.Symptom;
import ch.unil.doplab.beeaware.Utilis.Utils;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static ch.unil.doplab.beeaware.Utilis.Utils.isDateBefore;
import static ch.unil.doplab.beeaware.Utilis.Utils.parseDate;

@Getter
@Setter
public class SymptomService {
    private final Map<Long, Symptom> symptoms = new HashMap<>();
    private Long idSymptom = 0L;
    private Logger logger = Logger.getLogger(SymptomService.class.getName());

    public boolean addSymptom(@NotNull Symptom symptom) {
        if (symptom.getDate() == null) {
            symptom.setDate(new Date());
        }
        SymptomsDTO symptomsDTO = new SymptomsDTO(symptom);
        logger.log(Level.INFO, "Adding symptom {0}....", symptomsDTO);
        for (Map.Entry<Long, Symptom> sym : symptoms.entrySet()) {
            if (Utils.isSameDate(sym.getValue().getDate(), symptom.getDate()) && sym.getValue().getBeezzer().getId().equals(symptom.getBeezzer().getId())) {
                symptom.setId(sym.getValue().getId());
                symptoms.put(sym.getValue().getId(), symptom);
                logger.log(Level.INFO, "Symptom replaced : {0}", symptomsDTO);
                return true;
            }
        }
        symptom.setId(idSymptom);
        symptoms.put(idSymptom++, symptom);
        logger.log(Level.INFO, "Symptom added : {0}", symptomsDTO);
        return true;
    }

    public List<SymptomsDTO> getAllSymptoms() {
        logger.log( Level.INFO, "Searching for all registered symptoms...");
        List<SymptomsDTO> symptomsList = new ArrayList<>();
        for (Symptom sym: symptoms.values()) {
                symptomsList.add(new SymptomsDTO(sym));
        }
        return symptomsList;
    }

    public List<SymptomsDTO> getSymptom(@NotNull Long beezzerId) {
        logger.log(Level.INFO, "Searching for Beezzer {0} symptoms...", beezzerId);
        List<SymptomsDTO> symptomsBeezzer = new ArrayList<>();
        for (Symptom sym : symptoms.values()) {
            if (sym.getBeezzer().getId() == beezzerId) {
                symptomsBeezzer.add(new SymptomsDTO(sym));
                logger.log(Level.INFO, "Symptom : {0}", symptomsBeezzer.get(symptomsBeezzer.size() - 1));
            }
        }
        return symptomsBeezzer;
    }

    public SymptomsDTO getSymptomForDate(@NotNull Long beezzerId, String stringDate) {
        try {
            Date date = parseDate(stringDate);
            logger.log(Level.INFO, "Searching symptom for Beezzer {0} for the following day: {1}...", new Object[]{String.valueOf(beezzerId), String.valueOf(date)});
            for (Symptom sym : symptoms.values()) {
                if (beezzerId.equals(sym.getBeezzer().getId()) && Utils.isSameDate(sym.getDate(), date)) {
                    return new SymptomsDTO(sym);
                }
            }
            logger.log(Level.WARNING, "Beezzer or date not found");
            return null;
        } catch (Exception e){
            logger.log(Level.WARNING, "Error to get symptoms from this date\n{0}\n{1}...", new Object[]{e.getMessage(), e.getStackTrace()});
            return null;
        }
    }

    public List<SymptomsDTO> getSymptomForRange(@NotNull Long beezzerId, String stringDateFrom, String stringDateTo) {
        try {
            List<SymptomsDTO> symptomsDto = new ArrayList<>();
            Date dateFrom = parseDate(stringDateFrom);
            Date dateTo = parseDate(stringDateTo);
            logger.log(Level.INFO, "Searching symptom for Beezzer {0} between {1} and {2}...", new Object[]{String.valueOf(beezzerId), stringDateFrom, stringDateTo});
            for (Symptom sym : symptoms.values()) {
                if (beezzerId.equals(sym.getBeezzer().getId()) && Utils.isDateAfter(sym.getDate(), dateFrom) && isDateBefore(sym.getDate(), dateTo)) {
                    symptomsDto.add(new SymptomsDTO(sym));
                }
            }
            return symptomsDto;
        } catch (Exception e){
            logger.log(Level.WARNING, "Error to get symptoms from this range\n{0}\n{1}...", new Object[]{e.getMessage(), e.getStackTrace()});
            return null;
        }
    }

    public SymptomsDTO getSymptom(@NotNull Long beezzerId, Long idSymptom){
        logger.log( Level.INFO, "Searching symptom for Beezzer {0} symptom: {1}...", new Object[]{String.valueOf(beezzerId), idSymptom});
        if (beezzerId.equals(symptoms.get(idSymptom).getBeezzer().getId())) {
            return new SymptomsDTO(symptoms.get(idSymptom));
        }
        return new SymptomsDTO();
    }

    public void removeSymptomsForBeezzer(@NotNull Long beezzerId) {
        logger.log(Level.INFO, "Removing Symptom for Beezzer {0}...", beezzerId);
        for (Symptom sym : symptoms.values()) {
            if (sym.getBeezzer().getId().equals(beezzerId)) {
                removeSymptom(sym.getId());
                return;
            }
        }
    }

    public boolean removeSymptom(Long idSymptom) {
        var symptom = symptoms.get(idSymptom);
        logger.log(Level.INFO, "Removing Symptom...");
        if (symptom == null) {
            logger.log(Level.WARNING, "Symptom with ID {0} doesn't exist.", idSymptom);
            return false;

        }
        var symptomDTO = new SymptomsDTO(symptom);
        symptoms.remove(idSymptom);
        logger.log(Level.INFO, "Symptom deleted : {0}", symptomDTO);
        return true;
    }
}
