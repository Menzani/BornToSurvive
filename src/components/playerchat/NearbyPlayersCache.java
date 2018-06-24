package it.menzani.bts.components.playerchat;

import it.menzani.bts.components.ComponentTask;
import it.menzani.bts.components.SimpleComponent;
import it.menzani.bts.components.SimpleComponentTask;
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
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import java.util.*;
import java.util.stream.Collectors;

class NearbyPlayersCache extends SimpleComponentTask implements Listener {
    private static final Set<TeleportCause> unnaturalTeleportCauses =
            EnumSet.of(TeleportCause.SPECTATE, TeleportCause.COMMAND, TeleportCause.PLUGIN);

    private double distance, netherDistance;
    private final Map<Player, Set<Player>> cache = Collections.synchronizedMap(new HashMap<>());

    NearbyPlayersCache(SimpleComponent component, double distance) {
        super(component);
        setDistance(distance);
    }

    void setDistance(double distance) {
        this.distance = Math.pow(distance, 2);
        netherDistance = Math.pow(distance / 8, 2);
    }

    Set<Player> getNearbyPlayers(Player player) {
        return cache.get(player);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        cache.put(player, Collections.singleton(player));
        updateCacheLater(player);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.isCancelled() ||
                !unnaturalTeleportCauses.contains(event.getCause()) ||
                event.getFrom().getWorld() != event.getTo().getWorld()) return;
        Player player = event.getPlayer();
        updateCacheLater(player);
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
        getBornToSurvive().getServer().getOnlinePlayers().forEach(this::updateCache);
    }

    private void updateCacheLater(Player player) {
        ComponentTask task = getComponent().newWrappedRunnableTask(() -> updateCache(player));
        task.runTask();
    }

    private void updateCache(Player player) {
        updateCache(player, new HashSet<>());
    }

    private void updateCache(Player player, Set<Player> processed) {
        if (!cache.containsKey(player)) {
            return;
        }
        World world = player.getWorld();
        Location location = player.getLocation();
        double x = location.getX();
        double z = location.getZ();
        Set<Player> nearbyPlayers = new HashSet<>();
        switch (getBornToSurvive().matchWorld(world)) {
            case NORMAL:
                double x2 = x / 8;
                double z2 = z / 8;
                nearbyPlayers.addAll(computeCache(player, x2, z2, false));
                nearbyPlayers.addAll(computeCache(player, x, z, true));
                break;
            case NETHER:
                x2 = x * 8;
                z2 = z * 8;
                if (x2 < -29999872) x2 = -29999872;
                if (x2 > 29999872) x2 = 29999872;
                if (z2 < -29999872) z2 = -29999872;
                if (z2 > 29999872) z2 = 29999872;
                nearbyPlayers.addAll(computeCache(player, x2, z2, true));
                nearbyPlayers.addAll(computeCache(player, x, z, false));
                break;
            case THE_END:
                nearbyPlayers.addAll(world.getPlayers());
                break;
        }
        Set<Player> oldNearbyPlayers = cache.put(player, nearbyPlayers);
        assert oldNearbyPlayers != null;

        Set<Player> processing = symmetricDifference(nearbyPlayers, oldNearbyPlayers);
        processing.removeAll(processed);
        processed.add(player);
        for (Player nearbyPlayer : processing) {
            updateCache(nearbyPlayer, processed);
        }
    }

    private static <T> Set<T> symmetricDifference(Set<? extends T> one, Set<? extends T> two) {
        Set<T> result = new HashSet<>(one);
        result.addAll(two);
        Set<T> temp = new HashSet<>(one);
        temp.retainAll(two);
        result.removeAll(temp);
        return result;
    }

    private Set<Player> computeCache(Player player, double x, double z, boolean inOverworld) {
        World world = inOverworld ? getBornToSurvive().getOverworld() : getBornToSurvive().getNether();
        if (world == null) return Collections.emptySet();
        double distance = inOverworld ? this.distance : netherDistance;
        return world.getPlayers().stream()
                .filter(other -> player.equals(other) ||
                        distanceSquared(x, z, other.getLocation()) < distance)
                .collect(Collectors.toSet());
    }

    private static double distanceSquared(double x1, double z1, Location two) {
        return Math.pow(x1 - two.getX(), 2) + Math.pow(z1 - two.getZ(), 2);
    }
}
