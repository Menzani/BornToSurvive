package it.menzani.bts;

import it.menzani.bts.components.Component;
import it.menzani.bts.components.disablecommand.DisableCommand;
import it.menzani.bts.components.floatingitem.FloatingItem;
import it.menzani.bts.components.minecartspeed.MinecartSpeed;
import it.menzani.bts.components.optimize.Optimize;
import it.menzani.bts.components.playerarmornotice.ItemBreakNotice;
import it.menzani.bts.components.playerchat.PlayerChat;
import it.menzani.bts.components.playerspawn.PlayerSpawn;
import it.menzani.bts.components.playerteleport.PlayerTeleport;
import it.menzani.bts.configuration.MainConfiguration;
import it.menzani.bts.logging.LoggerFactory;
import it.menzani.bts.persistence.sql.PostgreSQLDatabase;
import it.menzani.bts.persistence.sql.wrapper.WrappedSQLDatabase;
import it.menzani.logger.api.Logger;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

public class BornToSurvive extends JavaPlugin {
    private static final Path logFile = Paths.get("logs", "bts", "bts.log");

    private Logger logger;
    private WrappedSQLDatabase database;

    public Logger getRootLogger() {
        return logger;
    }

    public WrappedSQLDatabase getDatabase() {
        return database;
    }

    public BornToSurvive() {
        LoggerFactory builder = new LoggerFactory(logFile, getLogger());
        boolean failure = builder.createLogFolder();
        if (failure) return;
        logger = builder.createLogger();
    }

    @Override
    public void onLoad() {
        if (logger == null) return;

        MainConfiguration mainConfiguration = new MainConfiguration(this);
        boolean invalid = mainConfiguration.validate();
        if (invalid) return;

        database = new WrappedSQLDatabase(new PostgreSQLDatabase(mainConfiguration.getDatabaseCredentials()), logger);
    }

    @Override
    public void onEnable() {
        if (database == null || database.isConnectionNotAvailable()) return;

        final Set<Component> components = Set.of(
                new PlayerSpawn(this),
                new PlayerChat(this),
                new MinecartSpeed(this),
                new PlayerTeleport(this),
                new ItemBreakNotice(this),
                new FloatingItem(this),
                new DisableCommand(this),
                new Optimize(this)
        );
        components.forEach(Component::load);
    }

    @Override
    public void onDisable() {
        if (database != null) {
            database.close();
        }
    }

    public void registerListener(Listener listener) {
        getServer().getPluginManager().registerEvents(listener, this);
    }

    public World getOverworld() {
        return getServer().getWorld("world");
    }

    public World getNether() {
        return getServer().getWorld("world_nether");
    }

    public World getTheEnd() {
        return getServer().getWorld("world_the_end");
    }

    public Stream<World> getWorlds() {
        Set<World> worlds = new HashSet<>() {
            public boolean add(World world) {
                if (world == null) return false;
                return super.add(world);
            }
        };
        worlds.add(getOverworld());
        worlds.add(getNether());
        worlds.add(getTheEnd());
        return worlds.stream();
    }

    public World.Environment matchWorld(World world) {
        Objects.requireNonNull(world, "world");
        if (world == getOverworld()) {
            return World.Environment.NORMAL;
        }
        if (world == getNether()) {
            return World.Environment.NETHER;
        }
        if (world == getTheEnd()) {
            return World.Environment.THE_END;
        }
        throw new IllegalArgumentException("Unknown world.");
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
}
