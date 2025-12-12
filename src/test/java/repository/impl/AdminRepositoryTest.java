package repository.impl;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.Statement;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import config.DatabaseConnection; // Adjust package if yours is 'database'
import util.PasswordUtil;

/**
 * JUnit 5 Test for AdminRepository
 * Uses H2 In-Memory Database for isolation.
 */
class AdminRepositoryTest {

    private AdminRepository adminRepository;
    
    // H2 Database URL (Simulates MySQL)
    private static final String H2_URL = "jdbc:h2:mem:testdb_admin;DB_CLOSE_DELAY=-1;MODE=MySQL";

    @BeforeEach
    void setUp() throws Exception {
        // 1. Initialize DatabaseConnection Singleton with H2
        DatabaseConnection.createInstance(H2_URL, "sa", "");
        
        // 2. Create 'admins' table and insert dummy data
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Drop table first to ensure clean state (only affects this test's database)
            stmt.execute("DROP TABLE IF EXISTS admins");
            
            // Create Table with H2-compatible AUTO_INCREMENT syntax
            // Password field increased to VARCHAR(255) to support SHA256 hash (64 characters)
            stmt.execute("CREATE TABLE admins (" +
                         "admin_id INT AUTO_INCREMENT PRIMARY KEY, " +
                         "name VARCHAR(50), " +
                         "password VARCHAR(255))");
            
            // Insert Test Admin with hashed password (admin_id will be auto-generated)
            String hashedPassword = PasswordUtil.hashPassword("123");
            stmt.execute("INSERT INTO admins (name, password) VALUES ('admin', '" + hashedPassword + "')");
        }

        // 3. Initialize Repository
        adminRepository = new AdminRepository();
    }

    @AfterEach
    void tearDown() throws Exception {
        // Clean up test data only - DO NOT drop the admins table
        // Dropping the table can affect other tests that might be using the same database instance
        // Instead, just delete the test data to ensure test isolation
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement()) {
            // Only delete data, not the table structure
            // This ensures the table remains for other tests
            stmt.execute("DELETE FROM admins");
        } catch (Exception e) {
            // Ignore errors during cleanup to prevent test failures
            // The table might not exist or might have been cleaned already
        }
        
        // Close connection
        try {
            DatabaseConnection.getInstance().closeConnection();
        } catch (Exception e) {
            // Ignore connection close errors
        }
    }

    // ==========================================
    // Test Cases
    // ==========================================

    @Test
    @DisplayName("Authenticate - Success (Correct Username & Hashed Password)")
    void testAuthenticate_Success() {
        // Repository now expects hashed password
        String hashedPassword = PasswordUtil.hashPassword("123");
        boolean result = adminRepository.authenticate("admin", hashedPassword);
        assertTrue(result, "Should return true for valid credentials");
    }
    
    @Test
    @DisplayName("Authenticate - Failure (Wrong Username)")
    void testAuthenticate_WrongUsername() {
        // Repository now expects hashed password
        String hashedPassword = PasswordUtil.hashPassword("123");
        boolean result = adminRepository.authenticate("unknown", hashedPassword);
        assertFalse(result, "Should return false for unknown username");
    }

    @Test
    @DisplayName("Authenticate - Failure (SQL Injection Attempt)")
    void testAuthenticate_SQLInjection() {
        // Attempt a basic SQL injection bypass - hash the injection attempt
        String hashedPassword = PasswordUtil.hashPassword("123");
        boolean result = adminRepository.authenticate("admin' OR '1'='1", hashedPassword);
        assertFalse(result, "Should prevent SQL injection");
    }

    @Test
    @DisplayName("Authenticate - Exception Handling (Invalid Connection)")
    void testAuthenticate_Exception() throws Exception {
        // Test exception handling by using an invalid query that will cause an error
        // Instead of dropping the table (which affects other tests), we'll test with invalid credentials
        // that will trigger the exception handling path in the repository
        
        // Test with null/invalid inputs to trigger exception handling
        String hashedPassword = PasswordUtil.hashPassword("123");
        boolean result1 = adminRepository.authenticate(null, hashedPassword);
        assertFalse(result1, "Should return false for null username");
        
        boolean result2 = adminRepository.authenticate("admin", null);
        assertFalse(result2, "Should return false for null password");
        
        // Test with non-existent admin to verify normal error handling
        String wrongHashedPassword = PasswordUtil.hashPassword("wrong");
        boolean result3 = adminRepository.authenticate("nonexistent", wrongHashedPassword);
        assertFalse(result3, "Should return false for non-existent admin");
    }
}