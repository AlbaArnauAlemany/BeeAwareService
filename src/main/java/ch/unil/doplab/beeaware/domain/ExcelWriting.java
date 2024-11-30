package ch.unil.doplab.beeaware.domain;


import ch.unil.doplab.beeaware.Domain.DTO.SymptomsDTO;
import jakarta.inject.Inject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.List;
import java.util.logging.Logger;

// https://www.baeldung.com/java-microsoft-excel
@Getter
@Setter
@NoArgsConstructor
public class ExcelWriting {
    Workbook workbook;
    @Inject
    private ApplicationState state;
    private Logger logger = Logger.getLogger(ExcelWriting.class.getName());

    public ExcelWriting(List<SymptomsDTO> symptomsDTO) {
        workbook = new XSSFWorkbook();

        Sheet sheet = workbook.createSheet("Symptoms");
        sheet.setColumnWidth(0, 6000);
        sheet.setColumnWidth(1, 4000);
        sheet.setColumnWidth(2, 8000);

        Row header = sheet.createRow(0);

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        XSSFFont font = ((XSSFWorkbook) workbook).createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 16);
        font.setBold(true);
        headerStyle.setFont(font);

        CellStyle cellStyleDate = workbook.createCellStyle();
        CreationHelper createHelper = workbook.getCreationHelper();
        cellStyleDate.setDataFormat(createHelper.createDataFormat().getFormat("MM/DD/yyyy"));

        Cell headerCell = header.createCell(0);
        headerCell.setCellValue("Day");
        headerCell.setCellStyle(headerStyle);


        headerCell = header.createCell(1);
        headerCell.setCellValue("Reaction");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(2);
        headerCell.setCellValue("Antihistamine");
        headerCell.setCellStyle(headerStyle);

        int rowIndex = 1;
        for (SymptomsDTO symptom : symptomsDTO) {
            Row row = sheet.createRow(rowIndex++);
            Cell birthDateCell = row.createCell(0);
            birthDateCell.setCellValue(symptom.getDate());
            birthDateCell.setCellStyle(cellStyleDate);
            row.createCell(1).setCellValue(symptom.getReaction().toString());
            row.createCell(2).setCellValue(symptom.isAntihistamine());
        }
    }
}


