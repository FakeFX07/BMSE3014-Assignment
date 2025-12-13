package model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

class AdminTest {

    @Test
    @DisplayName("Test Default Constructor and Setters")
    void testDefaultConstructorAndSetters() {
        //reate instance use default constructor
        Admin admin = new Admin();

        //Set values
        admin.setAdminId(1);
        admin.setName("Admin User");
        admin.setPassword("secure123");

        //Verify values using Getters
        assertEquals(1, admin.getAdminId());
        assertEquals("Admin User", admin.getName());
        assertEquals("secure123", admin.getPassword());
    }

    @Test
    @DisplayName("Test Constructor with Arguments")
    void testAllArgsConstructor() {
        //Create instance using full constructor
        Admin admin = new Admin(99, "Super Manager", "rootPass");

        //Verify values immediately
        assertEquals(99, admin.getAdminId());
        assertEquals("Super Manager", admin.getName());
        assertEquals("rootPass", admin.getPassword());
    }
}