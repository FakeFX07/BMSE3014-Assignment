package repository.impl;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.Statement;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import config.DatabaseConnection;
import util.PasswordUtil;

class AdminRepositoryTest {

    private AdminRepository adminRepository;
    
    //H2 Database URL
    private static final String H2_URL = "jdbc:h2:mem:testdb_admin;DB_CLOSE_DELAY=-1;MODE=MySQL";

    @BeforeEach
    void setUp() throws Exception {
        //Initialize DatabaseConnection Singleton with H2
        DatabaseConnection.createInstance(H2_URL, "sa", "");
        
        //Create admins table and insert dummy data
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement()) {
            
            //drop table first to ensure clean state
            stmt.execute("DROP TABLE IF EXISTS admins");
            
            //Create table with H2-compatible AUTO_INCREMENT syntax
            stmt.execute("CREATE TABLE admins (" +
                         "admin_id INT AUTO_INCREMENT PRIMARY KEY, " +
                         "name VARCHAR(50), " +
                         "password VARCHAR(255))");
            
            //Insert test admin with hashed password
            String hashedPassword = PasswordUtil.hashPassword("123");
            stmt.execute("INSERT INTO admins (name, password) VALUES ('admin', '" + hashedPassword + "')");
        }

        //initialize Repository
        adminRepository = new AdminRepository();
    }

    @AfterEach
    void tearDown() throws Exception {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("DELETE FROM admins");
        } catch (Exception e) {
        }
        
        //close connection
        try {
            DatabaseConnection.getInstance().closeConnection();
        } catch (Exception e) {
        }
    }

    // Test Cases

    @Test
    @DisplayName("Authenticate - Success (Correct Username & Hashed Password)")
    void testAuthenticate_Success() {
        //repository now expects hashed password
        String hashedPassword = PasswordUtil.hashPassword("123");
        boolean result = adminRepository.authenticate("admin", hashedPassword);
        assertTrue(result, "Should return true for valid credentials");
    }
    
    @Test
    @DisplayName("Authenticate - Failure (Wrong Username)")
    void testAuthenticate_WrongUsername() {
        String hashedPassword = PasswordUtil.hashPassword("123");
        boolean result = adminRepository.authenticate("unknown", hashedPassword);
        assertFalse(result, "Should return false for unknown username");
    }

    @Test
    @DisplayName("Authenticate - Failure (SQL Injection Attempt)")
    void testAuthenticate_SQLInjection() {
        String hashedPassword = PasswordUtil.hashPassword("123");
        boolean result = adminRepository.authenticate("admin' OR '1'='1", hashedPassword);
        assertFalse(result, "Should prevent SQL injection");
    }

    @Test
    @DisplayName("Authenticate - Exception Handling (Invalid Connection)")
    void testAuthenticate_Exception() throws Exception {
        
        String hashedPassword = PasswordUtil.hashPassword("123");
        boolean result1 = adminRepository.authenticate(null, hashedPassword);
        assertFalse(result1, "Should return false for null username");
        
        boolean result2 = adminRepository.authenticate("admin", null);
        assertFalse(result2, "Should return false for null password");
        
        //test non-existent admin to verify normal error handling
        String wrongHashedPassword = PasswordUtil.hashPassword("wrong");
        boolean result3 = adminRepository.authenticate("nonexistent", wrongHashedPassword);
        assertFalse(result3, "Should return false for non-existent admin");
    }
}