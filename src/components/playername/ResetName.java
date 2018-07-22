package it.menzani.bts.components.playername;

import it.menzani.bts.components.Component;
import it.menzani.bts.persistence.sql.wrapper.CheckedSQLDatabaseCallable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

class ResetName implements CheckedSQLDatabaseCallable<Integer> {
    private final PreparedStatement preparedStatement;
    private final UUID playerId;

    ResetName(PreparedStatement preparedStatement, UUID playerId) {
        this.preparedStatement = preparedStatement;
        this.playerId = playerId;
    }

    @Override
    public Integer call(Connection connection, Component component) throws SQLException {
        preparedStatement.setObject(1, playerId);
        return preparedStatement.executeUpdate();
    }

    @Override
    public String doPostCheck(Integer result) {
        if (result == 0 || result == 1) {
            return null;
        }
        return "Unexpected query update count." + System.lineSeparator() + "updateCount=" + result + ", context: " + toString();
    }

    @Override
    public String getErrorMessage() {
        return "Could not reset player name." + System.lineSeparator() + "context: " + toString();
    }

    @Override
    public String toString() {
        return "ResetName{" +
                "playerId=" + playerId +
                '}';
    }
}
