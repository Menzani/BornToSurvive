package it.menzani.bts.components.optimize;

import it.menzani.bts.BornToSurvive;
import it.menzani.bts.components.SimpleComponent;
import it.menzani.bts.components.playerspawn.PlayerSpawn;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.time.Duration;

public class Optimize extends SimpleComponent {
    public static final double borderSize = PlayerSpawn.spawnAreaSide + 20_000;

    private ViewDistanceAdjuster viewDistanceAdjuster;

    public Optimize(BornToSurvive bornToSurvive) {
        super(bornToSurvive);
    }

    @Override
    public void loadPreWorld() {
        viewDistanceAdjuster = new ViewDistanceAdjuster(this);
        if (viewDistanceAdjuster.isServerViewDistanceWrong()) {
            viewDistanceAdjuster = null;
            return;
        }
        viewDistanceAdjuster.register();
    }

    @Override
    public void load() {
        prepareServer();
        prepareWorlds();
        if (viewDistanceAdjuster != null) {
            viewDistanceAdjuster.runTaskTimer(Duration.ofMinutes(2));
        }
    }

    private void prepareServer() {
        Scoreboard scoreboard = getBornToSurvive().getServer().getScoreboardManager().getMainScoreboard();
        final String deaths = "deaths";
        Objective objective = scoreboard.getObjective(deaths);
        if (objective == null) {
            objective = scoreboard.registerNewObjective(deaths, "deathCount", deaths);
            objective.setDisplaySlot(DisplaySlot.PLAYER_LIST);
        }
    }

    private void prepareWorlds() {
        for (World world : getBornToSurvive().getWorlds()) {
            world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
            world.setGameRule(GameRule.REDUCED_DEBUG_INFO, true);
            world.setGameRule(GameRule.SHOW_DEATH_MESSAGES, false);
        }
        WorldBorder border = getBornToSurvive().getOverworld().getWorldBorder();
        border.setSize(borderSize);
        border = getBornToSurvive().getNether().getWorldBorder();
        border.setSize(borderSize / 8);
        border = getBornToSurvive().getTheEnd().getWorldBorder();
        border.setSize(borderSize);
    }
}
