package it.menzani.bts.components.worldreset;

import it.menzani.bts.components.Component;
import it.menzani.bts.persistence.sql.wrapper.SQLDatabaseRunnable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

class SetChunksReset implements SQLDatabaseRunnable {
    private final PreparedStatement preparedStatement;
    private final String chunksReset;

    SetChunksReset(PreparedStatement preparedStatement, String chunksReset) {
        this.preparedStatement = preparedStatement;
        this.chunksReset = chunksReset;
    }

    @Override
    public void run(Connection connection, Component component) throws SQLException {
        preparedStatement.setString(1, chunksReset);
        preparedStatement.executeUpdate();
    }

    @Override
    public String getErrorMessage() {
        return "Could not store chunks reset." + System.lineSeparator() + "context: " + toString();
    }

    @Override
    public String toString() {
        return "SetChunksReset{" +
                "chunksReset='" + chunksReset + '\'' +
                '}';
    }
}
