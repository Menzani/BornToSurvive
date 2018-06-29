package it.menzani.bts.components.worldreset;

import it.menzani.bts.components.Component;
import it.menzani.bts.persistence.sql.wrapper.SQLDatabaseCallable;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

class GetChunksReset implements SQLDatabaseCallable {
    @Override
    public String call(Connection connection, Component component) throws SQLException {
        ResultSet resultSet = connection.createStatement().executeQuery("SELECT value FROM " +
                component.getName() + "_reset WHERE id = false");
        boolean validRow = resultSet.next();
        if (validRow) {
            return resultSet.getString(1);
        }
        return "";
    }

    @Override
    public String getErrorMessage() {
        return "Could not retrieve chunks reset.";
    }
}
