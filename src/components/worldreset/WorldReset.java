package it.menzani.bts.components.worldreset;

import it.menzani.bts.BornToSurvive;
import it.menzani.bts.components.ComponentListener;
import it.menzani.bts.components.SimpleComponent;
import it.menzani.bts.persistence.sql.wrapper.WrappedSQLDatabase;

import java.sql.PreparedStatement;

public class WorldReset extends SimpleComponent {
    private final Phase phase;

    public WorldReset(BornToSurvive bornToSurvive, Phase phase) {
        super(bornToSurvive);
        this.phase = phase;
    }

    @Override
    public void load() {
        WrappedSQLDatabase database = getBornToSurvive().getDatabase();

        PreparedStatement[] preparedStatements =
                (PreparedStatement[]) database.submit(new PrepareStatements(), this);
        if (preparedStatements == null) return;
        ComponentListener phaseListener = null;
        switch (phase) {
            case MARK:
                phaseListener = new MarkPhase(this, preparedStatements[0], preparedStatements[1]);
                break;
            case RESET:
                phaseListener = new ResetPhase(this);
                break;
        }

        boolean error = database.execute(new CreateTable(), this);
        if (error) return;

        if (phaseListener != null) {
            phaseListener.register();
        }
    }
}
