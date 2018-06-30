package it.menzani.bts.components.worldreset;

import it.menzani.bts.components.Component;
import it.menzani.bts.persistence.sql.wrapper.SQLDatabaseRunnable;
import org.bukkit.World;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

class SetChunksReset implements SQLDatabaseRunnable {
    private final PreparedStatement preparedStatement;
    private final Map<World, String> chunksResetCompact;

    SetChunksReset(PreparedStatement preparedStatement, Map<World, String> chunksResetCompact) {
        this.preparedStatement = preparedStatement;
        this.chunksResetCompact = chunksResetCompact;
    }

    @Override
    public void run(Connection connection, Component component) throws SQLException {
        for (var entry : chunksResetCompact.entrySet()) {
            updateElement(entry.getKey(), entry.getValue());
        }
    }

    private void updateElement(World world, String compactElement) throws SQLException {
        preparedStatement.setObject(1, world.getUID());
        preparedStatement.setString(2, compactElement);
        preparedStatement.executeUpdate();
    }

    @Override
    public String getErrorMessage() {
        return "Could not store chunks reset." + System.lineSeparator() + "context: " + toString();
    }

    @Override
    public String toString() {
        return "SetChunksReset{" +
                "chunksResetCompact='" + chunksResetCompact + '\'' +
                '}';
    }
}
