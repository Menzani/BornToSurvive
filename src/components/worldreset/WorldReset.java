package it.menzani.bts.components.worldreset;

import it.menzani.bts.BornToSurvive;
import it.menzani.bts.components.ComponentListener;
import it.menzani.bts.components.SimpleComponent;
import it.menzani.bts.components.SimpleComponentTask;
import it.menzani.bts.persistence.sql.wrapper.WrappedSQLDatabase;

import java.sql.PreparedStatement;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WorldReset extends SimpleComponent {
    static final String signText = "Do not reset me!";
    static final int markRange = 10;
    private static final char chunksResetCompactSeparatorChar = ';';
    static final String chunksResetCompactSeparator = Character.toString(chunksResetCompactSeparatorChar);

    private final Phase phase;
    private SimpleComponentTask chunksResetAutosave;

    public WorldReset(BornToSurvive bornToSurvive, Phase phase) {
        super(bornToSurvive);
        this.phase = phase;
    }

    @Override
    public void load() {
        if (phase == Phase.NONE) {
            ComponentListener phaseListener = new NonePhase(this);
            phaseListener.register();
            return;
        }
        WrappedSQLDatabase database = getBornToSurvive().getDatabase();

        boolean error = database.execute(new CreateTables(), this);
        if (error) return;

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

                Set<ChunkLocation> chunksReset;
                try {
                    chunksReset = split(chunksResetCompact)
                            .map(ChunkLocation::fromCompactString)
                            .collect(Collectors.toSet());
                } catch (ParseException e) {
                    getLogger().throwable(e, "Could not parse chunk location.");
                    return;
                }
                phaseListener = new ResetPhase(this, markedArea, chunksReset);
                chunksResetAutosave = new ChunksResetAutosave(this, preparedStatements[2], chunksReset);
                chunksResetAutosave.runTaskTimerAsynchronously(Duration.ofMinutes(5));
                break;
            default:
                throw new AssertionError();
        }
        phaseListener.register();
    }

    private static Stream<String> split(String chunksResetCompact) {
        List<String> result = new ArrayList<>();
        char[] characters = chunksResetCompact.toCharArray();
        int lastPosition = 0;
        for (int i = 0; i < characters.length; i++) {
            char character = characters[i];
            if (character == chunksResetCompactSeparatorChar) {
                result.add(chunksResetCompact.substring(lastPosition, i));
                lastPosition = i + 1;
            }
        }
        if (lastPosition != characters.length) {
            result.add(chunksResetCompact.substring(lastPosition));
        }
        return result.parallelStream();
    }

    @Override
    public void unload() {
        if (chunksResetAutosave == null) return;
        chunksResetAutosave.run();
    }
}
