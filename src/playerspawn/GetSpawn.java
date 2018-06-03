package it.menzani.bts.playerspawn;

import it.menzani.bts.Component;
import it.menzani.bts.datastore.wrapper.SQLDatabaseCallable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

class GetSpawn implements SQLDatabaseCallable {
    private final PreparedStatement preparedStatement;
    private final UUID playerId;

    GetSpawn(PreparedStatement preparedStatement, UUID playerId) {
        this.preparedStatement = preparedStatement;
        this.playerId = playerId;
    }

    @Override
    public Spawn call(Object connection, Component component) throws SQLException {
        preparedStatement.setObject(1, playerId);
        ResultSet resultSet = preparedStatement.executeQuery();
        boolean validRow = resultSet.next();
        if (validRow) {
            return new Spawn(resultSet.getInt(1), resultSet.getInt(2), resultSet.getInt(3));
        }
        return null;
    }

    @Override
    public String getErrorMessage() {
        return "Could not retrieve player spawn." + System.lineSeparator() + "context: " + toString();
    }

    @Override
    public String toString() {
        return "GetSpawn{" +
                "playerId=" + playerId +
                '}';
    }
}
