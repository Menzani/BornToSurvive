package it.menzani.bts.components.worldreset;

import it.menzani.bts.components.Component;
import it.menzani.bts.persistence.sql.wrapper.SQLDatabaseRunnable;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

class CreateTables implements SQLDatabaseRunnable {
    @Override
    public void run(Connection connection, Component component) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS " + component.getName() + "_mark" +
                    "(chunkWorld UUID, chunkX INT, chunkZ INT, PRIMARY KEY(chunkWorld, chunkX, chunkZ))");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS " + component.getName() + "_reset" +
                    "(world UUID PRIMARY KEY, chunksResetCompact TEXT NOT NULL)");
        }
    }

    @Override
    public String getErrorMessage() {
        return "Could not create database tables.";
    }
}
