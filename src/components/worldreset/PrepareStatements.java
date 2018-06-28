package it.menzani.bts.components.worldreset;

import it.menzani.bts.components.Component;
import it.menzani.bts.persistence.sql.wrapper.SQLDatabaseCallable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

class PrepareStatements implements SQLDatabaseCallable {
    @Override
    public PreparedStatement[] call(Connection connection, Component component) throws SQLException {
        return new PreparedStatement[]{
                connection.prepareStatement("INSERT INTO " + component.getName() + " VALUES (?, ?) ON CONFLICT DO NOTHING"),
                connection.prepareStatement("DELETE FROM " + component.getName() + " WHERE chunkX = ? AND chunkZ = ?")
        };
    }

    @Override
    public String getErrorMessage() {
        return "Could not prepare database statements.";
    }
}
