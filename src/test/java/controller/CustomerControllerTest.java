package controller;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import model.Customer;

import java.util.Random;

class CustomerControllerTest {

    private CustomerController controller;
    private String uniquePhone;

    @BeforeEach
    void setUp() {
        controller = new CustomerController();
        //Generate random phone number
        uniquePhone = "01" + (10000000 + new Random().nextInt(80000000));
    }

    // Test registration features
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
        c.setAge(5); //invalid age
        
        Customer result = controller.registerCustomer(c);
        assertNull(result, "Registration should fail due to invalid age");
    }

    @Test
    @DisplayName("Register - Fail (Duplicate Phone)")
    void testRegisterCustomer_Fail_DuplicatePhone() {
        //Register first user
        Customer c1 = createValidCustomer(uniquePhone);
        assertNotNull(controller.registerCustomer(c1));

        //Try to register second user with same phone number
        Customer c2 = createValidCustomer(uniquePhone); 
        c2.setName("Clone User");
        
        Customer result = controller.registerCustomer(c2);
        assertNull(result, "Should return null for duplicate phone");
    }

    //Test Login features
    @Test
    @DisplayName("Login - Success")
    void testLogin_Success() {
        Customer c = createValidCustomer(uniquePhone);
        c.setPassword("mypass123");
        Customer registered = controller.registerCustomer(c);

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
        Customer c = createValidCustomer(uniquePhone);
        c.setPassword("correctPass");
        Customer registered = controller.registerCustomer(c);

        // try login with wrong password
        Customer result = controller.login(registered.getCustomerId(), "wrongPass");
        assertNull(result);
    }

    // Field Validation Tests
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
    @DisplayName("Check Phone - Duplicate")
    void testCheckPhoneNumber_Duplicate() {
        Customer c = createValidCustomer(uniquePhone);
        controller.registerCustomer(c);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, 
            () -> controller.checkPhoneNumber(uniquePhone));
            
        assertTrue(ex.getMessage().contains("already registered"));
    }

    @Test
    void testIsPhoneNumberRegistered() {
        // Test when phone does not exist
        assertFalse(controller.isPhoneNumberRegistered(uniquePhone));

        // Test when phone exists
        Customer c = createValidCustomer(uniquePhone);
        controller.registerCustomer(c);
        assertTrue(controller.isPhoneNumberRegistered(uniquePhone));
    }

    @Test
    void testName() {
        //Validate name format
        assertTrue(controller.validateName("Ali Baba"));
        assertFalse(controller.validateName("Ali123")); //no numbers allowed
        
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
        assertTrue(controller.validateGender("female")); 
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