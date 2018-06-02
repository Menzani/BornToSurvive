package it.menzani.bts.datastore.wrapper;

import java.sql.SQLException;

public interface SQLDatabaseRunnable extends DatabaseRunnable, SQLDatabaseCallable {
    @Override
    void run(Object connection) throws SQLException;

    @Override
    default Object call(Object connection) throws SQLException {
        run(connection);
        return null;
    }
}
