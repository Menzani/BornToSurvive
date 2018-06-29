package it.menzani.bts.components.worldreset;

import it.menzani.bts.BornToSurvive;
import it.menzani.bts.components.ComponentListener;
import it.menzani.bts.components.SimpleComponent;
import it.menzani.bts.persistence.sql.wrapper.WrappedSQLDatabase;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class WorldReset extends SimpleComponent {
    static final String signText = "Do not reset me!";
    static final int markRange = 10;
    private static final char chunksResetCompactSeparator = ';';

    private final Phase phase;
    private ComponentListener phaseListener;
    private PreparedStatement setChunksResetStatement;

    public WorldReset(BornToSurvive bornToSurvive, Phase phase) {
        super(bornToSurvive);
        this.phase = phase;
    }

    @Override
    public void load() {
        if (phase == Phase.NONE) return;
        WrappedSQLDatabase database = getBornToSurvive().getDatabase();

        PreparedStatement[] preparedStatements =
                (PreparedStatement[]) database.submit(new PrepareStatements(), this);
        if (preparedStatements == null) return;
        ComponentListener phaseListener;
        switch (phase) {
            case MARK:
                phaseListener = new MarkPhase(this, preparedStatements[0], preparedStatements[1]);
                break;
            case RESET:
                Set<?> markedArea = (Set<?>) database.submit(new GetMarkedArea(), this);
                if (markedArea == null) return;
                String chunksResetCompact = (String) database.submit(new GetChunksReset(), this);
                if (chunksResetCompact == null) return;

                Set<ChunkLocation> chunksReset = new HashSet<>();
                try {
                    for (String string : split(chunksResetCompact)) {
                        chunksReset.add(ChunkLocation.fromCompactString(string));
                    }
                } catch (ParseException e) {
                    getLogger().throwable(e, "Could not parse chunk location.");
                    return;
                }
                phaseListener = new ResetPhase(this, markedArea, chunksReset);
                setChunksResetStatement = preparedStatements[2];
                break;
            default:
                throw new AssertionError();
        }

        boolean error = database.execute(new CreateTables(), this);
        if (error) return;

        phaseListener.register();
        this.phaseListener = phaseListener;
    }

    private static List<String> split(String chunksResetCompact) {
        List<String> result = new ArrayList<>();
        char[] characters = chunksResetCompact.toCharArray();
        int lastPosition = 0;
        for (int i = 0; i < characters.length; i++) {
            char character = characters[i];
            if (character == chunksResetCompactSeparator) {
                result.add(chunksResetCompact.substring(lastPosition, i));
                lastPosition = i + 1;
            }
        }
        if (lastPosition != characters.length) {
            result.add(chunksResetCompact.substring(lastPosition));
        }
        return result;
    }

    @Override
    public void unload() {
        if (phaseListener == null || phase != Phase.RESET) return;
        ResetPhase resetPhase = (ResetPhase) phaseListener;

        String chunksResetCompact = resetPhase.getChunksReset()
                .map(ChunkLocation::toCompactString)
                .collect(Collectors.joining(","));
        getBornToSurvive().getDatabase().execute(new SetChunksReset(setChunksResetStatement, chunksResetCompact), this);
    }
}
