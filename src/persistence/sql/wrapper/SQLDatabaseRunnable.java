package it.menzani.bts.persistence.sql.wrapper;

import it.menzani.bts.components.Component;

import java.sql.Connection;
import java.sql.SQLException;

public interface SQLDatabaseRunnable extends DatabaseRunnable, SQLDatabaseCallable<Object> {
    @Override
    default void run(Object connection, Component component) throws SQLException {
        run((Connection) connection, component);
    }

    void run(Connection connection, Component component) throws SQLException;

    @Override
    default Object call(Object connection, Component component) throws SQLException {
        return call((Connection) connection, component);
    }

    @Override
    default Object call(Connection connection, Component component) throws SQLException {
        run(connection, component);
        return null;
    }
}
