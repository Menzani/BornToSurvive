package it.menzani.bts.components.worldreset;

import it.menzani.bts.components.SimpleComponent;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.world.ChunkLoadEvent;

import java.util.Map;
import java.util.Set;

class ResetPhase extends NonePhase {
    private final MarkedArea markedArea;
    private final Map<World, Set<ChunkLocation>> chunksReset;

    ResetPhase(SimpleComponent component, MarkedArea markedArea, Map<World, Set<ChunkLocation>> chunksReset) {
        super(component);
        this.markedArea = markedArea;
        this.chunksReset = chunksReset;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChunkLoad(ChunkLoadEvent event) {
        World world = event.getWorld();
        Chunk chunk = event.getChunk();
        ChunkLocation chunkLocation = new ChunkLocation(chunk);
        if (event.isNewChunk()) {
            chunksReset.get(world).add(chunkLocation);
            return;
        }
        if (chunksReset.get(world).contains(chunkLocation)) {
            return;
        }
        if (markedArea.area.get(world).contains(chunkLocation)) {
            return;
        }
        world.regenerateChunk(chunk.getX(), chunk.getZ());
        chunksReset.get(world).add(chunkLocation);
    }
}
