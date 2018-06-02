package it.menzani.bts;

import it.menzani.bts.datastore.DatabaseCredentials;
import it.menzani.bts.datastore.impl.PostgreSQLDatabase;
import it.menzani.bts.datastore.wrapper.WrappedSQLDatabase;
import it.menzani.bts.playerspawn.PlayerSpawn;
import org.bukkit.World;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class BornToSurvive extends JavaPlugin {
    private static final DatabaseCredentials DATABASE_CREDENTIALS =
            new DatabaseCredentials(null, null, null, null);

    private WrappedSQLDatabase database;

    public WrappedSQLDatabase getDatabase() {
        return database;
    }

    @Override
    public void onEnable() {
        database = new WrappedSQLDatabase(new PostgreSQLDatabase(DATABASE_CREDENTIALS), getLogger());
        if (database.isConnectionNotAvailable()) return;

        Component playerSpawn = new PlayerSpawn(this);
        playerSpawn.load();
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
