package it.menzani.bts;

import it.menzani.bts.components.Component;
import it.menzani.bts.components.disablecommand.DisableCommand;
import it.menzani.bts.components.fix.Fix;
import it.menzani.bts.components.floatingitem.FloatingItem;
import it.menzani.bts.components.itembreaknotice.ItemBreakNotice;
import it.menzani.bts.components.lighthelper.LightHelper;
import it.menzani.bts.components.minecartspeed.MinecartSpeed;
import it.menzani.bts.components.optimize.Optimize;
import it.menzani.bts.components.playerchat.PlayerChat;
import it.menzani.bts.components.playername.PlayerName;
import it.menzani.bts.components.playerspawn.PlayerSpawn;
import it.menzani.bts.components.playerteleport.PlayerTeleport;
import it.menzani.bts.components.worldreset.WorldReset;
import it.menzani.bts.configuration.MainConfiguration;
import it.menzani.bts.logging.LoggerFactory;
import it.menzani.bts.persistence.PropertyStore;
import it.menzani.bts.persistence.sql.PostgreSQLDatabase;
import it.menzani.bts.persistence.sql.wrapper.WrappedSQLDatabase;
import it.menzani.logger.api.Logger;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Stream;

public class BornToSurvive extends JavaPlugin {
    private static final Path logFile = Paths.get("logs", "bts", "bts.log");
    private static final String propertyInitialized = "Property has already been initialized.";
    private static final String propertyNotInitialized = "Property has not yet been initialized. Calling from Component#load() will fix this.";

    private Logger logger;
    private PropertyStore propertyStore;
    private WrappedSQLDatabase database;
    private Set<Component> components;
    private World overworld, nether, theEnd;
    private Set<World> worlds;

    public Logger getRootLogger() {
        return logger;
    }

    public PropertyStore getPropertyStore() {
        return propertyStore;
    }

    public WrappedSQLDatabase getDatabase() {
        return database;
    }

    Set<Component> getComponents() {
        return components;
    }

    @Override
    public void onLoad() {
        MainConfiguration mainConfiguration = new MainConfiguration(this);
        boolean invalid = mainConfiguration.validate();
        if (invalid) return;

        LoggerFactory builder = new LoggerFactory(logFile, getLogger());
        boolean failure = builder.createLogFolder();
        if (failure) return;
        logger = builder.withLevel(mainConfiguration.getLog().getLevel())
                .createLogger();
        if (logger == null) return;

        File persistenceFolder = new File(getDataFolder(), "persistence");
        propertyStore = new PropertyStore(persistenceFolder);
        boolean error = propertyStore.load();
        if (error) return;

        database = new WrappedSQLDatabase(new PostgreSQLDatabase(mainConfiguration.getDatabaseCredentials()), logger);
        if (database.isConnectionNotAvailable()) return;

        components = Set.of(
                new PlayerSpawn(this),
                new PlayerChat(this),
                new MinecartSpeed(this),
                new PlayerTeleport(this),
                new ItemBreakNotice(this),
                new FloatingItem(this),
                new DisableCommand(this),
                new Optimize(this),
                new WorldReset(this, mainConfiguration.getWorldReset().getPhase()),
                new LightHelper(this),
                new Fix(this),
                new PlayerName(this)
        );
    }

    @Override
    public void onEnable() {
        if (components != null) {
            components.forEach(Component::loadPreWorld);

            BukkitRunnable task = new ExecutePostWorld(this);
            task.runTask(this);
        }
    }

    @Override
    public void onDisable() {
        if (propertyStore != null) {
            if (database != null) {
                if (components != null) {
                    components.forEach(Component::unload);
                }
                database.close();
            }
            propertyStore.save();
        }
    }

    public void registerListener(Listener listener) {
        getServer().getPluginManager().registerEvents(listener, this);
    }

    public World getOverworld() {
        if (overworld == null) throw new IllegalStateException(propertyNotInitialized);
        return overworld;
    }

    void setOverworld(World overworld) {
        assert overworld != null;
        if (this.overworld != null) throw new IllegalStateException(propertyInitialized);
        this.overworld = overworld;
    }

    public World getNether() {
        if (nether == null) throw new IllegalStateException(propertyNotInitialized);
        return nether;
    }

    void setNether(World nether) {
        assert nether != null;
        if (this.nether != null) throw new IllegalStateException(propertyInitialized);
        this.nether = nether;
    }

    public World getTheEnd() {
        if (theEnd == null) throw new IllegalStateException(propertyNotInitialized);
        return theEnd;
    }

    void setTheEnd(World theEnd) {
        assert theEnd != null;
        if (this.theEnd != null) throw new IllegalStateException(propertyInitialized);
        this.theEnd = theEnd;
    }

    public Stream<World> getWorlds() {
        if (worlds == null) throw new IllegalStateException(propertyNotInitialized);
        return worlds.stream();
    }

    void setWorlds(Set<World> worlds) {
        if (this.worlds != null) throw new IllegalStateException(propertyInitialized);
        this.worlds = worlds;
    }

    public World.Environment matchWorld(World world) {
        if (world == getOverworld()) {
            return World.Environment.NORMAL;
        }
        if (world == getNether()) {
            return World.Environment.NETHER;
        }
        if (world == getTheEnd()) {
            return World.Environment.THE_END;
        }
        throw new IllegalArgumentException("Unknown world: " + world);
    }

    public User getUserOrNotify(String username, User receiver) {
        Player player = getServer().getPlayer(username);
        if (player == null) {
            receiver.sendMessageFormat("Player not found.");
            return null;
        }
        return new User(player);
    }

    public void broadcast(String message, Player... players) {
        for (int i = 0; i < players.length; i++) {
            message = message.replace('{' + Integer.toString(i + 1) + '}', players[i].getDisplayName() + ChatColor.RESET);
        }
        getServer().broadcastMessage(message);
    }

    public Profiler newProfiler(String label) {
        return new Profiler(logger, label);
    }
}
