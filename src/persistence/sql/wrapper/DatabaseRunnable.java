package it.menzani.bts.persistence.sql.wrapper;

import it.menzani.bts.components.Component;

public interface DatabaseRunnable extends DatabaseCallable<Object> {
    void run(Object connection, Component component) throws Exception;

    @Override
    default Object call(Object connection, Component component) throws Exception {
        run(connection, component);
        return null;
    }
}
