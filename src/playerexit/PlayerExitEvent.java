package it.menzani.bts.playerexit;

import org.bukkit.entity.Player;

public class PlayerExitEvent {
    private final Player player;
    private final boolean shutdown;

    PlayerExitEvent(Player player, boolean shutdown) {
        this.player = player;
        this.shutdown = shutdown;
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isShutdown() {
        return shutdown;
    }
}
