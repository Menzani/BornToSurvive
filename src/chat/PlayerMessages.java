package it.menzani.bts.chat;

import it.menzani.bts.BornToSurvive;
import it.menzani.bts.SimpleComponent;
import it.menzani.bts.time.TickDuration;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.time.Duration;
import java.util.Set;

public class PlayerMessages extends SimpleComponent {
    private final NearbyPlayersCache nearbyPlayersCache;

    public PlayerMessages(BornToSurvive bornToSurvive) {
        super(bornToSurvive);
        nearbyPlayersCache = new NearbyPlayersCache(bornToSurvive.getServer());
    }

    @Override
    public void load() {
        super.load();

        long period = TickDuration.from(Duration.ofSeconds(3));
        nearbyPlayersCache.runTaskTimer(getBornToSurvive(), period, period);
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
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
    }

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        event.setFormat(" %1$s " + ChatColor.WHITE + "%2$s");

        Set<Player> recipients = event.getRecipients();
        Player player = event.getPlayer();
        Set<Player> nearbyPlayers = nearbyPlayersCache.getNearbyPlayers(player);
        try {
            recipients.retainAll(nearbyPlayers);
        } catch (UnsupportedOperationException e) {
            getBornToSurvive().getRootLogger().fatal("Could not modify the recipients of a chat message." +
                    System.lineSeparator() + "player=" + player + ", recipients=" + recipients + ", message='" + event.getMessage() + '\'');
            e.printStackTrace();
        }
    }
}
