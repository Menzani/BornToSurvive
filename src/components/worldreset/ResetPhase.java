package it.menzani.bts.components.worldreset;

import it.menzani.bts.components.SimpleComponent;
import it.menzani.bts.components.SimpleComponentListener;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.world.ChunkLoadEvent;

import java.util.Set;
import java.util.stream.Stream;

class ResetPhase extends SimpleComponentListener {
    private final Set<?> markedArea;
    private final Set<ChunkLocation> chunksReset;

    ResetPhase(SimpleComponent component, Set<?> markedArea, Set<ChunkLocation> chunksReset) {
        super(component);
        this.markedArea = markedArea;
        this.chunksReset = chunksReset;
    }

    Stream<ChunkLocation> getChunksReset() {
        return chunksReset.stream();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChunkLoad(ChunkLoadEvent event) {
        if (event.isNewChunk()) return;
        Chunk chunk = event.getChunk();
        ChunkLocation chunkLocation = new ChunkLocation(chunk);
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
