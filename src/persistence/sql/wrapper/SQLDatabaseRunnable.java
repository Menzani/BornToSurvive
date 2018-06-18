package it.menzani.bts.persistence.sql.wrapper;

import it.menzani.bts.components.Component;

import java.sql.SQLException;

public interface SQLDatabaseRunnable extends DatabaseRunnable, SQLDatabaseCallable {
    @Override
    void run(Object connection, Component component) throws SQLException;

    @Override
    default Object call(Object connection, Component component) throws SQLException {
        run(connection, component);
        return null;
    }
}
