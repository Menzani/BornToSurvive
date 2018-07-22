package it.menzani.bts.components.assistant;

import it.menzani.bts.components.Component;
import it.menzani.bts.persistence.sql.wrapper.SQLDatabaseRunnable;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

class CreateTable implements SQLDatabaseRunnable {
    @Override
    public void run(Connection connection, Component component) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS " + component.getName() +
                    "(playerId UUID PRIMARY KEY, welcomeGuide BOOLEAN NOT NULL)");
        }
    }

    @Override
    public String getErrorMessage() {
        return "Could not create database table.";
    }
}
