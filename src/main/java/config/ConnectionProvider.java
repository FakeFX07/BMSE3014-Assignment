package config;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionProvider {
    //Get a database connection
    Connection getConnection() throws SQLException;
}

