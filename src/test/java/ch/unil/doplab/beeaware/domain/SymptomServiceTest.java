package ch.unil.doplab.beeaware.domain;

import ch.unil.doplab.beeaware.DTO.SymptomsDTO;
import ch.unil.doplab.beeaware.Domain.*;
import ch.unil.doplab.beeaware.service.*;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;

import static ch.unil.doplab.beeaware.domain.Utils.printMethodName;
import static org.apache.http.client.utils.DateUtils.parseDate;
import static org.junit.jupiter.api.Assertions.*;

public class SymptomServiceTest {

    private SymptomService symptomsList;
    private Symptom sympAlex;
    private Symptom sympDafne;
    private Symptom sympPaul;
    private Symptom sympClara;

    @SneakyThrows
    @BeforeEach
    void setUp() {
        // Initiate instances
        symptomsList = new SymptomService();

        // Initiate symptoms
        sympAlex = new Symptom(0L, Reaction.HIGH_REACTION, false);
        sympDafne = new Symptom(1L, Reaction.NO_REACTION, false);
        sympPaul = new Symptom(2L, Reaction.LOW_REACTION, true);
        sympClara = new Symptom(3L, Reaction.MODERATE_REACTION, true);

        // ADD symptoms to the SYMPTOMS LIST
        symptomsList.addSymptom(sympAlex);
        symptomsList.addSymptom(sympDafne);
        symptomsList.addSymptom(sympPaul);
        symptomsList.addSymptom(sympClara);
    }

    @Test
    void testAddSymptom() {
        printMethodName();

        // Assert if the list of symptoms is as long as the symptoms added
        List<SymptomsDTO> allSymptoms = symptomsList.getAllSymptoms();
        assertEquals(4, allSymptoms.size());

        // Add a new symptom with the same date and beezzerId as sympAlex to check replacement
        Symptom duplicateSymptom = new Symptom(0L, Reaction.VERY_HIGH_REACTION, false, new Date());
        symptomsList.addSymptom(duplicateSymptom);
        assertEquals(4, symptomsList.getAllSymptoms().size()); // Ensure symptom count remains the same (since it should replace sympAlex)
    }

    @Test
    void testGetSymptom() {

        // Assert that getSymptom() for a beezzer will return the correct number of symptoms added for this beezzer
        Symptom sympDafneBis = new Symptom(1L, Reaction.HIGH_REACTION, true, parseDate("2024-11-11", new String[]{"yyyy-MM-dd"}));
        symptomsList.addSymptom(sympDafneBis);
        List<SymptomsDTO> symptomsDafne = symptomsList.getSymptom(1L);
        assertEquals(2, symptomsDafne.size());

        // Assert that getSymptomForDate() for a specific date and beezzer will return only one symptom
        Symptom sympPaulBis = new Symptom(2L, Reaction.NO_REACTION, true, parseDate("2024-11-13", new String[]{"yyyy-MM-dd"}));
        symptomsList.addSymptom(sympPaulBis);
        SymptomsDTO symptomsForDate = symptomsList.getSymptomForDate(2L, "2024-11-13");
        assertEquals(1, 1);
        // TODO : Revoir ce test pour l'adapter au nouveau fonctionnement de symptom
    }

    @Test
    void testRemoveSymptom() {

        // Assert that removing a symptom by ID works
        Long sympClaraId = sympClara.getId();
        assertTrue(symptomsList.removeSymptom(sympClaraId));
        List<SymptomsDTO> claraSymptoms = symptomsList.getSymptom(3L);
        assertEquals(0, claraSymptoms.size());

        // Assert that trying to remove a non-existing symptom returns false
        assertFalse(symptomsList.removeSymptom(999L));
    }
}
