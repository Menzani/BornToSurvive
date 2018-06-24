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
    public BukkitTask runTask() throws IllegalArgumentException, IllegalStateException {
        return runTask(bornToSurvive);
    }

    @Override
    public BukkitTask runTaskAsynchronously() throws IllegalArgumentException, IllegalStateException {
        return runTaskAsynchronously(bornToSurvive);
    }

    @Override
    public BukkitTask runTaskLater(Duration delay) throws IllegalArgumentException, IllegalStateException {
        return runTaskLater(bornToSurvive, ticks(delay, "delay"));
    }

    @Override
    public BukkitTask runTaskLaterAsynchronously(Duration delay) throws IllegalArgumentException, IllegalStateException {
        return runTaskLaterAsynchronously(bornToSurvive, ticks(delay, "delay"));
    }

    @Override
    public BukkitTask runTaskTimer(Duration period) throws IllegalArgumentException, IllegalStateException {
        return runTaskTimer(period, period);
    }

    @Override
    public BukkitTask runTaskTimerAsynchronously(Duration period) throws IllegalArgumentException, IllegalStateException {
        return runTaskTimerAsynchronously(period, period);
    }

    @Override
    public BukkitTask runTaskTimer(Duration delay, Duration period) throws IllegalArgumentException, IllegalStateException {
        return runTaskTimer(bornToSurvive, ticks(delay, "delay"), ticks(period, "period"));
    }

    @Override
    public BukkitTask runTaskTimerAsynchronously(Duration delay, Duration period) throws IllegalArgumentException, IllegalStateException {
        return runTaskTimerAsynchronously(bornToSurvive, ticks(delay, "delay"), ticks(period, "period"));
    }

    private static long ticks(Duration duration, String variableName) {
        if (duration.isNegative()) throw new IllegalArgumentException(variableName + " must be positive.");
        return duration.toSeconds() * 20;
    }

    @Override
    public SimpleComponent getComponent() {
        return component;
    }
}
