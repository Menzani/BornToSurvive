package it.menzani.bts.persistence.sql.wrapper;

import it.menzani.bts.components.Component;

import java.sql.Connection;
import java.sql.SQLException;

public interface SQLDatabaseCallable<R> extends DatabaseCallable<R> {
    @Override
    default R call(Object connection, Component component) throws SQLException {
        return call((Connection) connection, component);
    }

    R call(Connection connection, Component component) throws SQLException;
}
