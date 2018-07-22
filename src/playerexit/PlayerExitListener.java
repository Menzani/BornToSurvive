package it.menzani.bts.playerexit;

import org.bukkit.event.Listener;

public interface PlayerExitListener extends Listener {
    void onPlayerExit(PlayerExitEvent event);
}
