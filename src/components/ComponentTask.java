package it.menzani.bts.components;

import java.time.Duration;

public interface ComponentTask extends ComponentElement {
    Duration ONE_TICK = Duration.ofMillis(50);

    void runTask() throws IllegalArgumentException, IllegalStateException;

    void runTaskAsynchronously() throws IllegalArgumentException, IllegalStateException;

    void runTaskLater(Duration delay) throws IllegalArgumentException, IllegalStateException;

    void runTaskLaterAsynchronously(Duration delay) throws IllegalArgumentException, IllegalStateException;

    void runTaskTimer() throws IllegalArgumentException, IllegalStateException;

    void runTaskTimerAsynchronously() throws IllegalArgumentException, IllegalStateException;

    void runTaskTimer(Duration period) throws IllegalArgumentException, IllegalStateException;

    void runTaskTimerAsynchronously(Duration period) throws IllegalArgumentException, IllegalStateException;

    void runTaskTimer(Duration delay, Duration period) throws IllegalArgumentException, IllegalStateException;

    void runTaskTimerAsynchronously(Duration delay, Duration period) throws IllegalArgumentException, IllegalStateException;

    void cancel();

    Counter newCounter(Duration period, boolean hasDelay);

    static long ticks(Duration duration, String variableName) {
        if (ONE_TICK.compareTo(duration) > 0)
            throw new IllegalArgumentException(variableName + " must be at least " + ONE_TICK.toMillis() + " milliseconds.");
        return duration.multipliedBy(20).toSeconds();
    }

    interface Counter {
        boolean tick();

        long getPeriod();

        long getActualTicks();

        default long getSkippedTicks() {
            return getActualTicks() - 1;
        }

        long getCount();

        default boolean peekNextTick() {
            return getCount() == getPeriod() - 1;
        }
    }
}
