package it.menzani.bts.chat;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

class NearbyPlayersCache extends BukkitRunnable {
    private final Server server;
    private final Map<Player, Set<Player>> cache = new HashMap<>();

    NearbyPlayersCache(Server server) {
        this.server = server;
    }

    synchronized Set<Player> getNearbyPlayers(Player player) {
        return cache.getOrDefault(player, Collections.singleton(player));
    }

    @Override
    public synchronized void run() {
        cache.clear();
        for (Player online : server.getOnlinePlayers()) {
            Set<Player> nearbyPlayers = online.getWorld().getPlayers().stream()
                    .filter(player -> online.equals(player) || online.getLocation().distanceSquared(player.getLocation()) < 160 * 160)
                    .collect(Collectors.toSet());
            cache.put(online, nearbyPlayers);
        }
    }
}
