package repository.impl;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.Statement;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import config.DatabaseConnection; // Adjust package if yours is 'database'

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
            
            // Create Table
            stmt.execute("CREATE TABLE IF NOT EXISTS admins (" +
                         "admin_id INT AUTO_INCREMENT PRIMARY KEY, " +
                         "name VARCHAR(50), " +
                         "password VARCHAR(50))");
            
            // Insert Test Admin
            stmt.execute("INSERT INTO admins (name, password) VALUES ('admin', '123')");
        }

        // 3. Initialize Repository
        adminRepository = new AdminRepository();
    }

    @AfterEach
    void tearDown() throws Exception {
        // Clean up database after each test
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS admins");
        }
        
        // Close connection
        DatabaseConnection.getInstance().closeConnection();
    }

    // ==========================================
    // Test Cases
    // ==========================================

    @Test
    @DisplayName("Authenticate - Success (Correct Username & Password)")
    void testAuthenticate_Success() {
        boolean result = adminRepository.authenticate("admin", "123");
        assertTrue(result, "Should return true for valid credentials");
    }

    @Test
    @DisplayName("Authenticate - Failure (Wrong Password)")
    void testAuthenticate_WrongPassword() {
        boolean result = adminRepository.authenticate("admin", "wrongPass");
        assertFalse(result, "Should return false for wrong password");
    }

    @Test
    @DisplayName("Authenticate - Failure (Wrong Username)")
    void testAuthenticate_WrongUsername() {
        boolean result = adminRepository.authenticate("unknown", "123");
        assertFalse(result, "Should return false for unknown username");
    }

    @Test
    @DisplayName("Authenticate - Failure (SQL Injection Attempt)")
    void testAuthenticate_SQLInjection() {
        // Attempt a basic SQL injection bypass
        boolean result = adminRepository.authenticate("admin' OR '1'='1", "123");
        assertFalse(result, "Should prevent SQL injection");
    }

    @Test
    @DisplayName("Authenticate - Exception Handling (Table Missing)")
    void testAuthenticate_Exception() throws Exception {
        // Force an error by dropping the table before the query runs
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DROP TABLE admins");
        }

        // Should catch SQLException and return false
        boolean result = adminRepository.authenticate("admin", "123");
        assertFalse(result, "Should return false (gracefully handle exception) when DB fails");
    }
}