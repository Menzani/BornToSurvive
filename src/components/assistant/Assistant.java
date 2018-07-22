package it.menzani.bts.components.assistant;

import it.menzani.bts.BornToSurvive;
import it.menzani.bts.components.ComponentListener;
import it.menzani.bts.components.SimpleComponent;
import it.menzani.bts.persistence.sql.wrapper.Value;
import it.menzani.bts.persistence.sql.wrapper.WrappedSQLDatabase;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.PreparedStatement;
import java.util.Set;

public class Assistant extends SimpleComponent {
    private PreparedStatement createRowStatement;

    public Assistant(BornToSurvive bornToSurvive) {
        super(bornToSurvive);
    }

    @Override
    public void loadPreWorld() {
        WrappedSQLDatabase database = getBornToSurvive().getDatabase();

        Value<PreparedStatement[]> preparedStatements = database.submit(new PrepareStatements(), this);
        if (preparedStatements == null) return;
        createRowStatement = preparedStatements.get()[0];

        boolean error = database.execute(new CreateTable(), this);
        if (error) return;

        super.loadPreWorld();
        final Set<ComponentListener> listeners = Set.of(new WelcomeGuide(this, preparedStatements.get()[1], preparedStatements.get()[2]));
        listeners.forEach(ComponentListener::register);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        Value<Integer> updateCount = getBornToSurvive().getDatabase().submit(new CreateRow(createRowStatement,
                player.getUniqueId()), this);
        if (updateCount == null) return;

        boolean hasPlayedBefore = player.hasPlayedBefore();
        String warning = null;
        switch (updateCount.get()) {
            case 0:
                if (!hasPlayedBefore) warning = "Row is present for player that has never played before." +
                        System.lineSeparator() + "player=" + player;
                break;
            case 1:
                if (hasPlayedBefore) warning = "Created row for player that has played before." +
                        System.lineSeparator() + "player=" + player;
                break;
        }
        if (warning != null) getLogger().warn(warning);
    }
}
