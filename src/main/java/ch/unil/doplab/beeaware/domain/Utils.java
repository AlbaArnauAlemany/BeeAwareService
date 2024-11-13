package ch.unil.doplab.beeaware.domain;

import ch.unil.doplab.beeaware.DTO.PollenInfoDTO;
import ch.unil.doplab.beeaware.Domain.Beezzer;
import ch.unil.doplab.beeaware.Domain.Pollen;
import ch.unil.doplab.beeaware.Domain.PollenLocationIndex;
import ch.unil.doplab.beeaware.Domain.Role;
import jakarta.inject.Inject;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.logging.Logger;

public class Utils {
    private static final Logger logger = Logger.getLogger(Utils.class.getName());
    @Inject
    private ApplicationState state;

    public static boolean isSameDay(@NotNull Date date1, @NotNull Date date2) {
        LocalDate localDate1 = date1.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        LocalDate localDate2 = date2.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        return localDate1.isEqual(localDate2);
    }

    public static Date formatDate(PollenLocationIndex.Date dailyInfo) {
        Calendar calendar = Calendar.getInstance();
        int year = dailyInfo.getYear();
        int month = dailyInfo.getMonth() - 1;
        int day = dailyInfo.getDay();
        calendar.set(year, month, day);
        return calendar.getTime();
    }

    public boolean isAdministrator(Long beezzerID) {
        Beezzer beezzer = state.getBeezzerService().getBeezzers().get(beezzerID);
        return Objects.equals(beezzer.getRole(), Role.ADMIN);
    }

    public static void addPollenInfo(List<PollenInfoDTO> PollenShortDTOsFct, PollenLocationIndex.DailyInfo dailyInfoFct, Beezzer beezzerFct) {
        for (PollenLocationIndex.PollenTypeInfo pollenTypeDailyInfo : dailyInfoFct.getPollenTypeInfo()) {
            for (Map.Entry<Long, Pollen> pollen : beezzerFct.getAllergens().entrySet()) {
                Utils.addPollenInfo2(PollenShortDTOsFct, pollen.getValue().getPollenNameEN(), pollenTypeDailyInfo);
            }
        }

        for (PollenLocationIndex.PlantInfo pollenDailyInfo : dailyInfoFct.getPlantInfo()) {
            for (Map.Entry<Long, Pollen> pollen : beezzerFct.getAllergens().entrySet()) {
                Utils.addPollenInfo3(PollenShortDTOsFct, pollen.getValue().getPollenNameEN(), pollenDailyInfo);
            }
        }
    }

    public static void addPollenInfo2(List<PollenInfoDTO> PollenShortDTOsFct2, String pollenName2, PollenLocationIndex.PollenTypeInfo pollenTypeDailyInfoFct) {
        if (pollenName2.equals(pollenTypeDailyInfoFct.getDisplayName())) {
            if (pollenTypeDailyInfoFct.getIndexInfo() != null) {
                PollenShortDTOsFct2.add(new PollenInfoDTO(pollenTypeDailyInfoFct));
            }
        }
    }

    public static void addPollenInfo3(List<PollenInfoDTO> PollenShortDTOsFct3, String pollenName3, PollenLocationIndex.PlantInfo pollenDailyInfoFct) {
        if (pollenName3.equals(pollenDailyInfoFct.getDisplayName())) {
            if (pollenDailyInfoFct.getIndexInfo() != null) {
                PollenShortDTOsFct3.add(new PollenInfoDTO(pollenDailyInfoFct));
            }
        }
    }

    // TODO: for the test part
    public static void printMethodName() {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        var methodName = stackTraceElements[2].getMethodName();
        var className = stackTraceElements[2].getClassName();
        className = className.substring(className.lastIndexOf('.') + 1);
        var method = className + "." + methodName;
        printSeparator('-', method.length());
        System.out.println(method);
        printSeparator('-', method.length());
    }

    public static void printSeparator(char c, int n) {
        for (int i = 0; i < n; i++) {
            System.out.print(c);
        }
        System.out.println();
    }

}
