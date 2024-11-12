package ch.unil.doplab.beeaware.domain;

import ch.unil.doplab.beeaware.Domain.Beezzer;
import ch.unil.doplab.beeaware.Domain.PollenLocationIndex;
import ch.unil.doplab.beeaware.Domain.Role;
import jakarta.inject.Inject;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.logging.Logger;

public class Utilis {
    private static final Logger logger = Logger.getLogger(Utilis.class.getName());
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
}
