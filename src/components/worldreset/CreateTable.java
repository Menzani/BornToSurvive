package it.menzani.bts.components.worldreset;

import it.menzani.bts.components.Component;
import it.menzani.bts.persistence.sql.wrapper.SQLDatabaseRunnable;

import java.sql.Connection;
import java.sql.SQLException;

class CreateTable implements SQLDatabaseRunnable {
    @Override
    public void run(Object connection, Component component) throws SQLException {
        Connection sqlConnection = (Connection) connection;
        sqlConnection.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS " + component.getName() +
                "(chunkX INT, chunkZ INT, PRIMARY KEY(chunkX, chunkZ))");
    }

    @Override
    public String getErrorMessage() {
        return "Could not create database table.";
    }
}
