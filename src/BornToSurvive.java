package it.menzani.bts;

import it.menzani.bts.config.MainConfiguration;
import it.menzani.bts.datastore.impl.PostgreSQLDatabase;
import it.menzani.bts.datastore.wrapper.WrappedSQLDatabase;
import it.menzani.bts.playermessages.PlayerMessages;
import it.menzani.bts.playerspawn.PlayerSpawn;
import org.bukkit.World;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class BornToSurvive extends JavaPlugin {
    private MainConfiguration mainConfiguration;
    private WrappedSQLDatabase database;

    public MainConfiguration getMainConfiguration() {
        return mainConfiguration;
    }

    public WrappedSQLDatabase getDatabase() {
        return database;
    }

    @Override
    public void onEnable() {
        mainConfiguration = new MainConfiguration(this);
        boolean invalid = mainConfiguration.validate();
        if (invalid) return;

        database = new WrappedSQLDatabase(new PostgreSQLDatabase(mainConfiguration.getDatabaseCredentials()), getLogger());
        if (database.isConnectionNotAvailable()) return;

        final Component[] components = {
                new PlayerSpawn(this),
                new PlayerMessages(this)};
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
