package it.menzani.bts.persistence.sql.wrapper;

import it.menzani.bts.components.Component;
import it.menzani.bts.persistence.sql.SQLDatabase;
import it.menzani.logger.api.Logger;

import java.sql.Connection;
import java.sql.SQLException;

public class WrappedSQLDatabase implements AutoCloseable {
    private final Logger logger;
    private final Connection connection;

    public WrappedSQLDatabase(SQLDatabase database, Logger logger) {
        this.logger = logger;

        Connection connection = null;
        try {
            connection = database.connect();
        } catch (ClassNotFoundException e) {
            logger.fatal("Could not load JDBC driver.");
            e.printStackTrace();
        } catch (SQLException e) {
            logger.fatal("Could not establish database connection.");
            e.printStackTrace();
        } finally {
            this.connection = connection;
        }
    }

    public boolean isConnectionNotAvailable() {
        return connection == null;
    }

    public boolean execute(SQLDatabaseRunnable runnable, Component component) {
        Value<Object> result = submit(runnable, component);
        if (result == null) {
            return true;
        }
        assert result.isNull();
        return false;
    }

    public <T> Value<T> submit(SQLDatabaseCallable<T> callable, Component component) {
        if (isConnectionNotAvailable()) {
            throw new IllegalStateException("Database connection is not available. " +
                    "Always check with #isConnectionNotAvailable() before calling this method.");
        }
        T result;
        try {
            result = callable.call(connection, component);
        } catch (SQLException e) {
            String errorMessage = callable.getErrorMessage();
            if (errorMessage == null) throw new NullPointerException("callable#getErrorMessage() must not be null.");
            component.getLogger().fatal(errorMessage);
            e.printStackTrace();
            return null;
        }
        if (callable instanceof CheckedSQLDatabaseCallable<?>) {
            CheckedSQLDatabaseCallable<T> checkedCallable = (CheckedSQLDatabaseCallable<T>) callable;
            String errorMessage = checkedCallable.doPostCheck(result);
            if (errorMessage != null) {
                component.getLogger().fatal(errorMessage);
                return null;
            }
        }
        return new Value<>(result);
    }

    @Override
    public void close() {
        if (isConnectionNotAvailable()) return;
        try {
            connection.close();
        } catch (SQLException e) {
            logger.warn("Could not finalize database connection.");
            e.printStackTrace();
        }
    }
}
