package it.menzani.bts.persistence.sql;

import it.menzani.bts.persistence.Database;

import java.sql.Connection;
import java.sql.SQLException;

public interface SQLDatabase extends Database {
    @Override
    Connection connect() throws ClassNotFoundException, SQLException;
}
