package it.menzani.bts.playerspawn.database;

import it.menzani.bts.datastore.wrapper.CheckedSQLDatabaseCallable;
import it.menzani.bts.playerspawn.Spawn;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class SetSpawn implements CheckedSQLDatabaseCallable {
    private final PreparedStatement preparedStatement;
    private final UUID playerId;
    private final Spawn spawn;

    public SetSpawn(PreparedStatement preparedStatement, UUID playerId, Spawn spawn) {
        this.preparedStatement = preparedStatement;
        this.playerId = playerId;
        this.spawn = spawn;
    }

    @Override
    public Integer call(Object connection) throws SQLException {
        preparedStatement.setObject(1, playerId);
        preparedStatement.setInt(2, spawn.x);
        preparedStatement.setInt(3, spawn.y);
        preparedStatement.setInt(4, spawn.z);
        return preparedStatement.executeUpdate();
    }

    @Override
    public String doPostCheck(Object result) {
        if (result.equals(0) || result.equals(1)) {
            return null;
        }
        return "Unexpected query update count." + System.lineSeparator() + "updateCount=" + result + ", context: " + toString();
    }

    @Override
    public String getErrorMessage() {
        return "Could not store player spawn." + System.lineSeparator() + "context: " + toString();
    }

    @Override
    public String toString() {
        return "SetSpawn{" +
                "playerId=" + playerId +
                ", spawn=" + spawn +
                '}';
    }
}
