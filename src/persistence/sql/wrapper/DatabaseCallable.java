package it.menzani.bts.persistence.sql.wrapper;

import it.menzani.bts.components.Component;

public interface DatabaseCallable<R> {
    R call(Object connection, Component component) throws Exception;

    String getErrorMessage();
}
