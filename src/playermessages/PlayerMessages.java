package it.menzani.bts.playermessages;

import it.menzani.bts.BornToSurvive;
import it.menzani.bts.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerMessages implements Component, Listener {
    private final BornToSurvive bornToSurvive;

    public PlayerMessages(BornToSurvive bornToSurvive) {
        this.bornToSurvive = bornToSurvive;
    }

    @Override
    public void load() {
        bornToSurvive.registerListener(this);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        event.setDeathMessage(null);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
    }
}
