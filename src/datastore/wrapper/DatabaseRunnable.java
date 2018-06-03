package it.menzani.bts.datastore.wrapper;

import it.menzani.bts.Component;

public interface DatabaseRunnable extends DatabaseCallable {
    void run(Object connection, Component component) throws Exception;

    @Override
    default Object call(Object connection, Component component) throws Exception {
        run(connection, component);
        return null;
    }
}
