package it.menzani.bts.components.optimize;

import it.menzani.bts.BornToSurvive;
import it.menzani.bts.components.SimpleComponent;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.world.WorldInitEvent;

import java.time.Duration;

public class Optimize extends SimpleComponent {
    private ViewDistanceAdjuster viewDistanceAdjuster;

    public Optimize(BornToSurvive bornToSurvive) {
        super(bornToSurvive);
    }

    @Override
    public void loadPreWorld() {
        super.loadPreWorld();
        viewDistanceAdjuster = new ViewDistanceAdjuster(this);
        viewDistanceAdjuster.register();
    }

    @Override
    public void load() {
        viewDistanceAdjuster.runTaskTimer(Duration.ofMinutes(1));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWorldInit(WorldInitEvent event) {
        World world = event.getWorld();
        world.setKeepSpawnInMemory(false);
    }
}
