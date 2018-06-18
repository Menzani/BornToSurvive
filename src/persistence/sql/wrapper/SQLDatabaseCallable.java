package it.menzani.bts.persistence.sql.wrapper;

import it.menzani.bts.components.Component;

import java.sql.SQLException;

public interface SQLDatabaseCallable extends DatabaseCallable {
    @Override
    Object call(Object connection, Component component) throws SQLException;
}
