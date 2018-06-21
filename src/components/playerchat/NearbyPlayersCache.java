package it.menzani.bts.components.playerchat;

import it.menzani.bts.BornToSurvive;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.Collectors;

class NearbyPlayersCache extends BukkitRunnable implements Listener {
    private final BornToSurvive bornToSurvive;
    private double distance;
    private final Map<Player, Set<Player>> cache = Collections.synchronizedMap(new HashMap<>());

    NearbyPlayersCache(BornToSurvive bornToSurvive, double distance) {
        this.bornToSurvive = bornToSurvive;
        setDistance(distance);
    }

    void setDistance(double distance) {
        this.distance = Math.pow(distance, 2);
    }

    Set<Player> getNearbyPlayers(Player player) {
        return cache.get(player);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        cache.put(player, null);
        updateCache(player);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.isCancelled()) return;
        Player player = event.getPlayer();
        updateCache(player);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        updateCache(player);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        cache.remove(player);
    }

    @Override
    public void run() {
        bornToSurvive.getServer().getOnlinePlayers().forEach(this::updateCache);
    }

    private void updateCache(Player player) {
        if (!cache.containsKey(player)) {
            return;
        }
        Location location = player.getLocation();
        double x = location.getX();
        double z = location.getZ();
        Set<Player> nearbyPlayers = new HashSet<>();
        World world = player.getWorld();
        switch (bornToSurvive.matchWorld(world)) {
            case NORMAL:
                double x2 = Math.floor(x / 8);
                double z2 = Math.floor(z / 8);
                nearbyPlayers.addAll(computeCache(world, player, x2, z2, distance / 8));
                nearbyPlayers.addAll(computeCache(world, player, x, z, distance));
                break;
            case NETHER:
                x2 = Math.floor(x * 8);
                z2 = Math.floor(z * 8);
                if (x2 < -29999872) x2 = -29999872;
                if (x2 > 29999872) x2 = 29999872;
                if (z2 < -29999872) z2 = -29999872;
                if (z2 > 29999872) z2 = 29999872;
                nearbyPlayers.addAll(computeCache(world, player, x2, z2, distance));
                nearbyPlayers.addAll(computeCache(world, player, x, z, distance / 8));
                break;
            case THE_END:
                nearbyPlayers.addAll(world.getPlayers());
                break;
        }
        cache.put(player, nearbyPlayers);
    }

    private Set<Player> computeCache(World world, Player player, double x, double z, double distance) {
        return world.getPlayers().stream()
                .filter(other -> player.equals(other) ||
                        distanceSquared(x, z, other.getLocation()) < distance)
                .collect(Collectors.toSet());
    }

    private static double distanceSquared(double x1, double z1, Location two) {
        return Math.pow(x1 - two.getX(), 2) + Math.pow(z1 - two.getZ(), 2);
    }
}
