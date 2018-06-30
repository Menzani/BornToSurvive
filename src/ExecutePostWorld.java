package it.menzani.bts;

import it.menzani.bts.components.Component;
import it.menzani.logger.api.Logger;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;

class ExecutePostWorld extends BukkitRunnable {
    private final BornToSurvive bornToSurvive;

    ExecutePostWorld(BornToSurvive bornToSurvive) {
        this.bornToSurvive = bornToSurvive;
    }

    @Override
    public void run() {
        Server server = bornToSurvive.getServer();
        World overworld = server.getWorld("world");
        World nether = server.getWorld("world_nether");
        World theEnd = server.getWorld("world_the_end");
        Logger logger = bornToSurvive.getRootLogger();
        if (overworld == null || overworld.getEnvironment() != World.Environment.NORMAL) {
            logger.fatal("Could not find Overworld as world 'world'.");
            return;
        }
        if (nether == null || nether.getEnvironment() != World.Environment.NETHER) {
            logger.fatal("Could not find Nether as world 'world_nether'.");
            return;
        }
        if (theEnd == null || theEnd.getEnvironment() != World.Environment.THE_END) {
            logger.fatal("Could not find The End as world 'world_the_end'.");
            return;
        }
        bornToSurvive.setOverworld(overworld);
        bornToSurvive.setNether(nether);
        bornToSurvive.setTheEnd(theEnd);
        Set<World> worlds = new HashSet<>();
        worlds.add(bornToSurvive.getOverworld());
        worlds.add(bornToSurvive.getNether());
        worlds.add(bornToSurvive.getTheEnd());
        bornToSurvive.setWorlds(worlds);

        bornToSurvive.getComponents().forEach(Component::load);
    }
}
