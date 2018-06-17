package it.menzani.bts.minecartspeed;

import it.menzani.bts.BornToSurvive;
import it.menzani.bts.SimpleComponent;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.vehicle.VehicleCreateEvent;

import java.util.EnumSet;
import java.util.Set;

public class MinecartSpeed extends SimpleComponent {
    private static final Set<EntityType> VEHICLE_TYPES = EnumSet.of(EntityType.MINECART, EntityType.MINECART_CHEST);

    public MinecartSpeed(BornToSurvive bornToSurvive) {
        super(bornToSurvive);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onVehicleCreate(VehicleCreateEvent event) {
        if (event.isCancelled()) return;
        Vehicle vehicle = event.getVehicle();
        if (!VEHICLE_TYPES.contains(vehicle.getType())) return;
        Minecart minecart = (Minecart) vehicle;
        minecart.setMaxSpeed(1.5);
    }
}
