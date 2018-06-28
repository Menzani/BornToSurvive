package it.menzani.bts.components.playerspawn;

import it.menzani.bts.components.Component;
import it.menzani.bts.persistence.sql.wrapper.CheckedSQLDatabaseCallable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

class SetSpawn implements CheckedSQLDatabaseCallable {
    private final PreparedStatement preparedStatement;
    private final UUID playerId;
    private final Spawn spawn;

    SetSpawn(PreparedStatement preparedStatement, UUID playerId, Spawn spawn) {
        this.preparedStatement = preparedStatement;
        this.playerId = playerId;
        this.spawn = spawn;
    }

    @Override
    public Integer call(Connection connection, Component component) throws SQLException {
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
