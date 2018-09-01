package it.menzani.bts.components.optimize;

import it.menzani.bts.BornToSurvive;
import it.menzani.bts.components.SimpleComponent;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.WorldBorder;

import java.time.Duration;

public class Optimize extends SimpleComponent {
    private static final int spawnAreaSide = 200_000;

    private ViewDistanceAdjuster viewDistanceAdjuster;

    public Optimize(BornToSurvive bornToSurvive) {
        super(bornToSurvive);
    }

    @Override
    public void loadPreWorld() {
        viewDistanceAdjuster = new ViewDistanceAdjuster(this);
        viewDistanceAdjuster.register();
    }

    @Override
    public void load() {
        prepareWorlds();
        viewDistanceAdjuster.runTaskTimer(Duration.ofMinutes(1));
    }

    private void prepareWorlds() {
        for (World world : getBornToSurvive().getWorlds()) {
            world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
            world.setGameRule(GameRule.REDUCED_DEBUG_INFO, true);
            world.setGameRule(GameRule.SHOW_DEATH_MESSAGES, false);
        }
        World overworld = getBornToSurvive().getOverworld();
        overworld.setSpawnLocation(0, overworld.getHighestBlockYAt(0, 0), 0);
        overworld.setGameRule(GameRule.SPAWN_RADIUS, spawnAreaSide / 2);
        overworld.setGameRule(GameRule.SPECTATORS_GENERATE_CHUNKS, true);
        WorldBorder border = overworld.getWorldBorder();
        double borderSize = spawnAreaSide + 20_000;
        border.setSize(borderSize);
        border = getBornToSurvive().getNether().getWorldBorder();
        border.setSize(borderSize / 8);
        border = getBornToSurvive().getTheEnd().getWorldBorder();
        border.setSize(borderSize);
    }
}
