package it.menzani.bts.components.assistant;

import it.menzani.bts.components.Component;
import it.menzani.bts.persistence.sql.wrapper.CheckedSQLDatabaseCallable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

class SetWelcomeGuide implements CheckedSQLDatabaseCallable<Integer> {
    private final PreparedStatement preparedStatement;
    private final UUID playerId;

    SetWelcomeGuide(PreparedStatement preparedStatement, UUID playerId) {
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
        if (result == 1) {
            return null;
        }
        return "Unexpected query update count." + System.lineSeparator() + "updateCount=" + result + ", context: " + toString();
    }

    @Override
    public String getErrorMessage() {
        return "Could not store welcome guide completion status." + System.lineSeparator() + "context: " + toString();
    }

    @Override
    public String toString() {
        return "SetWelcomeGuide{" +
                "playerId=" + playerId +
                '}';
    }
}
