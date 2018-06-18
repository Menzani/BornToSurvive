package it.menzani.bts.persistence.sql.wrapper;

public interface CheckedSQLDatabaseCallable extends SQLDatabaseCallable {
    String doPostCheck(Object result);
}
