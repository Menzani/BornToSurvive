package it.menzani.bts.datastore.wrapper;

public interface CheckedSQLDatabaseCallable extends SQLDatabaseCallable {
    String doPostCheck(Object result);
}
