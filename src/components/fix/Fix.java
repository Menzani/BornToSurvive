package it.menzani.bts.components.fix;

import it.menzani.bts.BornToSurvive;
import it.menzani.bts.User;
import it.menzani.bts.components.ComponentTask;
import it.menzani.bts.components.SimpleComponent;
import it.menzani.bts.components.minecartspeed.MinecartSpeed;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.vehicle.VehicleBlockCollisionEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import java.time.Duration;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class Fix extends SimpleComponent {
    private static final Set<Material> railMaterials = EnumSet.of(Material.RAIL, Material.POWERED_RAIL, Material.DETECTOR_RAIL, Material.ACTIVATOR_RAIL);
    private static final BlockFace[] cardinalBlockFaces = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};

    public Fix(BornToSurvive bornToSurvive) {
        super(bornToSurvive);
    }

    @EventHandler
    public void preventWaterSpawn(PlayerSpawnLocationEvent event) {
        User player = new User(event.getPlayer());
        if (player.hasPlayedBefore()) return;
        Block spawnBlock = event.getSpawnLocation().getBlock();
        if (!isWater(spawnBlock)) return;
        World world = spawnBlock.getWorld();
        int x = spawnBlock.getX();
        int z = spawnBlock.getZ();
        for (int y = spawnBlock.getY(); y <= world.getSeaLevel(); y++) {
            Block block = world.getBlockAt(x, y, z);
            if (isWater(block)) continue;
            event.setSpawnLocation(block.getLocation().add(0.5, -0.5, 0.5));
            giveSlowFallingEffect(player);
            break;
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (event.isBedSpawn()) return;
        Location respawnLocation = event.getRespawnLocation();
        if (!isWater(respawnLocation.getBlock())) return;
        event.setRespawnLocation(respawnLocation.add(0, 0.5, 0));
        User player = new User(event.getPlayer());
        giveSlowFallingEffect(player);
    }

    private static boolean isWater(Block block) {
        return block.getType() == Material.WATER;
    }

    private void giveSlowFallingEffect(User player) {
        ComponentTask task = getComponent().newWrappedRunnableTask(() ->
                player.addPotionEffect(PotionEffectType.SLOW_FALLING, Duration.ofSeconds(8), 1, User.PotionEffectParticleType.OFF));
        task.runTask();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void allowHighVelocityMinecarts(VehicleBlockCollisionEvent event) {
        Vehicle vehicle = event.getVehicle();
        if (!MinecartSpeed.minecartEntityTypes.contains(vehicle.getType())) return;
        Block block = event.getBlock();
        Block blockUp = block.getRelative(BlockFace.UP);
        if (!railMaterials.contains(blockUp.getType())) return;
        outer:
        for (BlockFace blockFace : cardinalBlockFaces) {
            Block rail = block.getRelative(blockFace);
            if (!railMaterials.contains(rail.getType())) continue;
            for (BlockFace _blockFace : cardinalBlockFaces) {
                Block _rail = blockUp.getRelative(_blockFace);
                if (!railMaterials.contains(_rail.getType())) continue;
                List<Entity> passengers = vehicle.getPassengers();
                Location nextRail = _rail.getLocation();
                for (Entity passenger : passengers) {
                    Location location = passenger.getLocation();
                    nextRail.setYaw(location.getYaw());
                    nextRail.setPitch(location.getPitch());
                    passenger.teleport(nextRail);
                }
                vehicle.teleport(nextRail);
                passengers.forEach(vehicle::addPassenger);
                vehicle.setVelocity(new Vector(_blockFace.getModX(), 0, _blockFace.getModZ()));
                break outer;
            }
        }
    }
}
