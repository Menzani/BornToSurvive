package it.menzani.bts.components.fix;

import it.menzani.bts.BornToSurvive;
import it.menzani.bts.components.SimpleComponent;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import java.util.EnumSet;
import java.util.Set;

public class Fix extends SimpleComponent {
    private static final Set<Material> waterMaterials = EnumSet.of(Material.STATIONARY_WATER, Material.WATER);

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
}
