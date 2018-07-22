package it.menzani.bts.components.playername;

import it.menzani.bts.components.Component;
import it.menzani.bts.persistence.sql.wrapper.SQLDatabaseCallable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

class GetName implements SQLDatabaseCallable<String> {
    private final PreparedStatement preparedStatement;
    private final UUID playerId;

    GetName(PreparedStatement preparedStatement, UUID playerId) {
        this.preparedStatement = preparedStatement;
        this.playerId = playerId;
    }

    @Override
    public String call(Connection connection, Component component) throws SQLException {
        preparedStatement.setObject(1, playerId);
        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            boolean validRow = resultSet.next();
            if (validRow) {
                return resultSet.getString(1);
            }
        }
        return null;
    }

    @Override
    public String getErrorMessage() {
        return "Could not retrieve player name." + System.lineSeparator() + "context: " + toString();
    }

    @Override
    public String toString() {
        return "GetName{" +
                "playerId=" + playerId +
                '}';
    }
}
