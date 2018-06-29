package it.menzani.bts.components.worldreset;

import it.menzani.bts.components.SimpleComponent;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.world.ChunkLoadEvent;

import java.util.Set;

class ResetPhase extends NonePhase {
    private final Set<?> markedArea;
    private final Set<ChunkLocation> chunksReset;

    ResetPhase(SimpleComponent component, Set<?> markedArea, Set<ChunkLocation> chunksReset) {
        super(component);
        this.markedArea = markedArea;
        this.chunksReset = chunksReset;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChunkLoad(ChunkLoadEvent event) {
        Chunk chunk = event.getChunk();
        ChunkLocation chunkLocation = new ChunkLocation(chunk);
        if (event.isNewChunk()) {
            chunksReset.add(chunkLocation);
            return;
        }
        if (chunksReset.contains(chunkLocation)) {
            return;
        }
        if (markedArea.contains(chunkLocation)) {
            return;
        }
        World world = chunk.getWorld();
        world.regenerateChunk(chunk.getX(), chunk.getZ());
        chunksReset.add(chunkLocation);
    }
}
