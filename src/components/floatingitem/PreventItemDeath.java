package it.menzani.bts.components.floatingitem;

import it.menzani.bts.BornToSurvive;
import org.bukkit.scheduler.BukkitRunnable;

class PreventItemDeath extends BukkitRunnable {
    private final BornToSurvive bornToSurvive;

    PreventItemDeath(BornToSurvive bornToSurvive) {
        this.bornToSurvive = bornToSurvive;
    }

    @Override
    public void run() {
        FloatingItem.processEntities(bornToSurvive.getWorlds()
                .flatMap(world -> world.getEntities().stream()));
    }
}
