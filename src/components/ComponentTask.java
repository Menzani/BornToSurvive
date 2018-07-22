package it.menzani.bts.components;

import java.time.Duration;

public interface ComponentTask extends ComponentElement {
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

    interface Counter {
        boolean tick();

        long getPeriod();

        long getActualTicks();

        long getCount();

        default boolean peekNextTick() {
            return getCount() == getPeriod() - 1;
        }
    }
}
