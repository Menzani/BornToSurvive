package it.menzani.bts.misc;

import java.time.Duration;

public final class TickDuration {
    private TickDuration() {
    }

    public static long from(Duration duration) {
        if (duration.isNegative()) throw new IllegalArgumentException("duration must be positive.");
        return duration.toSeconds() * 20;
    }

    public static final long ZERO = from(Duration.ZERO);
    public static final long ONE_MINUTE = from(Duration.ofMinutes(1));
}
