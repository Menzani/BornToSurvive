package it.menzani.bts.playerexit;

import it.menzani.bts.BornToSurvive;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashSet;
import java.util.Set;

public class PlayerExit implements Listener {
    private final BornToSurvive bornToSurvive;
    private final Set<PlayerExitListener> listeners = new HashSet<>();

    public PlayerExit(BornToSurvive bornToSurvive) {
        this.bornToSurvive = bornToSurvive;
    }

    public void register() {
        bornToSurvive.registerListener(this);
    }

    public void addListener(PlayerExitListener listener) {
        listeners.add(listener);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        callEvent(new PlayerExitEvent(player, false));
    }

    public void shutdown() {
        for (Player player : bornToSurvive.getServer().getOnlinePlayers()) {
            callEvent(new PlayerExitEvent(player, true));
        }
    }

    private void callEvent(PlayerExitEvent event) {
        for (PlayerExitListener listener : listeners) {
            listener.onPlayerExit(event);
        }
    }
}
