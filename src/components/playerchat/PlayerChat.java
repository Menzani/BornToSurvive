package it.menzani.bts.components.playerchat;

import it.menzani.bts.BornToSurvive;
import it.menzani.bts.components.SimpleComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.time.Duration;
import java.util.Set;

public class PlayerChat extends SimpleComponent {
    private static final double nearbyPlayersDistance = Double.MAX_VALUE; // In blocks

    private NearbyPlayersCache nearbyPlayersCache;

    public PlayerChat(BornToSurvive bornToSurvive) {
        super(bornToSurvive);
    }

    @Override
    public void loadPreWorld() {
        super.loadPreWorld();
        nearbyPlayersCache = new NearbyPlayersCache(this, nearbyPlayersDistance);
        nearbyPlayersCache.register();
    }

    @Override
    public void load() {
        nearbyPlayersCache.runTaskTimer(Duration.ofMinutes(1));
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        String deathMessage = event.getDeathMessage();
        event.setDeathMessage(null);
        Player player = event.getEntity();
        player.sendMessage(deathMessage);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);

        Player player = event.getPlayer();
        player.setDisplayName(ChatColor.YELLOW + player.getName());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
    }

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        event.setFormat(" %1$s " + ChatColor.RESET + "%2$s");

        Set<Player> recipients = event.getRecipients();
        Player player = event.getPlayer();
        Set<Player> nearbyPlayers = nearbyPlayersCache.getNearbyPlayers(player);
        try {
            recipients.retainAll(nearbyPlayers);
        } catch (UnsupportedOperationException e) {
            getLogger().fatal("Could not modify the recipients of a chat message." +
                    System.lineSeparator() + "player=" + player + ", recipients=" + recipients + ", message='" + event.getMessage() + '\'');
            e.printStackTrace();
        }
    }
}
