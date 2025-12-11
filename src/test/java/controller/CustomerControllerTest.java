package controller;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import model.Customer;

import java.util.Random;

/**
 * JUnit 5 Test Class for CustomerController
 * Target: 100% Code Coverage
 */
class CustomerControllerTest {

    private CustomerController controller;
    private String uniquePhone; // Store a valid phone for reuse

    @BeforeEach
    void setUp() {
        controller = new CustomerController();
        // Generate a random phone for every test to avoid conflicts
        uniquePhone = "01" + (10000000 + new Random().nextInt(80000000));
    }

    // ==========================================
    // 1. Register Logic (Try/Catch & Duplicate)
    // ==========================================

    @Test
    @DisplayName("Register - Success")
    void testRegisterCustomer_Success() {
        Customer c = createValidCustomer(uniquePhone);
        Customer result = controller.registerCustomer(c);

        assertNotNull(result);
        assertTrue(result.getCustomerId() > 0);
    }

    @Test
    @DisplayName("Register - Fail (Invalid Data)")
    void testRegisterCustomer_Fail_InvalidData() {
        Customer c = createValidCustomer(uniquePhone);
        c.setAge(5); // Invalid Age
        
        Customer result = controller.registerCustomer(c);
        assertNull(result, "Should return null because age is invalid");
    }

    @Test
    @DisplayName("Register - Fail (Duplicate Phone)")
    void testRegisterCustomer_Fail_DuplicatePhone() {
        // 1. Register first time (Success)
        Customer c1 = createValidCustomer(uniquePhone);
        assertNotNull(controller.registerCustomer(c1));

        // 2. Register second time with SAME phone (Fail)
        Customer c2 = createValidCustomer(uniquePhone); 
        c2.setName("Clone User");
        
        // This hits the specific "Phone already registered" exception in Service
        Customer result = controller.registerCustomer(c2);
        assertNull(result, "Should return null because phone is duplicate");
    }

    // ==========================================
    // 2. Login Logic (All Branches)
    // ==========================================

    @Test
    @DisplayName("Login - Success")
    void testLogin_Success() {
        // Register first
        Customer c = createValidCustomer(uniquePhone);
        c.setPassword("mypass123");
        Customer registered = controller.registerCustomer(c);

        // Login
        Customer loggedIn = controller.login(registered.getCustomerId(), "mypass123");
        assertNotNull(loggedIn);
        assertEquals(registered.getCustomerId(), loggedIn.getCustomerId());
    }

    @Test
    @DisplayName("Login - Fail (User Not Found)")
    void testLogin_Fail_NotFound() {
        Customer result = controller.login(-999, "anyPass");
        assertNull(result);
    }

    @Test
    @DisplayName("Login - Fail (Wrong Password)")
    void testLogin_Fail_WrongPassword() {
        // Register first
        Customer c = createValidCustomer(uniquePhone);
        c.setPassword("correctPass");
        Customer registered = controller.registerCustomer(c);

        // Try login with wrong password
        Customer result = controller.login(registered.getCustomerId(), "wrongPass");
        assertNull(result);
    }

    // ==========================================
    // 3. Validation Logic (Logic & Exceptions)
    // ==========================================

    // --- Phone Number Checks (Crucial for Coverage) ---
    
    @Test
    @DisplayName("Check Phone - Valid & Unique")
    void testCheckPhoneNumber_Success() {
        assertDoesNotThrow(() -> controller.checkPhoneNumber(uniquePhone));
    }

    @Test
    @DisplayName("Check Phone - Invalid Format")
    void testCheckPhoneNumber_InvalidFormat() {
        assertThrows(IllegalArgumentException.class, () -> controller.checkPhoneNumber("123"));
    }

    @Test
    @DisplayName("Check Phone - Duplicate (The Missing 5%)")
    void testCheckPhoneNumber_Duplicate() {
        // 1. Register a number first
        Customer c = createValidCustomer(uniquePhone);
        controller.registerCustomer(c);

        // 2. Try to check the SAME number again
        // This forces the "if (exists)" branch to execute
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, 
            () -> controller.checkPhoneNumber(uniquePhone));
            
        // Optional: verify message confirms it's a duplicate error
        assertTrue(ex.getMessage().contains("already registered"));
    }

    // --- Is Phone Registered (True/False) ---

    @Test
    void testIsPhoneNumberRegistered() {
        // Case 1: False
        assertFalse(controller.isPhoneNumberRegistered(uniquePhone));

        // Case 2: True
        Customer c = createValidCustomer(uniquePhone);
        controller.registerCustomer(c);
        assertTrue(controller.isPhoneNumberRegistered(uniquePhone));
    }

    // --- Other Validations (Standard) ---

    @Test
    void testName() {
        assertTrue(controller.validateName("Ali Baba"));
        assertFalse(controller.validateName("Ali123"));
        assertDoesNotThrow(() -> controller.checkName("Ali"));
        assertThrows(IllegalArgumentException.class, () -> controller.checkName("Ali123"));
    }

    @Test
    void testAge() {
        assertTrue(controller.validateAge(20));
        assertFalse(controller.validateAge(10));
        assertDoesNotThrow(() -> controller.checkAge(20));
        assertThrows(IllegalArgumentException.class, () -> controller.checkAge(10));
    }

    @Test
    void testGender() {
        assertTrue(controller.validateGender("Male"));
        assertTrue(controller.validateGender("female")); // Case insensitive
        assertFalse(controller.validateGender("Robot"));
        assertDoesNotThrow(() -> controller.checkGender("Male"));
        assertThrows(IllegalArgumentException.class, () -> controller.checkGender("Robot"));
    }

    @Test
    void testPassword() {
        assertTrue(controller.validatePassword("123456"));
        assertFalse(controller.validatePassword("123"));
        assertDoesNotThrow(() -> controller.checkPassword("123456"));
        assertThrows(IllegalArgumentException.class, () -> controller.checkPassword("123"));
    }

    @Test
    void testPasswordConfirmation() {
        assertTrue(controller.validatePasswordConfirmation("abc", "abc"));
        assertFalse(controller.validatePasswordConfirmation("abc", "def"));
        assertDoesNotThrow(() -> controller.checkPasswordConfirmation("a", "a"));
        assertThrows(IllegalArgumentException.class, () -> controller.checkPasswordConfirmation("a", "b"));
    }

    // ==========================================
    // Helper Method (To keep code clean)
    // ==========================================
    private Customer createValidCustomer(String phone) {
        Customer c = new Customer();
        c.setName("Test Unit");
        c.setAge(25);
        c.setPhoneNumber(phone);
        c.setGender("Male");
        c.setPassword("password123");
        return c;
    }
}