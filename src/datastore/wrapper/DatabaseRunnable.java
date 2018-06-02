package it.menzani.bts.datastore.wrapper;

public interface DatabaseRunnable extends DatabaseCallable {
    void run(Object connection) throws Exception;

    @Override
    default Object call(Object connection) throws Exception {
        run(connection);
        return null;
    }
}
