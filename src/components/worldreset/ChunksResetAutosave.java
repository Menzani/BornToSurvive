package it.menzani.bts.components.worldreset;

import it.menzani.bts.components.SimpleComponent;
import it.menzani.bts.components.SimpleComponentTask;
import org.bukkit.World;

import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

class ChunksResetAutosave extends SimpleComponentTask {
    private final PreparedStatement setChunksResetStatement;
    private final Map<World, Set<ChunkLocation>> chunksReset;

    ChunksResetAutosave(SimpleComponent component, PreparedStatement setChunksResetStatement, Map<World, Set<ChunkLocation>> chunksReset) {
        super(component);
        this.setChunksResetStatement = setChunksResetStatement;
        this.chunksReset = chunksReset;
    }

    @Override
    public void run() {
        Map<World, String> chunksResetCompact = new HashMap<>();
        for (var entry : chunksReset.entrySet()) {
            var element = computeCompactElement(entry.getValue());
            chunksResetCompact.put(entry.getKey(), element);
        }
        getBornToSurvive().getDatabase().execute(new SetChunksReset(setChunksResetStatement, chunksResetCompact), getComponent());
    }

    private static String computeCompactElement(Set<ChunkLocation> element) {
        return element.parallelStream()
                .map(ChunkLocation::toCompactString)
                .collect(Collectors.joining(WorldReset.chunksResetCompactSeparator));
    }
}
