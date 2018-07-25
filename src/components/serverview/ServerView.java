package it.menzani.bts.components.serverview;

import it.menzani.bts.BornToSurvive;
import it.menzani.bts.ChatColors;
import it.menzani.bts.components.SimpleComponent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.server.ServerListPingEvent;

public class ServerView extends SimpleComponent {
    private static final int[] maxPlayersThresholds = {50, 100, 150, 200, 250, 300, 350, 400, 450, 500};

    private String motd = "Starting up...";

    public ServerView(BornToSurvive bornToSurvive) {
        super(bornToSurvive);
    }

    @Override
    public void load() {
        motd = ChatColors.translate(
                getBornToSurvive().getServer().getMotd()
                        .replace("{SERVER}", getBornToSurvive().getServer().getServerName())
                        .replace("{VERSION}", getBornToSurvive().getDescription().getVersion()));
    }

    @EventHandler
    public void onServerListPing(ServerListPingEvent event) {
        event.setMotd(motd);
        event.setMaxPlayers(maxPlayersFor(event.getNumPlayers()));
    }

    private static int maxPlayersFor(int numPlayers) {
        for (int threshold : maxPlayersThresholds) {
            if (numPlayers < threshold - 10) {
                return threshold;
            }
        }
        return ++numPlayers;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void ignoreMaxPlayers(AsyncPlayerPreLoginEvent event) {
        if (event.getLoginResult() != AsyncPlayerPreLoginEvent.Result.KICK_FULL) return;
        event.allow();
    }
}
