package it.menzani.bts.playerspawn;

import it.menzani.bts.BornToSurvive;
import it.menzani.bts.Component;
import it.menzani.bts.datastore.wrapper.DatabaseCallable;
import it.menzani.bts.datastore.wrapper.WrappedSQLDatabase;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.sql.PreparedStatement;

public class PlayerSpawn implements Component, Listener {
    private final BornToSurvive bornToSurvive;
    private final PreparedStatement setSpawnStatement, getSpawnStatement;

    public PlayerSpawn(BornToSurvive bornToSurvive) {
        this.bornToSurvive = bornToSurvive;
        WrappedSQLDatabase database = bornToSurvive.getDatabase();

        PreparedStatement[] preparedStatements =
                (PreparedStatement[]) database.submit(new PrepareStatements(), this);
        if (preparedStatements == null) {
            setSpawnStatement = getSpawnStatement = null;
            return;
        }

        setSpawnStatement = preparedStatements[0];
        getSpawnStatement = preparedStatements[1];

        database.execute(new CreateTable(), this);
    }

    @Override
    public void load() {
        bornToSurvive.registerListener(this);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Location location = player.getLocation();

        Integer updateCount = (Integer) bornToSurvive.getDatabase().submit(new SetSpawn(setSpawnStatement,
                player.getUniqueId(), new Spawn(location.getBlockX(), location.getBlockY(), location.getBlockZ())), this);
        if (updateCount == null) return;

        boolean hasPlayedBefore = player.hasPlayedBefore();
        String warning = null;
        switch (updateCount) {
            case 0:
                if (!hasPlayedBefore) warning = "Did not store spawn for player that has never played before." +
                        System.lineSeparator() + "player=" + player;
                break;
            case 1:
                if (hasPlayedBefore) warning = "Stored spawn for player that has played before." +
                        System.lineSeparator() + "player=" + player;
                break;
        }
        if (warning != null) bornToSurvive.getLogger().warning(warning);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (event.isBedSpawn()) {
            return;
        }
        Player player = event.getPlayer();

        Object result = bornToSurvive.getDatabase().submit(new GetSpawn(getSpawnStatement, player.getUniqueId()), this);
        if (result == null) return;

        if (result == DatabaseCallable.NULL) {
            bornToSurvive.getLogger().severe("Player spawn is not stored." + System.lineSeparator() + "player=" + player);
        } else {
            Spawn spawn = (Spawn) result;
            event.setRespawnLocation(new Location(bornToSurvive.getOverworld(), spawn.x + 0.5, spawn.y, spawn.z + 0.5));
        }
    }
}