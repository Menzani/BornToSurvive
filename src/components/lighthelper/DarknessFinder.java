package it.menzani.bts.components.lighthelper;

import it.menzani.bts.components.SimpleComponent;
import it.menzani.bts.components.SimpleComponentTask;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

class DarknessFinder extends SimpleComponentTask {
    private static final int range = 8, yRange = 4;

    private final Player player;

    DarknessFinder(SimpleComponent component, Player player) {
        super(component);
        this.player = player;
    }

    @Override
    public void run() {
        Location location = player.getLocation();
        int centerX = location.getBlockX();
        int centerY = location.getBlockY();
        int centerZ = location.getBlockZ();
        World world = player.getWorld();
        for (int x = centerX - range; x <= centerX + range; x++) {
            for (int z = centerZ - range; z <= centerZ + range; z++) {
                int highestY = Math.min(centerY + yRange, world.getHighestBlockYAt(x, z));
                for (int y = centerY - yRange; y <= highestY; y++) {
                    Block block = world.getBlockAt(x, y, z);
                    if (block.getLightLevel() > 7) {
                        continue;
                    }
                    if (!block.getType().isTransparent()) {
                        continue;
                    }
                    if (block.getRelative(BlockFace.DOWN).getType().isTransparent()) {
                        continue;
                    }
                    highlight(x, y, z);
                }
            }
        }
    }

    private void highlight(double x, double y, double z) {
        player.spawnParticle(Particle.FLAME, x + 0.5, y + 0.5, z + 0.5, 1, 0, 0, 0, 0);
    }
}
