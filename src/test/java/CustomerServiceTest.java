package test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import model.Customer;
import repository.interfaces.ICustomerRepository;
import service.impl.CustomerService;

import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Optional;

/**
 * Customer Service Test
 * Tests customer service business logic
 * Follows TDD principles
 */
public class CustomerServiceTest {
    
    private CustomerService customerService;
    private MockCustomerRepository mockRepository;
    
    @BeforeEach
    void setUp() {
        mockRepository = new MockCustomerRepository();
        customerService = new CustomerService(mockRepository);
    }
    
    @Test
    @DisplayName("Test validateName - valid name")
    void testValidateName_Valid() {
        assertTrue(customerService.validateName("John Doe"));
        assertTrue(customerService.validateName("Mary Jane"));
    }
    
    @Test
    @DisplayName("Test validateName - invalid name with digits")
    void testValidateName_InvalidWithDigits() {
        assertFalse(customerService.validateName("John123"));
        assertFalse(customerService.validateName("123"));
    }
    
    @Test
    @DisplayName("Test validateAge - valid age")
    void testValidateAge_Valid() {
        assertTrue(customerService.validateAge(18));
        assertTrue(customerService.validateAge(50));
        assertTrue(customerService.validateAge(79));
    }
    
    @Test
    @DisplayName("Test validateAge - invalid age")
    void testValidateAge_Invalid() {
        assertFalse(customerService.validateAge(17));
        assertFalse(customerService.validateAge(80));
    }
    
    @Test
    @DisplayName("Test validatePhoneNumber - valid")
    void testValidatePhoneNumber_Valid() {
        assertTrue(customerService.validatePhoneNumber("0123456789"));
        assertTrue(customerService.validatePhoneNumber("01234567890"));
    }
    
    @Test
    @DisplayName("Test validatePhoneNumber - invalid")
    void testValidatePhoneNumber_Invalid() {
        assertFalse(customerService.validatePhoneNumber("12345"));
        assertFalse(customerService.validatePhoneNumber("123456789012"));
    }
    
    @Test
    @DisplayName("Test validateGender - valid")
    void testValidateGender_Valid() {
        assertTrue(customerService.validateGender("Male"));
        assertTrue(customerService.validateGender("Female"));
    }
    
    @Test
    @DisplayName("Test validateGender - invalid")
    void testValidateGender_Invalid() {
        assertFalse(customerService.validateGender("Other"));
        assertFalse(customerService.validateGender(""));
    }

    @Test
    @DisplayName("Test validateGender - null")
    void testValidateGender_Null() {
        assertFalse(customerService.validateGender(null));
    }
    
    @Test
    @DisplayName("Test validatePassword - valid")
    void testValidatePassword_Valid() {
        assertTrue(customerService.validatePassword("password123"));
        assertTrue(customerService.validatePassword("12345"));
    }
    
    @Test
    @DisplayName("Test validatePassword - invalid")
    void testValidatePassword_Invalid() {
        assertFalse(customerService.validatePassword("1234"));
        assertFalse(customerService.validatePassword(""));
    }

    @Test
    @DisplayName("Test validate fields - null name/phone")
    void testValidateNullFields() {
        assertFalse(customerService.validateName(null));
        assertFalse(customerService.validatePhoneNumber(null));
    }
    
    @Test
    @DisplayName("Test validatePasswordConfirmation - matching")
    void testValidatePasswordConfirmation_Matching() {
        assertTrue(customerService.validatePasswordConfirmation("password", "password"));
    }
    
    @Test
    @DisplayName("Test validatePasswordConfirmation - not matching")
    void testValidatePasswordConfirmation_NotMatching() {
        assertFalse(customerService.validatePasswordConfirmation("password", "different"));
    }
    
    @Test
    @DisplayName("Test registerCustomer - valid customer")
    void testRegisterCustomer_Valid() {
        Customer customer = new Customer();
        customer.setName("John Doe");
        customer.setAge(25);
        customer.setPhoneNumber("0123456789");
        customer.setGender("Male");
        customer.setPassword("password123");
        
        Customer registered = customerService.registerCustomer(customer);
        assertNotNull(registered);
        assertEquals("John Doe", registered.getName());
    }
    
    @Test
    @DisplayName("Test registerCustomer - invalid name")
    void testRegisterCustomer_InvalidName() {
        Customer customer = new Customer();
        customer.setName("John123");
        customer.setAge(25);
        customer.setPhoneNumber("0123456789");
        customer.setGender("Male");
        customer.setPassword("password123");
        
        assertThrows(IllegalArgumentException.class, () -> {
            customerService.registerCustomer(customer);
        });
    }

    @Test
    @DisplayName("Test registerCustomer - duplicate phone")
    void testRegisterCustomer_DuplicatePhone() {
        Customer existing = new Customer();
        existing.setCustomerId(1001);
        existing.setName("Existing User");
        existing.setAge(30);
        existing.setPhoneNumber("0111111111");
        existing.setGender("Female");
        existing.setPassword("password123");
        mockRepository.addCustomer(existing);

        Customer duplicate = new Customer();
        duplicate.setName("New User");
        duplicate.setAge(22);
        duplicate.setPhoneNumber("0111111111");
        duplicate.setGender("Female");
        duplicate.setPassword("password123");

        assertThrows(IllegalArgumentException.class, () -> customerService.registerCustomer(duplicate));
    }
    
    @Test
    @DisplayName("Test login - valid credentials")
    void testLogin_Valid() {
        Customer customer = new Customer(1000, "John Doe", 25, "0123456789", "Male", "password123");
        mockRepository.addCustomer(customer);
        
        Optional<Customer> result = customerService.login(1000, "password123");
        assertTrue(result.isPresent());
        assertEquals("John Doe", result.get().getName());
    }
    
    @Test
    @DisplayName("Test login - invalid credentials")
    void testLogin_Invalid() {
        Optional<Customer> result = customerService.login(9999, "wrongpassword");
        assertFalse(result.isPresent());
    }
    
    // Mock repository for testing
    private static class MockCustomerRepository implements ICustomerRepository {
        private java.util.Map<Integer, Customer> customers = new java.util.HashMap<>();
        
        @Override
        public Optional<Customer> findById(int customerId) {
            return Optional.ofNullable(customers.get(customerId));
        }
        
        @Override
        public Optional<Customer> findByPhoneNumber(String phoneNumber) {
            return customers.values().stream()
                    .filter(c -> c.getPhoneNumber().equals(phoneNumber))
                    .findFirst();
        }
        
        @Override
        public Optional<Customer> authenticate(int customerId, String password) {
            Customer customer = customers.get(customerId);
            if (customer != null && customer.getPassword().equals(password)) {
                return Optional.of(customer);
            }
            return Optional.empty();
        }
        
        @Override
        public Customer save(Customer customer) {
            customers.put(customer.getCustomerId(), customer);
            return customer;
        }
        
        @Override
        public int getNextCustomerId() {
            return customers.size() > 0 ? 
                    customers.keySet().stream().mapToInt(Integer::intValue).max().orElse(999) + 1 : 1000;
        }
        
        @Override
        public boolean existsByPhoneNumber(String phoneNumber) {
            return customers.values().stream()
                    .anyMatch(c -> c.getPhoneNumber().equals(phoneNumber));
        }
        
        public void addCustomer(Customer customer) {
            customers.put(customer.getCustomerId(), customer);
        }
    }
}
