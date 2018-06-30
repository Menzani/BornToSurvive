package it.menzani.bts.components.worldreset;

import it.menzani.bts.components.Component;
import it.menzani.bts.persistence.sql.wrapper.CheckedSQLDatabaseCallable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

class RemoveChunk implements CheckedSQLDatabaseCallable {
    private final PreparedStatement preparedStatement;
    private final ChunkLocation chunkLocation;

    RemoveChunk(PreparedStatement preparedStatement, ChunkLocation chunkLocation) {
        this.preparedStatement = preparedStatement;
        this.chunkLocation = chunkLocation;
    }

    @Override
    public Integer call(Connection connection, Component component) throws SQLException {
        preparedStatement.setObject(1, chunkLocation.world.getUID());
        preparedStatement.setInt(2, chunkLocation.x);
        preparedStatement.setInt(3, chunkLocation.z);
        return preparedStatement.executeUpdate();
    }

    @Override
    public String doPostCheck(Object result) {
        if (result.equals(1)) {
            return null;
        }
        return "Unexpected query update count." + System.lineSeparator() + "updateCount=" + result + ", context: " + toString();
    }

    @Override
    public String getErrorMessage() {
        return "Could not delete chunk." + System.lineSeparator() + "context: " + toString();
    }

    @Override
    public String toString() {
        return "RemoveChunk{" +
                "chunkLocation=" + chunkLocation +
                '}';
    }
}
