package it.menzani.bts.components.lighthelper;

import it.menzani.bts.components.SimpleComponent;
import it.menzani.bts.components.SimpleComponentTask;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import java.time.Duration;

class DarknessFinder extends SimpleComponentTask {
    private static final int range = 8, yRange = 4;

    private final Player player;
    private final Counter counter = newCounter(Duration.ofSeconds(1), false);
    private final Block[] blocksToHighlight = new Block[(range + 1) * (range + 1) * (yRange + 1)];
    private int blocksToHighlightLength, blocksToHighlightIndex;

    DarknessFinder(SimpleComponent component, Player player) {
        super(component);
        this.player = player;
    }

    @Override
    public void run() {
        if (counter.tick()) {
            blocksToHighlightLength = 0;
            blocksToHighlightIndex = 0;

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
                        if (block.getLightFromBlocks() > 7) {
                            continue;
                        }
                        if (!isTransparent(block.getType())) {
                            continue;
                        }
                        Block blockBelow = block.getRelative(BlockFace.DOWN);
                        if (blockBelow.isLiquid()) {
                            continue;
                        }
                        if (isTransparent(blockBelow.getType())) {
                            continue;
                        }
                        blocksToHighlight[blocksToHighlightLength++] = block;
                    }
                }
            }
        }

        long blocksPerTick = blocksToHighlightLength / counter.getActualTicks();
        if (counter.peekNextTick()) {
            blocksPerTick += blocksToHighlightLength % counter.getActualTicks();
        }
        for (int i = 0; i < blocksPerTick; i++) {
            highlight(blocksToHighlight[blocksToHighlightIndex++]);
        }
    }

    @SuppressWarnings("deprecation")
    private static boolean isTransparent(Material material) {
        return material.isTransparent();
    }

    private void highlight(Block block) {
        player.spawnParticle(Particle.FLAME, block.getX() + 0.5, block.getY() + 0.5, block.getZ() + 0.5, 1, 0, 0, 0, 0);
    }
}
