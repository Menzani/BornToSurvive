package it.menzani.bts.components.worldreset;

import it.menzani.bts.BornToSurvive;
import it.menzani.bts.components.ComponentListener;
import it.menzani.bts.components.SimpleComponent;
import it.menzani.bts.persistence.sql.wrapper.WrappedSQLDatabase;

import java.sql.PreparedStatement;

public class WorldReset extends SimpleComponent {
    private final ComponentListener phase;

    public WorldReset(BornToSurvive bornToSurvive, Phase phase) {
        super(bornToSurvive);
        WrappedSQLDatabase database = bornToSurvive.getDatabase();

        PreparedStatement[] preparedStatements =
                (PreparedStatement[]) database.submit(new PrepareStatements(), this);
        if (preparedStatements == null) {
            this.phase = null;
            return;
        }

        switch (phase) {
            case MARK:
                this.phase = new MarkPhase(this, preparedStatements[0], preparedStatements[1]);
                break;
            case RESET:
                this.phase = new ResetPhase(this);
                break;
            default:
                this.phase = null;
                break;
        }

        database.execute(new CreateTable(), this);
    }

    @Override
    public void load() {
        if (phase != null) {
            phase.register();
        }
    }
}
