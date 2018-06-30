package it.menzani.bts.components.worldreset;

import it.menzani.bts.BornToSurvive;
import it.menzani.bts.components.ComponentListener;
import it.menzani.bts.components.SimpleComponent;
import it.menzani.bts.components.SimpleComponentTask;
import it.menzani.bts.persistence.sql.wrapper.Value;
import it.menzani.bts.persistence.sql.wrapper.WrappedSQLDatabase;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;

import java.sql.PreparedStatement;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

public class WorldReset extends SimpleComponent {
    private static final char chunksResetCompactSeparatorChar = ';';
    static final String chunksResetCompactSeparator = Character.toString(chunksResetCompactSeparatorChar);
    static final String signText = "Do not reset me!";
    static final int markRange = 10;

    private final Phase phase;
    private SimpleComponentTask chunksResetAutosave;

    public WorldReset(BornToSurvive bornToSurvive, Phase phase) {
        super(bornToSurvive);
        this.phase = phase;
    }

    @Override
    public void loadPreWorld() {
    }

    @Override
    public void load() {
        WrappedSQLDatabase database = getBornToSurvive().getDatabase();

        boolean error = database.execute(new CreateTables(), this);
        if (error) return;

        Value<PreparedStatement[]> preparedStatements = database.submit(new PrepareStatements(), this);
        if (preparedStatements == null) return;
        ComponentListener phaseListener;
        switch (phase) {
            case MARK:
                phaseListener = new MarkPhase(this, preparedStatements.get()[0], preparedStatements.get()[1]);
                break;
            case RESET:
                Value<MarkedArea> markedArea = submitGetMarkedArea(preparedStatements.get());
                if (markedArea == null) return;
                Value<Map<World, String>> chunksResetCompact = database.submit(
                        new GetChunksReset(preparedStatements.get()[4], getBornToSurvive()), this);
                if (chunksResetCompact == null) return;

                Map<World, Set<ChunkLocation>> chunksReset = new HashMap<>();
                for (var entry : chunksResetCompact.get().entrySet()) {
                    var element = computeChunksResetElement(entry.getValue());
                    if (element == null) return;
                    chunksReset.put(entry.getKey(), element);
                }

                phaseListener = new ResetPhase(this, markedArea.get(), chunksReset);
                chunksResetAutosave = new ChunksResetAutosave(this, preparedStatements.get()[3], chunksReset);
                chunksResetAutosave.runTaskTimerAsynchronously(Duration.ofMinutes(5));
                break;
            case NONE:
                markedArea = submitGetMarkedArea(preparedStatements.get());
                if (markedArea == null) return;

                phaseListener = new NonePhase(this, markedArea.get());
                break;
            default:
                throw new AssertionError();
        }
        phaseListener.register();
        getBornToSurvive().getPropertyStore().getWorldReset().setLastPhase(phase);
    }

    private Value<MarkedArea> submitGetMarkedArea(PreparedStatement[] preparedStatements) {
        return getBornToSurvive().getDatabase().submit(new GetMarkedArea(preparedStatements[2], getBornToSurvive()), this);
    }

    private Set<ChunkLocation> computeChunksResetElement(String compactElement) {
        try {
            return split(compactElement).parallelStream()
                    .map(ChunkLocation::fromCompactString)
                    .collect(Collectors.toSet());
        } catch (ParseException e) {
            getLogger().throwable(e, "Could not parse chunk location.");
            return null;
        }
    }

    private static List<String> split(String chunksResetCompact) {
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
        return result;
    }

    @Override
    public void unload() {
        if (chunksResetAutosave == null) return;
        chunksResetAutosave.run();
    }

    static boolean isMark(BlockState state) {
        Sign sign = (Sign) state;
        return sign.getLine(0).equals(signText);
    }
}
