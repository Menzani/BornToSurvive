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
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

class ViewDistanceAdjuster extends SimpleComponentTask implements ComponentListener {
    private static final double minTPS = 19.5, goodTPS = 19.8;
    private static final int minViewDistance = 10, maxViewDistance = 20, netherViewDistance = 7;

    private final Server server;
    private final Set<Player> justJoined = new HashSet<>();
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
        justJoined.add(player);
        ComponentTask task = getComponent().newWrappedRunnableTask(() -> {
            if (player.isOnline()) {
                update(player);
            }
            justJoined.remove(player);
        });
        task.runTaskLater(Duration.ofSeconds(5));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        update(player);
    }

    @Override
    public void run() {
        double tps = server.getTPS()[0];
        if (tps < minTPS && viewDistance > minViewDistance) {
            viewDistance--;
        } else if (tps > goodTPS && viewDistance < maxViewDistance) {
            viewDistance++;
        }

        Set<Player> online = new HashSet<>(server.getOnlinePlayers());
        online.removeAll(justJoined);
        online.forEach(this::update);
    }

    private void update(Player player) {
        int viewDistance = this.viewDistance;
        if (player.getWorld() == getBornToSurvive().getNether()) {
            viewDistance = Math.min(netherViewDistance, viewDistance);
        }
        player.setViewDistance(Math.min(player.getClientViewDistance(), viewDistance));
    }
}
