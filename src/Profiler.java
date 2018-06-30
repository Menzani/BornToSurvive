package it.menzani.bts;

import it.menzani.logger.api.Logger;

import java.time.Duration;

public class Profiler {
    private final Logger logger;
    private final String label;
    private final long startTime;

    Profiler(Logger logger, String label) {
        this.logger = logger;
        this.label = label;
        startTime = System.nanoTime();
    }

    public Duration stop() {
        long endTime = System.nanoTime();
        long diff = endTime - startTime;
        return Duration.ofNanos(diff);
    }

    public Duration report() {
        Duration duration = stop();
        logger.debug(() -> {
            String formatted = String.format("%dm %dms", duration.toSecondsPart(), duration.toMillisPart());
            return label + " took " + formatted + " to complete.";
        });
        return duration;
    }
}
