package it.menzani.bts.components.playerchat;

import it.menzani.bts.BornToSurvive;
import it.menzani.bts.components.SimpleComponent;
import it.menzani.logger.api.Logger;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.time.Duration;
import java.util.Set;
import java.util.function.Consumer;

public class PlayerChat extends SimpleComponent {
    private static final double nearbyPlayersDistance = 10000; // In blocks

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
        String deathMessage = ChatColor.GRAY + event.getDeathMessage();
        event.setDeathMessage(null);
        Player player = event.getEntity();
        Player killer = player.getKiller();
        player.sendMessage(deathMessage);
        if (killer != null) {
            killer.sendMessage(deathMessage);
        }
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
        event.setFormat(" %1$s " + ChatColor.RESET + "%2$s");

        Player player = event.getPlayer();
        Set<Player> nearbyPlayers = nearbyPlayersCache.getNearbyPlayers(player);
        modifyRecipients(event.getRecipients(), _recipients -> _recipients.retainAll(nearbyPlayers), getLogger(), player, event.getMessage());
    }

    public static void modifyRecipients(Set<? extends CommandSender> recipients, Consumer<? super Set<? extends CommandSender>> action,
                                        Logger logger, CommandSender sender, String message) {
        try {
            action.accept(recipients);
        } catch (UnsupportedOperationException e) {
            logger.fatal("Could not modify the recipients of a chat message." + System.lineSeparator() +
                    (sender == null ? "" : "sender=" + sender + ", ") +
                    "recipients=" + recipients + ", message='" + message + '\'');
            e.printStackTrace();
        }
    }
}
