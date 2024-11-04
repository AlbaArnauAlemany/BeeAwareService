package ch.unil.doplab.beeaware.service;

import ch.unil.doplab.beeaware.Domain.Beezzer;
import ch.unil.doplab.beeaware.Domain.Symptom;
import lombok.Getter;
import lombok.Setter;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Getter
@Setter
public class SymptomService {
    private Long idSymptom = 0L;
    private final Map<Long, Symptom> symptoms = new HashMap<>();

    public void addSymptom(@NotNull Symptom symptom){
        Date todayDate = new Date();
        symptom.setDate(todayDate);
        for (Map.Entry<Long, Symptom> sym: symptoms.entrySet()) {
            if (isSameDay(sym.getValue().getDate(), todayDate)) {
                symptom.setId(sym.getValue().getId());
                symptoms.put(sym.getValue().getId(), symptom);
                return;
            }
        }
        symptom.setId(idSymptom++);
        symptoms.put(symptom.getBeezzerId(), symptom);
    }

    public void addSymptom(@NotNull Symptom symptom, @NotNull Date date){
        symptom.setDate(date);
        for (Map.Entry<Long, Symptom> sym: symptoms.entrySet()) {
            if (isSameDay(sym.getValue().getDate(), date)) {
                symptom.setId(sym.getValue().getId());
                symptoms.put(sym.getValue().getId(), symptom);
                return;
            }
        }
        symptom.setId(idSymptom++);
        symptoms.put(symptom.getBeezzerId(), symptom);
    }

    public List<Symptom> getSymptomsForASpecificBeezzer(@NotNull Long id) {
        List<Symptom> symptomsBeezzer = new ArrayList<>();
        for (Map.Entry<Long, Symptom> sym: symptoms.entrySet()) {
            if (id == sym.getValue().getBeezzerId()) {
                symptomsBeezzer.add(sym.getValue());
            }
        }
        return symptomsBeezzer;
    }

    public List<Symptom> getSymptomForASpecificDate(@NotNull Long beezzerId, @NotNull Date date){
        List<Symptom> symptomsDate = new ArrayList<>();
        for (Map.Entry<Long, Symptom> sym: symptoms.entrySet()) {
            if (beezzerId == sym.getValue().getBeezzerId()
                    && isSameDay(sym.getValue().getDate(), date)) {
                symptomsDate.add(sym.getValue());
            }
        }
        return symptomsDate;
    }

    public static boolean isSameDay(@NotNull Date date1, @NotNull Date date2) {
        LocalDate localDate1 = date1.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        LocalDate localDate2 = date2.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        return localDate1.isEqual(localDate2);
    }
}
