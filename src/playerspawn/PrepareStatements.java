package it.menzani.bts.playerspawn;

import it.menzani.bts.datastore.wrapper.SQLDatabaseCallable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

class PrepareStatements implements SQLDatabaseCallable {
    @Override
    public PreparedStatement[] call(Object connection) throws SQLException {
        Connection sqlConnection = (Connection) connection;
        return new PreparedStatement[]{
                sqlConnection.prepareStatement("INSERT INTO PlayerSpawn VALUES (?, ?, ?, ?) ON CONFLICT DO NOTHING"),
                sqlConnection.prepareStatement("SELECT spawnX, spawnY, spawnZ FROM PlayerSpawn WHERE playerId = ?")
        };
    }

    @Override
    public String getErrorMessage() {
        return "Could not prepare database statements.";
    }
}
