package it.menzani.bts;

import it.menzani.bts.components.Component;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Set;

class ExecutePostWorld extends BukkitRunnable {
    private final Set<Component> components;

    ExecutePostWorld(Set<Component> components) {
        this.components = components;
    }

    @Override
    public void run() {
        components.forEach(Component::load);
    }
}
