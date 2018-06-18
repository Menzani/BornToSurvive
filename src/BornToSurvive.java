package it.menzani.bts;

import it.menzani.bts.chat.PlayerMessages;
import it.menzani.bts.config.MainConfiguration;
import it.menzani.bts.datastore.impl.PostgreSQLDatabase;
import it.menzani.bts.datastore.wrapper.WrappedSQLDatabase;
import it.menzani.bts.logging.LoggerFactory;
import it.menzani.bts.minecartspeed.MinecartSpeed;
import it.menzani.bts.optimize.Optimize;
import it.menzani.bts.playerspawn.PlayerSpawn;
import it.menzani.logger.api.Logger;
import org.bukkit.World;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

public class BornToSurvive extends JavaPlugin {
    private static final Path LOG_FILE = Paths.get("logs", "bts", "bts.log");

    private Logger logger;
    private WrappedSQLDatabase database;

    public Logger getRootLogger() {
        return logger;
    }

    public WrappedSQLDatabase getDatabase() {
        return database;
    }

    public BornToSurvive() {
        LoggerFactory builder = new LoggerFactory(LOG_FILE, getLogger());
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
                new PlayerMessages(this),
                new MinecartSpeed(this),
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

    void registerListener(Listener listener) {
        getServer().getPluginManager().registerEvents(listener, this);
    }

    public World getOverworld() {
        return getServer().getWorlds().get(0);
    }
}
