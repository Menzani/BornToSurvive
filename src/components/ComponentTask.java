package it.menzani.bts.components;

import org.bukkit.scheduler.BukkitTask;

import java.time.Duration;

public interface ComponentTask {
    BukkitTask runTask() throws IllegalArgumentException, IllegalStateException;

    BukkitTask runTaskAsynchronously() throws IllegalArgumentException, IllegalStateException;

    BukkitTask runTaskLater(Duration delay) throws IllegalArgumentException, IllegalStateException;

    BukkitTask runTaskLaterAsynchronously(Duration delay) throws IllegalArgumentException, IllegalStateException;

    BukkitTask runTaskTimer(Duration period) throws IllegalArgumentException, IllegalStateException;

    BukkitTask runTaskTimerAsynchronously(Duration period) throws IllegalArgumentException, IllegalStateException;

    BukkitTask runTaskTimer(Duration delay, Duration period) throws IllegalArgumentException, IllegalStateException;

    BukkitTask runTaskTimerAsynchronously(Duration delay, Duration period) throws IllegalArgumentException, IllegalStateException;

    Component getComponent();
}
