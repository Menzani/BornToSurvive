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
        task = runTaskLater(bornToSurvive, ticks(delay, "delay"));
    }

    @Override
    public void runTaskLaterAsynchronously(Duration delay) throws IllegalArgumentException, IllegalStateException {
        task = runTaskLaterAsynchronously(bornToSurvive, ticks(delay, "delay"));
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
        task = runTaskTimer(bornToSurvive, ticks(delay, "delay"), ticks(period, "period"));
    }

    @Override
    public void runTaskTimerAsynchronously(Duration delay, Duration period) throws IllegalArgumentException, IllegalStateException {
        task = runTaskTimerAsynchronously(bornToSurvive, ticks(delay, "delay"), ticks(period, "period"));
    }

    private static long ticks(Duration duration, String variableName) {
        if (duration.isNegative()) throw new IllegalArgumentException(variableName + " must be positive.");
        return duration.toSeconds() * 20;
    }

    @Override
    public void cancel() {
        if (task == null) throw new IllegalStateException("Task was never scheduled.");
        task.cancel();
    }

    @Override
    public SimpleComponent getComponent() {
        return component;
    }
}
