package it.menzani.bts.components.playername;

import it.menzani.bts.components.Component;
import it.menzani.bts.persistence.sql.wrapper.CheckedSQLDatabaseCallable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

class SetName implements CheckedSQLDatabaseCallable<Integer> {
    private final PreparedStatement preparedStatement;
    private final UUID playerId;
    private final String name;

    SetName(PreparedStatement preparedStatement, UUID playerId, String name) {
        this.preparedStatement = preparedStatement;
        this.playerId = playerId;
        this.name = name;
    }

    @Override
    public Integer call(Connection connection, Component component) throws SQLException {
        preparedStatement.setObject(1, playerId);
        preparedStatement.setString(2, name);
        return preparedStatement.executeUpdate();
    }

    @Override
    public String doPostCheck(Integer result) {
        if (result == 1) {
            return null;
        }
        return "Unexpected query update count." + System.lineSeparator() + "updateCount=" + result + ", context: " + toString();
    }

    @Override
    public String getErrorMessage() {
        return "Could not store player name." + System.lineSeparator() + "context: " + toString();
    }

    @Override
    public String toString() {
        return "SetName{" +
                "playerId=" + playerId +
                ", name='" + name + '\'' +
                '}';
    }
}
