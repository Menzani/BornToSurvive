package it.menzani.bts;

import java.time.Duration;

public class TickDuration {
    public static final Duration ONE_TICK = Duration.ofMillis(50);

    public static long convert(Duration duration, String variableName) {
        if (ONE_TICK.compareTo(duration) > 0)
            throw new IllegalArgumentException(variableName + " must be at least " + ONE_TICK.toMillis() + " milliseconds.");
        return duration.multipliedBy(20).toSeconds();
    }
}
