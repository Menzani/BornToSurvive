package it.menzani.bts.components.optimize;

import it.menzani.bts.components.ComponentListener;
import it.menzani.bts.components.ComponentTask;
import it.menzani.bts.components.SimpleComponent;
import it.menzani.bts.components.SimpleComponentTask;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerJoinEvent;

import java.time.Duration;

class ViewDistanceAdjuster extends SimpleComponentTask implements ComponentListener {
    private static final double minTPS = 19.5, goodTPS = 19.8;
    private static final int minViewDistance = 5, maxViewDistance = 20;

    private final Server server;
    private int viewDistance = maxViewDistance;

    ViewDistanceAdjuster(SimpleComponent component) {
        super(component);
        server = getBornToSurvive().getServer();
    }

    boolean isServerViewDistanceWrong() {
        if (server.getViewDistance() != maxViewDistance) {
            getLogger().fatal("Please set 'view-distance=" + maxViewDistance + "' in server.properties file.");
            return true;
        }
        return false;
    }

    @Override
    public void register() {
        getBornToSurvive().registerListener(this);
    }

    @Override
    public void unregister() {
        HandlerList.unregisterAll(this);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.setViewDistance(viewDistance);
        ComponentTask task = getComponent().newWrappedRunnableTask(() -> update(player));
        task.runTaskLater(Duration.ofSeconds(3));
    }

    @Override
    public void run() {
        double tps = server.getTPS()[0];
        if (tps < minTPS && viewDistance > minViewDistance) {
            viewDistance--;
        } else if (tps > goodTPS && viewDistance < maxViewDistance) {
            viewDistance++;
        }

        server.getOnlinePlayers().forEach(this::update);
    }

    private void update(Player player) {
        player.setViewDistance(Math.min(player.getClientViewDistance(), viewDistance));
    }
}
