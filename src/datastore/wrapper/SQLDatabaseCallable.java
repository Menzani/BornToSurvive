package it.menzani.bts.datastore.wrapper;

import it.menzani.bts.Component;

import java.sql.SQLException;

public interface SQLDatabaseCallable extends DatabaseCallable {
    @Override
    Object call(Object connection, Component component) throws SQLException;
}
