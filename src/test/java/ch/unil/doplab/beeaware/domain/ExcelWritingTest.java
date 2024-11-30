package ch.unil.doplab.beeaware.domain;

import ch.unil.doplab.beeaware.Domain.DTO.SymptomsDTO;
import ch.unil.doplab.beeaware.Domain.Reaction;
import ch.unil.doplab.beeaware.Utilis.Utils;
import org.apache.poi.ss.usermodel.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ExcelWritingTest {

    private List<SymptomsDTO> symptomsDTOList;

    @BeforeEach
    void setUp() {
        try{
        symptomsDTOList = Arrays.asList(
                new SymptomsDTO(Reaction.HIGH_REACTION.getValue(), true, Utils.parseDate("11-15-2024")),
                new SymptomsDTO(Reaction.MODERATE_REACTION.getValue(), false, Utils.parseDate("11-16-2024")),
                new SymptomsDTO(Reaction.LOW_REACTION.getValue(), true, Utils.parseDate("11-17-2024"))
        );
        } catch (Exception e){
            assertTrue(true);
        }

    }

    @Test
    void testExcelFileCreation() {
        ExcelWriting excelWriting = new ExcelWriting(symptomsDTOList);

        // Assert workbook is not null
        assertNotNull(excelWriting.getWorkbook(), "Workbook should not be null");

        Workbook workbook = excelWriting.getWorkbook();

        // Assert sheet creation
        Sheet sheet = workbook.getSheet("Symptoms");
        assertNotNull(sheet, "Symptoms sheet should be created");

        // Assert header row
        Row headerRow = sheet.getRow(0);
        assertNotNull(headerRow, "Header row should be created");
        assertEquals("Day", headerRow.getCell(0).getStringCellValue(), "First header cell should be 'Day'");
        assertEquals("Reaction", headerRow.getCell(1).getStringCellValue(), "Second header cell should be 'Reaction'");
        assertEquals("Antihistamine", headerRow.getCell(2).getStringCellValue(), "Third header cell should be 'Antihistamine'");

        // Assert data rows
        for (int i = 0; i < symptomsDTOList.size(); i++) {
            Row row = sheet.getRow(i + 1);
            SymptomsDTO symptom = symptomsDTOList.get(i);

            assertNotNull(row, "Row " + (i + 1) + " should be created");
            assertEquals(symptom.getDate(), row.getCell(0).getDateCellValue(), "Date value should match");
            assertEquals(symptom.getReaction(), row.getCell(1).getNumericCellValue(), "Reaction value should match");
            assertEquals(symptom.isAntihistamine(), row.getCell(2).getBooleanCellValue(), "Antihistamine value should match");
        }
    }

    @Test
    void testExcelFileSaving() throws IOException {
        ExcelWriting excelWriting = new ExcelWriting(symptomsDTOList);
        Workbook workbook = excelWriting.getWorkbook();

        File tempFile = File.createTempFile("Symptoms", ".xlsx");
        try (FileOutputStream fileOut = new FileOutputStream(tempFile)) {
            workbook.write(fileOut);
        }

        assertTrue(tempFile.exists(), "Temporary file should exist after saving");
        assertTrue(tempFile.length() > 0, "Temporary file should not be empty");

        // Clean up
        if (!tempFile.delete()) {
            System.err.println("Failed to delete temporary file");
        }
    }
}
