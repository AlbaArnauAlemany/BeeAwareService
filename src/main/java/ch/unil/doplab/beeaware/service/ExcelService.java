package ch.unil.doplab.beeaware.service;

import ch.unil.doplab.beeaware.Domain.DTO.SymptomsDTO;
import ch.unil.doplab.beeaware.domain.ExcelWriting;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Data
@AllArgsConstructor
public class ExcelService {
    public byte[] excelWrite(List<SymptomsDTO> symptomsDTOs) throws IOException {
        ExcelWriting excelWriting = new ExcelWriting(symptomsDTOs);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            excelWriting.getWorkbook().write(out);
            excelWriting.getWorkbook().close();

            byte[] excelData = out.toByteArray();
            return excelData;
        }
    }
}
