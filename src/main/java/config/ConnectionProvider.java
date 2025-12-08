package config;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Connection Provider Interface
 * Allows dependency injection for database connections
 * Enables testability by allowing mock/test implementations
 */
public interface ConnectionProvider {
    /**
     * Get a database connection
     * 
     * @return Connection object
     * @throws SQLException if connection fails
     */
    Connection getConnection() throws SQLException;
}

