package it.menzani.bts.components.playerspawn;

import it.menzani.bts.components.Component;
import it.menzani.bts.persistence.sql.wrapper.SQLDatabaseCallable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

class PrepareStatements implements SQLDatabaseCallable {
    @Override
    public PreparedStatement[] call(Object connection, Component component) throws SQLException {
        Connection sqlConnection = (Connection) connection;
        return new PreparedStatement[]{
                sqlConnection.prepareStatement("INSERT INTO " + component.getName() + " VALUES (?, ?, ?, ?) ON CONFLICT DO NOTHING"),
                sqlConnection.prepareStatement("SELECT spawnX, spawnY, spawnZ FROM " + component.getName() + " WHERE playerId = ?")
        };
    }

    @Override
    public String getErrorMessage() {
        return "Could not prepare database statements.";
    }
}
