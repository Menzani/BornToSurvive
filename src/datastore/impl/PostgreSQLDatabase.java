package it.menzani.bts.datastore.impl;

import it.menzani.bts.datastore.DatabaseCredentials;
import it.menzani.bts.datastore.SQLDatabase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PostgreSQLDatabase implements SQLDatabase {
    private final DatabaseCredentials credentials;

    public PostgreSQLDatabase(DatabaseCredentials credentials) {
        this.credentials = credentials;
    }

    @Override
    public Connection connect() throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");
        return DriverManager.getConnection("jdbc:postgresql://" +
                credentials.host + '/' + credentials.database, credentials.user, credentials.password);
    }
}
