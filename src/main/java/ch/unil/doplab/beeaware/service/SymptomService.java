package ch.unil.doplab.beeaware.service;

import ch.unil.doplab.beeaware.Domain.DTO.SymptomsDTO;
import ch.unil.doplab.beeaware.Domain.Symptom;
import ch.unil.doplab.beeaware.Utilis.Utils;
import ch.unil.doplab.beeaware.repository.SymptomRepository;
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
public class SymptomService {
    private Logger logger = Logger.getLogger(SymptomService.class.getName());
    @Inject
    private SymptomRepository symptomRepository;

    public void addSymptom(@NotNull Symptom symptom) {
        symptomRepository.addSymptom(symptom);
    }

    public List<SymptomsDTO> getAllSymptoms() {
        logger.log( Level.INFO, "Searching for all registered symptoms...");
        List<Symptom> symptoms = symptomRepository.findAll();
        List<SymptomsDTO> symptomsList = new ArrayList<>();
        for (Symptom sym: symptoms) {
                symptomsList.add(new SymptomsDTO(sym));
        }
        return symptomsList;
    }

    public List<SymptomsDTO> getSymptom(@NotNull Long beezzerId) {
        List<Symptom> symptoms = symptomRepository.findAllForSpecificBeezzer(beezzerId);
        List<SymptomsDTO> symptomsList = new ArrayList<>();
        if(symptoms != null && !symptoms.isEmpty()){
            for (Symptom sym: symptoms) {
                symptomsList.add(new SymptomsDTO(sym));
            }
            return symptomsList;
        } return null;
    }

    public SymptomsDTO getSymptomForDate(@NotNull Long beezzerId, String stringDate) {
        try {
            List<Symptom> symptoms = symptomRepository.findAllForSpecificBeezzer(beezzerId);
            Date date = parseDate(stringDate);
            logger.log(Level.INFO, "Searching symptom for Beezzer {0} for the following day: {1}...", new Object[]{String.valueOf(beezzerId), String.valueOf(date)});
            for (Symptom sym : symptoms) {
                if (beezzerId.equals(sym.getBeezzerId()) && Utils.isSameDate(sym.getDate(), date)) {
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
            List<Symptom> symptoms = symptomRepository.findAllForSpecificBeezzer(beezzerId);
            List<SymptomsDTO> symptomsDto = new ArrayList<>();
            Date dateFrom = parseDate(stringDateFrom);
            Date dateTo = parseDate(stringDateTo);
            logger.log(Level.INFO, "Searching symptom for Beezzer {0} between {1} and {2}...", new Object[]{String.valueOf(beezzerId), stringDateFrom, stringDateTo});
            for (Symptom sym : symptoms) {
                if (beezzerId.equals(sym.getBeezzerId()) && Utils.isDateAfter(sym.getDate(), dateFrom) && isDateBefore(sym.getDate(), dateTo)) {
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
        Symptom symptom = symptomRepository.findAllForSpecificBeezzerAndId(beezzerId, idSymptom);
        return new SymptomsDTO(symptom);

    }

    //TODO REMOVE SYMPTOM
//    public void removeSymptomsForBeezzer(@NotNull Long beezzerId) {
//        logger.log(Level.INFO, "Removing Symptom for Beezzer {0}...", beezzerId);
//        for (Symptom sym : symptoms.values()) {
//            if (sym.getBeezzer().getId().equals(beezzerId)) {
//                removeSymptom(sym.getId());
//                return;
//            }
//        }
//    }

//    public boolean removeSymptom(Long idSymptom) {
//        var symptom = symptoms.get(idSymptom);
//        logger.log(Level.INFO, "Removing Symptom...");
//        if (symptom == null) {
//            logger.log(Level.WARNING, "Symptom with ID {0} doesn't exist.", idSymptom);
//            return false;
//
//        }
//        var symptomDTO = new SymptomsDTO(symptom);
//        symptoms.remove(idSymptom);
//        logger.log(Level.INFO, "Symptom deleted : {0}", symptomDTO);
//        return true;
//    }
}
