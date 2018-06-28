package it.menzani.bts.persistence.sql.wrapper;

import it.menzani.bts.components.Component;

import java.sql.Connection;
import java.sql.SQLException;

public interface SQLDatabaseCallable extends DatabaseCallable {
    @Override
    default Object call(Object connection, Component component) throws SQLException {
        return call((Connection) connection, component);
    }

    Object call(Connection connection, Component component) throws SQLException;
}
