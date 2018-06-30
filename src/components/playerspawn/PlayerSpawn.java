package it.menzani.bts.components.playerspawn;

import it.menzani.bts.BornToSurvive;
import it.menzani.bts.components.SimpleComponent;
import it.menzani.bts.persistence.sql.wrapper.Value;
import it.menzani.bts.persistence.sql.wrapper.WrappedSQLDatabase;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import java.sql.PreparedStatement;

public class PlayerSpawn extends SimpleComponent {
    private PreparedStatement setSpawnStatement, getSpawnStatement;

    public PlayerSpawn(BornToSurvive bornToSurvive) {
        super(bornToSurvive);
    }

    @Override
    public void loadPreWorld() {
        WrappedSQLDatabase database = getBornToSurvive().getDatabase();

        Value<PreparedStatement[]> preparedStatements = database.submit(new PrepareStatements(), this);
        if (preparedStatements == null) return;
        setSpawnStatement = preparedStatements.get()[0];
        getSpawnStatement = preparedStatements.get()[1];

        boolean error = database.execute(new CreateTable(), this);
        if (error) return;

        super.loadPreWorld();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerSpawnLocation(PlayerSpawnLocationEvent event) {
        Player player = event.getPlayer();

        Value<Integer> updateCount = getBornToSurvive().getDatabase().submit(new SetSpawn(setSpawnStatement,
                player.getUniqueId(), new Spawn(event.getSpawnLocation())), this);
        if (updateCount == null) return;

        boolean hasPlayedBefore = player.hasPlayedBefore();
        String warning = null;
        switch (updateCount.get()) {
            case 0:
                if (!hasPlayedBefore) warning = "Spawn is stored for player that has never played before." +
                        System.lineSeparator() + "player=" + player;
                break;
            case 1:
                if (hasPlayedBefore) warning = "Stored spawn for player that has played before." +
                        System.lineSeparator() + "player=" + player;
                break;
        }
        if (warning != null) getLogger().warn(warning);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (event.isBedSpawn()) {
            return;
        }
        Player player = event.getPlayer();

        Value<Spawn> spawn = getBornToSurvive().getDatabase().submit(new GetSpawn(getSpawnStatement, player.getUniqueId()), this);
        if (spawn == null) return;

        if (spawn.isNull()) {
            getLogger().fatal("Player spawn is not stored." + System.lineSeparator() + "player=" + player);
        } else {
            event.setRespawnLocation(new Location(getBornToSurvive().getOverworld(), spawn.get().x + 0.5, spawn.get().y, spawn.get().z + 0.5));
        }
    }
}
