package ch.unil.doplab.beeaware.domain;

import ch.unil.doplab.beeaware.Domain.Location;
import ch.unil.doplab.beeaware.Domain.PollenLocationIndex;
import ch.unil.doplab.beeaware.Domain.PollenLocationInfo;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.logging.Logger;
@Singleton
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

    public static Date formatDate(PollenLocationInfo.Date dailyInfo) {
        Calendar calendar = Calendar.getInstance();
        int year = dailyInfo.getYear();
        int month = dailyInfo.getMonth() - 1;
        int day = dailyInfo.getDay();
        calendar.set(year, month, day);
        return calendar.getTime();
    }

    public static List<PollenLocationIndex> transformPollenInfoInPollenIndex(PollenLocationInfo pollenLocationInfo, Location location){
        List<PollenLocationIndex> pollenLocationIndexList = new ArrayList<>();
        for (PollenLocationInfo.DailyInfo dailyInfo : pollenLocationInfo.getDailyInfo()) {
            for (PollenLocationInfo.PollenTypeInfo pollenTypeDailyInfo : dailyInfo.getPollenTypeInfo()) {
                pollenLocationIndexList.add(new PollenLocationIndex(pollenTypeDailyInfo, formatDate(dailyInfo.getDate()), location));
            }
            for (PollenLocationInfo.PlantInfo pollenDailyInfo : dailyInfo.getPlantInfo()) {
                pollenLocationIndexList.add(new PollenLocationIndex(pollenDailyInfo, formatDate(dailyInfo.getDate()), location));
            }
        }
        return pollenLocationIndexList;
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
