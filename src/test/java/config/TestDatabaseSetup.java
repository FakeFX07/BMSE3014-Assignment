package config;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import util.PasswordUtil;

/**
 * Test Database Setup Utility
 * Creates test database schema for H2 database
 */
public class TestDatabaseSetup {
    
    /**
     * Initialize test database schema
     * 
     * @param connectionProvider Connection provider
     * @throws SQLException if setup fails
     */
    public static void initializeSchema(ConnectionProvider connectionProvider) throws SQLException {
        try (Connection conn = connectionProvider.getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Create customers table
            stmt.execute("CREATE TABLE IF NOT EXISTS customers (" +
                    "customer_id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "name VARCHAR(100) NOT NULL, " +
                    "age INT NOT NULL, " +
                    "phone_number VARCHAR(11) NOT NULL, " +
                    "gender VARCHAR(10) NOT NULL, " +
                    "password VARCHAR(100) NOT NULL" +
                    ")");
            
            // Create foods table with quantity field
            stmt.execute("CREATE TABLE IF NOT EXISTS foods (" +
                    "food_id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "food_name VARCHAR(100) NOT NULL, " +
                    "food_price DECIMAL(10,2) NOT NULL, " +
                    "food_type VARCHAR(20) NOT NULL, " +
                    "quantity INT NOT NULL DEFAULT 0" +
                    ")");
            
            // Create payment_methods table (new schema without customer_id)
            stmt.execute("CREATE TABLE IF NOT EXISTS payment_methods (" +
                    "payment_method_id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "password VARCHAR(255) NOT NULL, " +
                    "payment_type VARCHAR(20) NOT NULL, " +
                    "wallet_id VARCHAR(50), " +
                    "balance DECIMAL(10,2) NOT NULL DEFAULT 0.00, " +
                    "card_number VARCHAR(16), " +
                    "expiry_date VARCHAR(4)" +
                    ")");
            
            // Create orders table
            stmt.execute("CREATE TABLE IF NOT EXISTS orders (" +
                    "order_id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "customer_id INT NOT NULL, " +
                    "order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "total_price DECIMAL(10,2) NOT NULL, " +
                    "payment_method_id INT NOT NULL, " +
                    "payment_type VARCHAR(20) NOT NULL, " +
                    "status VARCHAR(20) NOT NULL" +
                    ")");
            
            // Create order_details table
            stmt.execute("CREATE TABLE IF NOT EXISTS order_details (" +
                    "order_detail_id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "order_id INT NOT NULL, " +
                    "food_id INT NOT NULL, " +
                    "quantity INT NOT NULL, " +
                    "unit_price DECIMAL(10,2) NOT NULL, " +
                    "subtotal DECIMAL(10,2) NOT NULL" +
                    ")");
            
            // Clear existing test data
            stmt.execute("DELETE FROM order_details");
            stmt.execute("DELETE FROM orders");
            stmt.execute("DELETE FROM payment_methods");
            stmt.execute("DELETE FROM foods");
            stmt.execute("DELETE FROM customers");
            
            // Insert test data
            insertTestData(stmt);
        }
    }
    
    /**
     * Insert test data
     */
    private static void insertTestData(Statement stmt) throws SQLException {
        // Hash passwords before inserting
        String password1Hash = PasswordUtil.hashPassword("password123");
        String password2Hash = PasswordUtil.hashPassword("pass456");
        
        // Insert test customers with hashed passwords
        stmt.execute("INSERT INTO customers (customer_id, name, age, phone_number, gender, password) VALUES " +
                "(1000, 'John Doe', 25, '0123456789', 'Male', '" + password1Hash + "'), " +
                "(1001, 'Jane Smith', 30, '0111111111', 'Female', '" + password2Hash + "')");
        
        // Insert test foods with quantities
        stmt.execute("INSERT INTO foods (food_id, food_name, food_price, food_type, quantity) VALUES " +
                "(2000, 'Chicken Rice', 10.50, 'Set', 50), " +
                "(2001, 'Nasi Lemak', 8.00, 'Set', 30), " +
                "(2002, 'Mee Goreng', 12.00, 'A la carte', 25)");
        
        // Insert test payment methods with hashed passwords (SHA256)
        String tngPassword = PasswordUtil.hashPassword("tng123");
        String grabPassword = PasswordUtil.hashPassword("grab456");
        String bankPassword = PasswordUtil.hashPassword("bank789");
        
        stmt.execute("INSERT INTO payment_methods (payment_method_id, password, payment_type, wallet_id, balance, card_number, expiry_date) VALUES " +
                "(1, '" + tngPassword + "', 'TNG', 'TNG001', 100.00, NULL, NULL), " +
                "(2, '" + grabPassword + "', 'Grab', 'GRAB001', 50.00, NULL, NULL), " +
                "(3, '" + bankPassword + "', 'Bank', NULL, 200.00, '1234567890123456', '1225'), " +
                "(4, '" + tngPassword + "', 'TNG', 'TNG002', 75.00, NULL, NULL)");
    }
    
    /**
     * Clean up test database
     * NOTE: This method only cleans up test data tables (customers, foods, orders, etc.)
     * It does NOT touch the admins table to prevent interference with admin authentication tests.
     * The admins table is managed separately by AdminRepositoryTest and should never be cleaned here.
     * 
     * IMPORTANT: Do NOT add "DELETE FROM admins" to this method.
     * The admins table must be preserved for admin authentication tests.
     * 
     * @param connectionProvider Connection provider
     * @throws SQLException if cleanup fails
     */
    public static void cleanup(ConnectionProvider connectionProvider) throws SQLException {
        try (Connection conn = connectionProvider.getConnection();
             Statement stmt = conn.createStatement()) {
            // Only delete from test data tables - do NOT touch admins table
            stmt.execute("DELETE FROM order_details");
            stmt.execute("DELETE FROM orders");
            stmt.execute("DELETE FROM payment_methods");
            stmt.execute("DELETE FROM foods");
            stmt.execute("DELETE FROM customers");
            // IMPORTANT: Admins table is intentionally NOT cleaned up here
            // to preserve admin authentication test data
        }
    }
}

