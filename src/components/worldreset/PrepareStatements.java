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
                connection.prepareStatement("INSERT INTO " + component.getName() +
                        "_mark VALUES (?, ?, ?) ON CONFLICT DO NOTHING"),
                connection.prepareStatement("DELETE FROM " + component.getName() +
                        "_mark WHERE chunkWorld = ? AND chunkX = ? AND chunkZ = ?"),
                connection.prepareStatement("SELECT chunkX, chunkZ FROM " + component.getName() +
                        "_mark WHERE chunkWorld = ?"),
                connection.prepareStatement("INSERT INTO " + component.getName() +
                        "_reset VALUES (?, ?) ON CONFLICT (world) DO UPDATE SET chunksResetCompact = EXCLUDED.chunksResetCompact"),
                connection.prepareStatement("SELECT chunksResetCompact FROM " + component.getName() +
                        "_reset WHERE world = ?")
        };
    }

    @Override
    public String getErrorMessage() {
        return "Could not prepare database statements.";
    }
}
