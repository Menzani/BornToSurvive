package it.menzani.bts.components.assistant;

import it.menzani.bts.User;
import it.menzani.bts.components.ComponentTask;
import it.menzani.bts.components.SimpleComponent;
import it.menzani.bts.components.SimpleComponentListener;
import it.menzani.bts.components.playerchat.PlayerChat;
import it.menzani.bts.persistence.sql.wrapper.Value;
import it.menzani.bts.playerexit.PlayerExitEvent;
import it.menzani.bts.playerexit.PlayerExitListener;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.server.BroadcastMessageEvent;

import java.sql.PreparedStatement;
import java.time.Duration;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

class WelcomeGuide extends SimpleComponentListener implements PlayerExitListener {
    private final PreparedStatement setWelcomeGuideStatement, getWelcomeGuideStatement;
    private final Set<Player> welcoming = Collections.synchronizedSet(new HashSet<>());
    private String serverOwner;

    WelcomeGuide(SimpleComponent component, PreparedStatement setWelcomeGuideStatement, PreparedStatement getWelcomeGuideStatement) {
        super(component);
        this.setWelcomeGuideStatement = setWelcomeGuideStatement;
        this.getWelcomeGuideStatement = getWelcomeGuideStatement;
    }

    void setServerOwner(String serverOwner) {
        this.serverOwner = serverOwner;
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
        player.setFlySpeed(0);
        player.setGameMode(GameMode.SPECTATOR);
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

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (!welcoming.contains(player)) return;
        String label = event.getMessage().substring(1);
        if (label.equals(Assistant.playCommandLabel)) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (welcoming.contains(player)) {
            event.setCancelled(true);
        } else {
            //noinspection SuspiciousMethodCalls
            PlayerChat.modifyRecipients(event.getRecipients(), _recipients -> _recipients.removeAll(welcoming), getLogger(), player, event.getMessage());
        }
    }

    @EventHandler
    public void onBroadcastMessage(BroadcastMessageEvent event) {
        //noinspection SuspiciousMethodCalls
        PlayerChat.modifyRecipients(event.getRecipients(), _recipients -> _recipients.removeAll(welcoming), getLogger(), null, event.getMessage());
    }

    @Override
    public void onPlayerExit(PlayerExitEvent event) {
        Player player = event.getPlayer();
        boolean contained = welcoming.remove(player);
        if (!contained) return;
        play(player);
    }

    void play(User player) {
        if (!welcoming.contains(player)) {
            player.sendMessageFormat("You are already playing.");
            return;
        }
        Value<?> updateCount = getBornToSurvive().getDatabase().submit(new SetWelcomeGuide(setWelcomeGuideStatement,
                player.getUniqueId()), getComponent());
        if (updateCount == null) return;
        play((Player) player);
        ComponentTask task = getComponent().newWrappedRunnableTask(() -> welcome(player));
        task.runTaskLater(Duration.ofSeconds(1).plusMillis(500));
    }

    private static void play(Player player) {
        player.setGameMode(GameMode.SURVIVAL);
        player.setFlySpeed(0.1F);
    }

    private void welcome(Player player) {
        assert serverOwner != null;
        player.sendMessage(ChatColor.AQUA + "Welcome to " + ChatColor.RESET + getBornToSurvive().getServer().getServerName() +
                ChatColor.AQUA + " by " + ChatColor.RESET + serverOwner + ChatColor.AQUA + '!');
        welcoming.remove(player);
    }
}
