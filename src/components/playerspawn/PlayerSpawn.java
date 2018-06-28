package it.menzani.bts.components.playerspawn;

import it.menzani.bts.BornToSurvive;
import it.menzani.bts.components.SimpleComponent;
import it.menzani.bts.persistence.sql.wrapper.DatabaseCallable;
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
    public void load() {
        WrappedSQLDatabase database = getBornToSurvive().getDatabase();

        PreparedStatement[] preparedStatements =
                (PreparedStatement[]) database.submit(new PrepareStatements(), this);
        if (preparedStatements == null) return;
        setSpawnStatement = preparedStatements[0];
        getSpawnStatement = preparedStatements[1];

        boolean error = database.execute(new CreateTable(), this);
        if (error) return;

        super.load();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerSpawnLocation(PlayerSpawnLocationEvent event) {
        Player player = event.getPlayer();

        Integer updateCount = (Integer) getBornToSurvive().getDatabase().submit(new SetSpawn(setSpawnStatement,
                player.getUniqueId(), new Spawn(event.getSpawnLocation())), this);
        if (updateCount == null) return;

        boolean hasPlayedBefore = player.hasPlayedBefore();
        String warning = null;
        switch (updateCount) {
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

        Object result = getBornToSurvive().getDatabase().submit(new GetSpawn(getSpawnStatement, player.getUniqueId()), this);
        if (result == null) return;

        if (result == DatabaseCallable.NULL) {
            getLogger().fatal("Player spawn is not stored." + System.lineSeparator() + "player=" + player);
        } else {
            Spawn spawn = (Spawn) result;
            event.setRespawnLocation(new Location(getBornToSurvive().getOverworld(), spawn.x + 0.5, spawn.y, spawn.z + 0.5));
        }
    }
}
