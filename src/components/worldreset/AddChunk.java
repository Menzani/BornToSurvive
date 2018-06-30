package it.menzani.bts.components.worldreset;

import it.menzani.bts.components.Component;
import it.menzani.bts.persistence.sql.wrapper.CheckedSQLDatabaseCallable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

class AddChunk implements CheckedSQLDatabaseCallable<Integer> {
    private final PreparedStatement preparedStatement;
    private final ChunkLocation chunkLocation;

    AddChunk(PreparedStatement preparedStatement, ChunkLocation chunkLocation) {
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
    public String doPostCheck(Integer result) {
        if (result == 0 || result == 1) {
            return null;
        }
        return "Unexpected query update count." + System.lineSeparator() + "updateCount=" + result + ", context: " + toString();
    }

    @Override
    public String getErrorMessage() {
        return "Could not store chunk." + System.lineSeparator() + "context: " + toString();
    }

    @Override
    public String toString() {
        return "AddChunk{" +
                "chunkLocation=" + chunkLocation +
                '}';
    }
}
