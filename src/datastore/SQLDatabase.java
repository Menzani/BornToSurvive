package it.menzani.bts.datastore;

import java.sql.Connection;
import java.sql.SQLException;

public interface SQLDatabase extends Database {
    @Override
    Connection connect() throws ClassNotFoundException, SQLException;
}
