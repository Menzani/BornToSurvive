package it.menzani.bts.components.worldreset;

import it.menzani.bts.components.Component;
import it.menzani.bts.persistence.sql.wrapper.SQLDatabaseRunnable;

import java.sql.Connection;
import java.sql.SQLException;

class PurgeTables implements SQLDatabaseRunnable {
    @Override
    public void run(Connection connection, Component component) throws SQLException {
        connection.createStatement().executeUpdate("DELETE FROM " + component.getName() + "_mark");
        connection.createStatement().executeUpdate("DELETE FROM " + component.getName() + "_reset");
    }

    @Override
    public String getErrorMessage() {
        return "Could not purge database tables.";
    }
}
