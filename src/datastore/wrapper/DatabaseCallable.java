package it.menzani.bts.datastore.wrapper;

import it.menzani.bts.Component;

public interface DatabaseCallable {
    Object NULL = new Object();

    Object call(Object connection, Component component) throws Exception;

    String getErrorMessage();
}
