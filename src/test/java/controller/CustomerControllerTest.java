package controller;

import model.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import service.interfaces.ICustomerService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Customer Controller Test
 */
public class CustomerControllerTest {
    
    private CustomerController controller;
    private ICustomerService mockService;
    
    @BeforeEach
    void setUp() {
        mockService = mock(ICustomerService.class);
        controller = new CustomerController(mockService);
    }
    
    @Test
    @DisplayName("Test registerCustomer - success")
    void testRegisterCustomer_Success() {
        Customer customer = new Customer();
        customer.setName("John Doe");
        customer.setAge(25);
        customer.setPhoneNumber("0123456789");
        customer.setGender("Male");
        customer.setPassword("password123");
        
        Customer registered = new Customer(1000, "John Doe", 25, "0123456789", "Male", "password123");
        when(mockService.registerCustomer(customer)).thenReturn(registered);
        
        Customer result = controller.registerCustomer(customer);
        assertNotNull(result);
        assertEquals(1000, result.getCustomerId());
    }
    
    @Test
    @DisplayName("Test registerCustomer - validation failure")
    void testRegisterCustomer_ValidationFailure() {
        Customer customer = new Customer();
        customer.setName("John123");
        
        when(mockService.registerCustomer(customer)).thenThrow(new IllegalArgumentException("Invalid name"));
        
        Customer result = controller.registerCustomer(customer);
        assertNull(result);
    }
    
    @Test
    @DisplayName("Test login - success")
    void testLogin_Success() {
        Customer customer = new Customer(1000, "John Doe", 25, "0123456789", "Male", "password123");
        when(mockService.login(1000, "password123")).thenReturn(Optional.of(customer));
        
        Customer result = controller.login(1000, "password123");
        assertNotNull(result);
        assertEquals("John Doe", result.getName());
    }
    
    @Test
    @DisplayName("Test login - failure")
    void testLogin_Failure() {
        when(mockService.login(9999, "wrong")).thenReturn(Optional.empty());
        
        Customer result = controller.login(9999, "wrong");
        assertNull(result);
    }
    
    @Test
    @DisplayName("Test validateName - delegates to service")
    void testValidateName() {
        when(mockService.validateName("John Doe")).thenReturn(true);
        when(mockService.validateName("John123")).thenReturn(false);
        
        assertTrue(controller.validateName("John Doe"));
        assertFalse(controller.validateName("John123"));
    }
    
    @Test
    @DisplayName("Test validateAge - delegates to service")
    void testValidateAge() {
        when(mockService.validateAge(25)).thenReturn(true);
        when(mockService.validateAge(15)).thenReturn(false);
        
        assertTrue(controller.validateAge(25));
        assertFalse(controller.validateAge(15));
    }
    
    @Test
    @DisplayName("Test validatePhoneNumber - delegates to service")
    void testValidatePhoneNumber() {
        when(mockService.validatePhoneNumber("0123456789")).thenReturn(true);
        assertTrue(controller.validatePhoneNumber("0123456789"));
    }
    
    @Test
    @DisplayName("Test validateGender - delegates to service")
    void testValidateGender() {
        when(mockService.validateGender("Male")).thenReturn(true);
        assertTrue(controller.validateGender("Male"));
    }
    
    @Test
    @DisplayName("Test validatePassword - delegates to service")
    void testValidatePassword() {
        when(mockService.validatePassword("password123")).thenReturn(true);
        assertTrue(controller.validatePassword("password123"));
    }
    
    @Test
    @DisplayName("Test validatePasswordConfirmation - delegates to service")
    void testValidatePasswordConfirmation() {
        when(mockService.validatePasswordConfirmation("pass", "pass")).thenReturn(true);
        assertTrue(controller.validatePasswordConfirmation("pass", "pass"));
    }
}

