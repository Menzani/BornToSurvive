package it.menzani.bts.components.fix;

import it.menzani.bts.BornToSurvive;
import it.menzani.bts.components.SimpleComponent;
import it.menzani.bts.components.minecartspeed.MinecartSpeed;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.vehicle.VehicleBlockCollisionEvent;
import org.bukkit.util.Vector;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class Fix extends SimpleComponent {
    private static final Set<Material> waterMaterials = EnumSet.of(Material.STATIONARY_WATER, Material.WATER);
    private static final Set<Material> railMaterials = EnumSet.of(Material.RAILS, Material.POWERED_RAIL, Material.DETECTOR_RAIL, Material.ACTIVATOR_RAIL);
    private static final BlockFace[] cardinalBlockFaces = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};

    public Fix(BornToSurvive bornToSurvive) {
        super(bornToSurvive);
    }

    @EventHandler
    public void preventWaterSpawn(PlayerSpawnLocationEvent event) {
        Player player = event.getPlayer();
        if (player.hasPlayedBefore()) return;
        Block spawnBlock = event.getSpawnLocation().getBlock();
        if (!waterMaterials.contains(spawnBlock.getType())) return;
        World world = spawnBlock.getWorld();
        int x = spawnBlock.getX();
        int z = spawnBlock.getZ();
        for (int y = spawnBlock.getY(); y <= world.getSeaLevel(); y++) {
            Block block = world.getBlockAt(x, y, z);
            if (waterMaterials.contains(block.getType())) continue;
            event.setSpawnLocation(block.getLocation());
            // TODO Add potion effect of Slow Falling
            break;
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void allowHighVelocityMinecarts(VehicleBlockCollisionEvent event) {
        Vehicle vehicle = event.getVehicle();
        if (!MinecartSpeed.minecartEntityTypes.contains(vehicle.getType())) return;
        Block block = event.getBlock();
        Block blockUp = block.getRelative(BlockFace.UP);
        if (!railMaterials.contains(blockUp.getType())) return;
        for (BlockFace blockFace : cardinalBlockFaces) {
            Block rail = block.getRelative(blockFace);
            if (!railMaterials.contains(rail.getType())) continue;
            List<Entity> passengers = vehicle.getPassengers();
            Location nextRail = blockUp.getLocation();
            for (Entity passenger : passengers) {
                Location location = passenger.getLocation();
                nextRail.setYaw(location.getYaw());
                nextRail.setPitch(location.getPitch());
                passenger.teleport(nextRail);
            }
            vehicle.teleport(nextRail);
            passengers.forEach(vehicle::addPassenger);
            BlockFace oppositeBlockFace = blockFace.getOppositeFace();
            vehicle.setVelocity(new Vector(oppositeBlockFace.getModX(), 0, oppositeBlockFace.getModZ()));
            break;
        }
    }
}
