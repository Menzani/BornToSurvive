package it.menzani.bts.components;

import it.menzani.bts.BornToSurvive;
import it.menzani.logger.api.Logger;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.time.Duration;

public abstract class SimpleComponentTask extends BukkitRunnable implements ComponentTask {
    private final SimpleComponent component;
    private final BornToSurvive bornToSurvive;
    private final Logger logger;
    private BukkitTask task;
    private long period = -1;

    protected SimpleComponentTask(SimpleComponent component) {
        this.component = component;
        bornToSurvive = component.getBornToSurvive();
        logger = component.getLogger();
    }

    protected BornToSurvive getBornToSurvive() {
        return bornToSurvive;
    }

    protected Logger getLogger() {
        return logger;
    }

    @Override
    public void runTask() throws IllegalArgumentException, IllegalStateException {
        task = runTask(bornToSurvive);
    }

    @Override
    public void runTaskAsynchronously() throws IllegalArgumentException, IllegalStateException {
        task = runTaskAsynchronously(bornToSurvive);
    }

    @Override
    public void runTaskLater(Duration delay) throws IllegalArgumentException, IllegalStateException {
        task = runTaskLater(bornToSurvive, ComponentTask.ticks(delay, "delay"));
    }

    @Override
    public void runTaskLaterAsynchronously(Duration delay) throws IllegalArgumentException, IllegalStateException {
        task = runTaskLaterAsynchronously(bornToSurvive, ComponentTask.ticks(delay, "delay"));
    }

    @Override
    public void runTaskTimer() throws IllegalArgumentException, IllegalStateException {
        runTaskTimer(ONE_TICK);
    }

    @Override
    public void runTaskTimerAsynchronously() throws IllegalArgumentException, IllegalStateException {
        runTaskTimerAsynchronously(ONE_TICK);
    }

    @Override
    public void runTaskTimer(Duration period) throws IllegalArgumentException, IllegalStateException {
        runTaskTimer(period, period);
    }

    @Override
    public void runTaskTimerAsynchronously(Duration period) throws IllegalArgumentException, IllegalStateException {
        runTaskTimerAsynchronously(period, period);
    }

    @Override
    public void runTaskTimer(Duration delay, Duration period) throws IllegalArgumentException, IllegalStateException {
        this.period = ComponentTask.ticks(period, "period");
        task = runTaskTimer(bornToSurvive, ComponentTask.ticks(delay, "delay"), this.period);
    }

    @Override
    public void runTaskTimerAsynchronously(Duration delay, Duration period) throws IllegalArgumentException, IllegalStateException {
        this.period = ComponentTask.ticks(period, "period");
        task = runTaskTimerAsynchronously(bornToSurvive, ComponentTask.ticks(delay, "delay"), this.period);
    }

    @Override
    public void cancel() {
        if (task == null) throw new IllegalStateException("Task was not scheduled.");
        task.cancel();
    }

    @Override
    public Counter newCounter(Duration period, boolean hasDelay) {
        return new SimpleCounter(ComponentTask.ticks(period, "period"), hasDelay);
    }

    @Override
    public SimpleComponent getComponent() {
        return component;
    }

    private class SimpleCounter implements Counter {
        private final long period;
        private long count;

        private SimpleCounter(long period, boolean hasDelay) {
            this.period = period;
            if (!hasDelay) {
                count = period - 1;
            }
        }

        @Override
        public boolean tick() {
            if (++count == period) {
                count = 0;
                return true;
            }
            return false;
        }

        @Override
        public long getPeriod() {
            return period;
        }

        @Override
        public long getActualTicks() {
            if (SimpleComponentTask.this.period == -1) throw new IllegalStateException("Task is not timer.");
            return period / SimpleComponentTask.this.period;
        }

        @Override
        public long getCount() {
            return count;
        }
    }
}
