package it.menzani.bts.components.worldreset;

import it.menzani.bts.components.SimpleComponent;
import it.menzani.bts.components.SimpleComponentTask;

import java.sql.PreparedStatement;
import java.util.Set;
import java.util.stream.Collectors;

class ChunksResetAutosave extends SimpleComponentTask {
    private final PreparedStatement setChunksResetStatement;
    private final Set<ChunkLocation> chunksReset;

    ChunksResetAutosave(SimpleComponent component, PreparedStatement setChunksResetStatement, Set<ChunkLocation> chunksReset) {
        super(component);
        this.setChunksResetStatement = setChunksResetStatement;
        this.chunksReset = chunksReset;
    }

    @Override
    public void run() {
        String chunksResetCompact = chunksReset.stream() // TODO Parallelize?
                .map(ChunkLocation::toCompactString)
                .collect(Collectors.joining(WorldReset.chunksResetCompactSeparator));
        getBornToSurvive().getDatabase().execute(new SetChunksReset(setChunksResetStatement, chunksResetCompact), getComponent());
    }
}
