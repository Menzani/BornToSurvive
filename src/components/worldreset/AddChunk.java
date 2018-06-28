package it.menzani.bts.components.worldreset;

import it.menzani.bts.components.Component;
import it.menzani.bts.persistence.sql.wrapper.CheckedSQLDatabaseCallable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

class AddChunk implements CheckedSQLDatabaseCallable {
    private final PreparedStatement preparedStatement;
    private final ChunkLocation chunkLocation;

    AddChunk(PreparedStatement preparedStatement, ChunkLocation chunkLocation) {
        this.preparedStatement = preparedStatement;
        this.chunkLocation = chunkLocation;
    }

    @Override
    public Integer call(Connection connection, Component component) throws SQLException {
        preparedStatement.setInt(1, chunkLocation.x);
        preparedStatement.setInt(2, chunkLocation.z);
        return preparedStatement.executeUpdate();
    }

    @Override
    public String doPostCheck(Object result) {
        if (result.equals(0) || result.equals(1)) {
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
