package it.menzani.bts.datastore.wrapper;

import java.sql.SQLException;

public interface SQLDatabaseCallable extends DatabaseCallable {
    @Override
    Object call(Object connection) throws SQLException;
}
