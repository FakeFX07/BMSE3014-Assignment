package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Database Connection Utility
 * Implements Singleton pattern for database connection management
 * Also implements ConnectionProvider for dependency injection
 * Follows Single Responsibility Principle - only handles database connections
 */
public class DatabaseConnection implements ConnectionProvider {
    
    private static final String DB_URL = "jdbc:mysql://localhost:3306/BMSE3014";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root";
    
    private static DatabaseConnection instance;
    private Connection connection;
    private String url;
    private String user;
    private String password;
    
    /**
     * Private constructor to prevent instantiation
     * Implements Singleton pattern
     */
    private DatabaseConnection() {
        this(DB_URL, DB_USER, DB_PASSWORD);
    }
    
    /**
     * Constructor with connection parameters
     * Allows dependency injection for testing
     * 
     * @param url Database URL
     * @param user Database user
     * @param password Database password
     */
    public DatabaseConnection(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
        initializeConnection();
    }
    
    /**
     * Initialize database connection
     */
    private void initializeConnection() {
        try {
            if (url.contains("h2")) {
                Class.forName("org.h2.Driver");
            } else {
                Class.forName("com.mysql.cj.jdbc.Driver");
            }
            this.connection = DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
        }
    }
    
    /**
     * Get singleton instance of DatabaseConnection
     * Thread-safe singleton implementation
     * 
     * @return DatabaseConnection instance
     */
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }
    
    /**
     * Create a new instance (for testing)
     * 
     * @param url Database URL
     * @param user Database user
     * @param password Database password
     * @return New DatabaseConnection instance
     */
    public static DatabaseConnection createInstance(String url, String user, String password) {
        return new DatabaseConnection(url, user, password);
    }
    
    /**
     * Get database connection
     * 
     * @return Connection object
     * @throws SQLException if connection fails
     */
    @Override
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                if (url != null && url.contains("h2")) {
                    Class.forName("org.h2.Driver");
                } else {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                }
                connection = DriverManager.getConnection(url != null ? url : DB_URL, 
                                                        user != null ? user : DB_USER, 
                                                        password != null ? password : DB_PASSWORD);
            } catch (ClassNotFoundException e) {
                throw new SQLException("JDBC Driver not found", e);
            }
        }
        return connection;
    }
    
    /**
     * Close database connection
     * 
     * @throws SQLException if closing fails
     */
    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
