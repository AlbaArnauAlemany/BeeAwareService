package ch.unil.doplab.beeaware.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyTaskService {

    private final Logger logger = Logger.getLogger(DailyTaskService.class.getName());
    private ScheduledExecutorService scheduler;
    private ForeCastService foreCastService;
    private PollenLocationIndexService pollenLocationIndexService;
    private LocationService locationService;


    public DailyTaskService(ForeCastService foreCastService, PollenLocationIndexService pollenLocationIndexService, LocationService locationService) {
        this.pollenLocationIndexService = pollenLocationIndexService;
        this.foreCastService = foreCastService;
        this.locationService = locationService;
        scheduler = Executors.newScheduledThreadPool(1);

        int targetHour = 6;
        int targetMinute = 0;

        ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());
        ZonedDateTime nextRun = now.withHour(targetHour).withMinute(targetMinute).withSecond(0);
        logger.log(Level.INFO, "Now {0}", now);
        logger.log(Level.INFO, "Next run {0}", nextRun);
        if (now.compareTo(nextRun) > 0) {
            nextRun = nextRun.plusDays(1);
//            foreCastService.forecastAllLocation(locationService.getLocations());
        }

        long initialDelay = TimeUnit.MILLISECONDS.convert(nextRun.toEpochSecond() - now.toEpochSecond(), TimeUnit.SECONDS);
        scheduler.scheduleAtFixedRate(this::runDailyTask, initialDelay, TimeUnit.DAYS.toMillis(1), TimeUnit.MILLISECONDS);
    }

    public boolean runDailyTask() {
        try {
            logger.log(Level.INFO, "Start scheduled forecast at {0}", new Date());
//            foreCastService.forecastAllLocation(locationService.getLocations());
            return true;
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error force forecasting all locations : \n{0}", e.getStackTrace());
            return false;
        }
    }
}