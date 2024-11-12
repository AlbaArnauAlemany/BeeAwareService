package ch.unil.doplab.beeaware.service;

import ch.unil.doplab.beeaware.domain.ApplicationState;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.inject.Inject;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
@Startup
public class DailyTaskService {

    private ScheduledExecutorService scheduler;
    private ForeCastService foreCastService;
    @Inject
    private ApplicationState state;
    private final Logger logger = Logger.getLogger(DailyTaskService.class.getName());

    @PostConstruct
    public void init() {
        foreCastService = new ForeCastService(state.getAPIKEY(), state.getPollenLocationIndexService());
        scheduler = Executors.newScheduledThreadPool(1);

        int targetHour = 10;
        int targetMinute = 45;


        ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());
        ZonedDateTime nextRun = now.withHour(targetHour).withMinute(targetMinute).withSecond(0);

        if (now.compareTo(nextRun) > 0) {
            nextRun = nextRun.plusDays(1);
            foreCastService.forecastAllLocation(state.getLocationService().getLocations());
        }

        long initialDelay = TimeUnit.MILLISECONDS.convert(nextRun.toEpochSecond() - now.toEpochSecond(), TimeUnit.SECONDS);

        scheduler.scheduleAtFixedRate(this::runDailyTask, initialDelay, TimeUnit.DAYS.toMillis(1), TimeUnit.MILLISECONDS);
    }

    private void runDailyTask() {
        logger.log(Level.INFO, "Start scheduled forecast at {0}", new Date());
        foreCastService.forecastAllLocation(state.getLocationService().getLocations());
    }
}