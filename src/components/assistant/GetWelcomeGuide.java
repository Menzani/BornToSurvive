package it.menzani.bts.components.assistant;

import it.menzani.bts.components.Component;
import it.menzani.bts.persistence.sql.wrapper.SQLDatabaseCallable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

class GetWelcomeGuide implements SQLDatabaseCallable<Boolean> {
    private final PreparedStatement preparedStatement;
    private final UUID playerId;

    GetWelcomeGuide(PreparedStatement preparedStatement, UUID playerId) {
        this.preparedStatement = preparedStatement;
        this.playerId = playerId;
    }

    @Override
    public Boolean call(Connection connection, Component component) throws SQLException {
        preparedStatement.setObject(1, playerId);
        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            boolean validRow = resultSet.next();
            if (validRow) {
                return resultSet.getBoolean(1);
            }
        }
        return null;
    }

    @Override
    public String getErrorMessage() {
        return "Could not retrieve welcome guide completion status." + System.lineSeparator() + "context: " + toString();
    }

    @Override
    public String toString() {
        return "GetWelcomeGuide{" +
                "playerId=" + playerId +
                '}';
    }
}
