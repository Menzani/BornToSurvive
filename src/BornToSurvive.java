package it.menzani.bts;

import it.menzani.bts.chat.PlayerMessages;
import it.menzani.bts.config.MainConfiguration;
import it.menzani.bts.datastore.impl.PostgreSQLDatabase;
import it.menzani.bts.datastore.wrapper.WrappedSQLDatabase;
import it.menzani.bts.logging.LoggerFactory;
import it.menzani.bts.playerspawn.PlayerSpawn;
import it.menzani.logger.api.Logger;
import org.bukkit.World;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.file.Path;
import java.nio.file.Paths;

public class BornToSurvive extends JavaPlugin {
    private static final Path LOG_FILE = Paths.get("logs", "bts", "bts.log");

    private Logger logger;
    private MainConfiguration mainConfiguration;
    private WrappedSQLDatabase database;

    public Logger getRootLogger() {
        return logger;
    }

    public MainConfiguration getMainConfiguration() {
        return mainConfiguration;
    }

    public WrappedSQLDatabase getDatabase() {
        return database;
    }

    @Override
    public void onEnable() {
        LoggerFactory builder = new LoggerFactory(LOG_FILE, getLogger());
        boolean failure = builder.createLogFolder();
        if (failure) return;
        logger = builder.createLogger();

        mainConfiguration = new MainConfiguration(this);
        boolean invalid = mainConfiguration.validate();
        if (invalid) return;

        database = new WrappedSQLDatabase(new PostgreSQLDatabase(mainConfiguration.getDatabaseCredentials()), logger);
        if (database.isConnectionNotAvailable()) return;

        final Component[] components = {
                new PlayerSpawn(this),
                new PlayerMessages(this)
        };
        for (Component component : components) {
            component.load();
        }
    }

    @Override
    public void onDisable() {
        database.close();
    }

    public void registerListener(Listener listener) {
        getServer().getPluginManager().registerEvents(listener, this);
    }

    public World getOverworld() {
        return getServer().getWorlds().get(0);
    }
}
