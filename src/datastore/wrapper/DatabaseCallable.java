package it.menzani.bts.datastore.wrapper;

public interface DatabaseCallable {
    Object NULL = new Object();

    Object call(Object connection) throws Exception;

    String getErrorMessage();
}
