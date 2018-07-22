package it.menzani.bts.components.serverview;

import it.menzani.bts.BornToSurvive;
import it.menzani.bts.ChatColors;
import it.menzani.bts.components.SimpleComponent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.server.ServerListPingEvent;

public class ServerView extends SimpleComponent {
    public ServerView(BornToSurvive bornToSurvive) {
        super(bornToSurvive);
    }

    @EventHandler
    public void onServerListPing(ServerListPingEvent event) {
        String motd = event.getMotd()
                .replace("{SERVER}", getBornToSurvive().getServerName())
                .replace("{VERSION}", getBornToSurvive().getDescription().getVersion());
        event.setMotd(ChatColors.translate(motd));
        event.setMaxPlayers(Math.max(event.getNumPlayers() + 1, 50));
    }
}
