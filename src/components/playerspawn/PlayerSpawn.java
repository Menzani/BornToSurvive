package it.menzani.bts.components.playerspawn;

import it.menzani.bts.BornToSurvive;
import it.menzani.bts.components.SimpleComponent;
import it.menzani.bts.persistence.sql.wrapper.Value;
import it.menzani.bts.persistence.sql.wrapper.WrappedSQLDatabase;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import java.sql.PreparedStatement;
import java.util.Random;
import java.util.UUID;

public class PlayerSpawn extends SimpleComponent {
    public static final int spawnAreaSide = 200_000;

    private PreparedStatement setSpawnStatement, getSpawnStatement;
    private final Random random = new Random();

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

    @EventHandler
    public void onPlayerSpawnLocation(PlayerSpawnLocationEvent event) {
        WrappedSQLDatabase database = getBornToSurvive().getDatabase();
        Player player = event.getPlayer();
        UUID id = player.getUniqueId();

        Value<Spawn> spawn = database.submit(new GetSpawn(getSpawnStatement, id), this);
        if (spawn == null) return;

        boolean hasPlayedBefore = player.hasPlayedBefore();
        String warning = null;
        if (spawn.isNull()) {
            int x = (1 + random.nextInt(spawnAreaSide / 2)) * (random.nextBoolean() ? 1 : -1);
            int z = (1 + random.nextInt(spawnAreaSide / 2)) * (random.nextBoolean() ? 1 : -1);
            Location location = new Location(getBornToSurvive().getOverworld(), x, 0, z);
            location.setY(location.getWorld().getHighestBlockYAt(x, z));
            event.setSpawnLocation(location.add(0.5, 0, 0.5));

            Value<Integer> updateCount = database.submit(new SetSpawn(setSpawnStatement, id, new Spawn(location)), this);
            if (updateCount == null) return;

            if (updateCount.get() == 1 && hasPlayedBefore) {
                warning = "Stored spawn for player that has played before." + System.lineSeparator() + "player=" + player;
            }
        } else if (!hasPlayedBefore) {
            warning = "Spawn is stored for player that has never played before." + System.lineSeparator() + "player=" + player;
        }
        if (warning != null) getLogger().warn(warning);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (event.isBedSpawn()) return;
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
