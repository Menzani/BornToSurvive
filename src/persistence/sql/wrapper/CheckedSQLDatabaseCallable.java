package it.menzani.bts.persistence.sql.wrapper;

public interface CheckedSQLDatabaseCallable<T> extends SQLDatabaseCallable<T> {
    String doPostCheck(T result);
}
