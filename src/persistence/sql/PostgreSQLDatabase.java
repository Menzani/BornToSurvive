package it.menzani.bts.persistence.sql;

import it.menzani.bts.persistence.DatabaseCredentials;

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
                credentials.getHost() + '/' + credentials.getDatabase(), credentials.getUser(), credentials.getPassword());
    }
}
