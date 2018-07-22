package it.menzani.bts.components.assistant;

import it.menzani.bts.components.SimpleComponent;
import it.menzani.bts.components.SimpleComponentListener;
import it.menzani.bts.persistence.sql.wrapper.Value;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.sql.PreparedStatement;
import java.util.HashSet;
import java.util.Set;

class WelcomeGuide extends SimpleComponentListener {
    private final PreparedStatement setWelcomeGuideStatement, getWelcomeGuideStatement;
    private final Set<Player> welcoming = new HashSet<>();

    WelcomeGuide(SimpleComponent component, PreparedStatement setWelcomeGuideStatement, PreparedStatement getWelcomeGuideStatement) {
        super(component);
        this.setWelcomeGuideStatement = setWelcomeGuideStatement;
        this.getWelcomeGuideStatement = getWelcomeGuideStatement;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        Value<Boolean> welcomeGuide = getBornToSurvive().getDatabase().submit(new GetWelcomeGuide(getWelcomeGuideStatement,
                player.getUniqueId()), getComponent());
        if (welcomeGuide == null) return;

        if (welcomeGuide.isNull()) {
            getLogger().fatal("Welcome guide completion status is not stored." + System.lineSeparator() + "player=" + player);
            return;
        }
        if (welcomeGuide.get()) {
            return;
        }
        player.setGameMode(GameMode.SPECTATOR);
        player.setFlySpeed(0);
        welcoming.add(player);
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.getCause() != PlayerTeleportEvent.TeleportCause.SPECTATE) return;
        Player player = event.getPlayer();
        if (!welcoming.contains(player)) return;
        event.setCancelled(true);
        player.setSpectatorTarget(null);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        boolean contained = welcoming.remove(player);
        if (!contained) return;
        player.setFlySpeed(0.1F);
        player.setGameMode(GameMode.SURVIVAL);
    }
}
