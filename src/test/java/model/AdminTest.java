package model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

/**
 * JUnit 5 Test for Admin Model
 * Target: 100% Code Coverage
 */
class AdminTest {

    @Test
    @DisplayName("Test Default Constructor and Setters")
    void testDefaultConstructorAndSetters() {
        // 1. Create instance using default constructor
        Admin admin = new Admin();

        // 2. Set values
        admin.setAdminId(1);
        admin.setName("Admin User");
        admin.setPassword("secure123");

        // 3. Verify values using Getters
        assertEquals(1, admin.getAdminId());
        assertEquals("Admin User", admin.getName());
        assertEquals("secure123", admin.getPassword());
    }

    @Test
    @DisplayName("Test Constructor with Arguments")
    void testAllArgsConstructor() {
        // 1. Create instance using full constructor
        Admin admin = new Admin(99, "Super Manager", "rootPass");

        // 2. Verify values immediately
        assertEquals(99, admin.getAdminId());
        assertEquals("Super Manager", admin.getName());
        assertEquals("rootPass", admin.getPassword());
    }
}