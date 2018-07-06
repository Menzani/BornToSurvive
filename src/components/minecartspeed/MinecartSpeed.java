package it.menzani.bts.components.minecartspeed;

import it.menzani.bts.BornToSurvive;
import it.menzani.bts.components.SimpleComponent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.vehicle.VehicleBlockCollisionEvent;
import org.bukkit.event.vehicle.VehicleCreateEvent;
import org.bukkit.util.Vector;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class MinecartSpeed extends SimpleComponent {
    private static final Set<EntityType> minecartEntityTypes = EnumSet.of(EntityType.MINECART, EntityType.MINECART_CHEST);
    private static final Set<Material> railMaterials = EnumSet.of(Material.RAILS, Material.POWERED_RAIL, Material.DETECTOR_RAIL, Material.ACTIVATOR_RAIL);
    private static final BlockFace[] cardinalBlockFaces = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};

    public MinecartSpeed(BornToSurvive bornToSurvive) {
        super(bornToSurvive);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onVehicleCreate(VehicleCreateEvent event) {
        Vehicle vehicle = event.getVehicle();
        if (!minecartEntityTypes.contains(vehicle.getType())) return;
        Minecart minecart = (Minecart) vehicle;
        minecart.setMaxSpeed(1.5);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onVehicleBlockCollision(VehicleBlockCollisionEvent event) {
        Vehicle vehicle = event.getVehicle();
        if (!minecartEntityTypes.contains(vehicle.getType())) return;
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
