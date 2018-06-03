package it.menzani.bts.datastore;

public class DatabaseCredentials {
    public final String host, database, user, password;

    public DatabaseCredentials(String host, String database, String user, String password) {
        this.host = host;
        this.database = database;
        this.user = user;
        this.password = password;
    }

    @Override
    public String toString() {
        return "DatabaseCredentials{" +
                "host='" + host + '\'' +
                ", database='" + database + '\'' +
                ", user='" + user + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
