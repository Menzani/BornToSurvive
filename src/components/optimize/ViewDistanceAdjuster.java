package it.menzani.bts.components.optimize;

import it.menzani.bts.components.ComponentListener;
import it.menzani.bts.components.SimpleComponent;
import it.menzani.bts.components.SimpleComponentTask;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerJoinEvent;

class ViewDistanceAdjuster extends SimpleComponentTask implements ComponentListener {
    private static final double minTPS = 19.5, goodTPS = 19.8;
    private static final int minViewDistance = 5, maxViewDistance = 20;

    private final Server server;
    private int viewDistance;

    ViewDistanceAdjuster(SimpleComponent component) {
        super(component);
        server = getBornToSurvive().getServer();
        viewDistance = server.getViewDistance();
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
    }

    @Override
    public void run() {
        double tps = server.getTPS()[0];
        if (tps < minTPS && viewDistance > minViewDistance) {
            viewDistance--;
        } else if (tps > goodTPS && viewDistance < maxViewDistance) {
            viewDistance++;
        } else return;
        update();
    }

    private void update() {
        for (Player player : server.getOnlinePlayers()) {
            player.setViewDistance(viewDistance);
        }
        getLogger().fine("viewDistance = " + viewDistance);
    }
}
