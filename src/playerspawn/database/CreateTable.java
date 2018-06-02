package it.menzani.bts.playerspawn.database;

import it.menzani.bts.datastore.wrapper.SQLDatabaseRunnable;

import java.sql.Connection;
import java.sql.SQLException;

public class CreateTable implements SQLDatabaseRunnable {
    @Override
    public void run(Object connection) throws SQLException {
        Connection sqlConnection = (Connection) connection;
        sqlConnection.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS PlayerSpawn" +
                "(playerId UUID PRIMARY KEY, spawnX INT NOT NULL, spawnY INT NOT NULL, spawnZ INT NOT NULL)");
    }

    @Override
    public String getErrorMessage() {
        return "Could not create database table.";
    }
}
