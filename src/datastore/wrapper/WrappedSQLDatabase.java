package it.menzani.bts.datastore.wrapper;

import it.menzani.bts.Component;
import it.menzani.bts.datastore.SQLDatabase;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

public class WrappedSQLDatabase implements Closeable {
    private final Logger logger;
    private final Connection connection;

    public WrappedSQLDatabase(SQLDatabase database, Logger logger) {
        this.logger = logger;

        Connection connection = null;
        try {
            connection = database.connect();
        } catch (ClassNotFoundException e) {
            logger.severe("Could not load JDBC driver.");
            e.printStackTrace();
        } catch (SQLException e) {
            logger.severe("Could not establish database connection.");
            e.printStackTrace();
        } finally {
            this.connection = connection;
        }
    }

    public boolean isConnectionNotAvailable() {
        return connection == null;
    }

    public void execute(SQLDatabaseRunnable runnable, Component component) {
        Object result = submit(runnable, component);
        assert result == null || result == DatabaseCallable.NULL;
    }

    public Object submit(SQLDatabaseCallable callable, Component component) {
        if (isConnectionNotAvailable()) {
            throw new IllegalStateException("Database connection is not available. " +
                    "Always check with #isConnectionNotAvailable() before calling this method.");
        }
        Object result;
        try {
            result = callable.call(connection);
        } catch (SQLException e) {
            String errorMessage = callable.getErrorMessage();
            if (errorMessage == null) throw new NullPointerException("callable#getErrorMessage() must not be null.");
            logger.severe(component.getTag() + errorMessage);
            e.printStackTrace();
            return null;
        }
        if (callable instanceof CheckedSQLDatabaseCallable) {
            CheckedSQLDatabaseCallable checkedCallable = (CheckedSQLDatabaseCallable) callable;
            String warningMessage = checkedCallable.doPostCheck(result);
            if (warningMessage != null) {
                logger.warning(component.getTag() + warningMessage);
                return null;
            }
        }
        if (result == null) {
            result = DatabaseCallable.NULL;
        }
        return result;
    }

    @Override
    public void close() {
        if (isConnectionNotAvailable()) return;
        try {
            connection.close();
        } catch (SQLException e) {
            logger.warning("Could not finalize database connection.");
            e.printStackTrace();
        }
    }
}
