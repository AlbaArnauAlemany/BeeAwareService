package ch.unil.doplab.beeaware.Utilis;

import ch.unil.doplab.beeaware.Domain.Location;
import ch.unil.doplab.beeaware.Domain.PollenLocationIndex;
import ch.unil.doplab.beeaware.Domain.PollenLocationInfo;
import ch.unil.doplab.beeaware.domain.ApplicationState;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.logging.Logger;

@Singleton
public class Utils {
    private static final Logger logger = Logger.getLogger(Utils.class.getName());
    @Inject
    private ApplicationState state;

    private static List<LocalDate> convertDateToLocalDate(@NotNull Date date1, @NotNull Date date2) {
        LocalDate localDate1 = date1.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        LocalDate localDate2 = date2.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        List<LocalDate> dates = new ArrayList<>();
        dates.add(localDate1);
        dates.add(localDate2);
        return dates;
    }
    public static boolean isSameDate(@NotNull Date date1, @NotNull Date date2) {
        List<LocalDate> dates = convertDateToLocalDate(date1, date2);
        return dates.get(0).isEqual(dates.get(1));
    }

    public static boolean isDateAfter(@NotNull Date date1, @NotNull Date date2) {
        List<LocalDate> dates = convertDateToLocalDate(date1, date2);
        return dates.get(0).isAfter(dates.get(1)) || dates.get(0).isEqual(dates.get(1));
    }

    public static boolean isDateBefore(@NotNull Date date1, @NotNull Date date2) {
        List<LocalDate> dates = convertDateToLocalDate(date1, date2);
        return dates.get(0).isBefore(dates.get(1)) || dates.get(0).isEqual(dates.get(1));
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

    public static Date parseDate(String dateStr) throws ParseException {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
            try {
                LocalDate localDate = LocalDate.parse(dateStr, formatter);
                ZonedDateTime zonedDateTime = localDate.atStartOfDay(ZoneId.systemDefault());
                return Date.from(zonedDateTime.toInstant());
            } catch (DateTimeParseException e) {
                throw new ParseException("Invalid date format, expected MM-dd-yyyy. Provided: " + dateStr, 0);
            }
        }
}
