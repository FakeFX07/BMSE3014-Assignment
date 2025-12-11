package service.impl;

import model.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import repository.interfaces.ICustomerRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CustomerService Test (Optimized)
 * Uses JUnit 5 Parameterized Tests to reduce code duplication
 */
public class CustomerServiceTest {

    private CustomerService customerService;
    private MockCustomerRepository mockRepository;

    @BeforeEach
    void setUp() {
        mockRepository = new MockCustomerRepository();
        customerService = new CustomerService(mockRepository);
    }

    // ==========================================
    // 1. Validation Logic Tests (Grouped)
    // ==========================================
    @Nested
    @DisplayName("Field Validation Tests")
    class ValidationTests {

        @ParameterizedTest(name = "Name '{0}' should be valid: {1}")
        @CsvSource({
            "John Doe, true",
            "Mary Jane, true",
            "John123, false",
            "123, false",
            "John@Doe, false"
        })
        void testValidateName(String name, boolean expected) {
            assertEquals(expected, customerService.validateName(name));
        }

        @Test
        @DisplayName("Name null checks")
        void testValidateName_Null() {
            assertFalse(customerService.validateName(null));
        }

        @ParameterizedTest(name = "Age {0} should be valid: {1}")
        @CsvSource({
            "18, true",
            "79, true",
            "50, true",
            "17, false",
            "80, false",
            "-1, false"
        })
        void testValidateAge(int age, boolean expected) {
            assertEquals(expected, customerService.validateAge(age));
        }

        @ParameterizedTest(name = "Phone '{0}' should be valid: {1}")
        @CsvSource({
            "0123456789, true",
            "01234567890, true",
            "12345, false",      // Too short
            "012345678, false",  // Too short
            "012345678901, false", // Too long
            "0223456789, false", // Wrong prefix
            "abcdefghij, false"  // Non-digit
        })
        void testValidatePhoneNumber(String phone, boolean expected) {
            assertEquals(expected, customerService.validatePhoneNumber(phone));
        }

        @Test
        @DisplayName("Phone null checks")
        void testValidatePhone_Null() {
            assertFalse(customerService.validatePhoneNumber(null));
        }

        @ParameterizedTest(name = "Gender '{0}' should be valid: {1}")
        @CsvSource({
            "Male, true",
            "Female, true",
            "male, true",   // Case insensitive check
            "FEMALE, true",
            "Robot, false",
            "Other, false"
        })
        void testValidateGender(String gender, boolean expected) {
            assertEquals(expected, customerService.validateGender(gender));
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("Gender null/empty checks")
        void testValidateGender_NullOrEmpty(String gender) {
            assertFalse(customerService.validateGender(gender));
        }

        @ParameterizedTest(name = "Password '{0}' should be valid: {1}")
        @CsvSource({
            "12345, true",
            "password, true",
            "1234, false",
            ", false" // Null check implicit in CSV source if handled, but safer to use explicit null check
        })
        void testValidatePassword(String password, boolean expected) {
            // Convert "null" string to actual null for test
            String input = "".equals(password) ? null : password;
            assertEquals(expected, customerService.validatePassword(input));
        }

        @ParameterizedTest
        @CsvSource({
            "password, password, true",
            "password, different, false",
            "password, , false",
            ", password, false"
        })
        void testValidatePasswordConfirmation(String pass, String confirm, boolean expected) {
            assertEquals(expected, customerService.validatePasswordConfirmation(pass, confirm));
        }
    }

    // ==========================================
    // 2. Business Logic Tests (Registration/Login)
    // ==========================================
    @Nested
    @DisplayName("Service Flow Tests")
    class ServiceFlowTests {

        @Test
        @DisplayName("Register - Success Scenario")
        void testRegisterCustomer_Success() {
            Customer customer = createValidCustomer();
            
            Customer registered = customerService.registerCustomer(customer);
            
            assertNotNull(registered);
            assertTrue(registered.getCustomerId() > 0); // Verify ID generated
            assertEquals("John Doe", registered.getName());
        }

        @Test
        @DisplayName("Register - Throws Exception on Invalid Field")
        void testRegisterCustomer_InvalidData() {
            Customer customer = createValidCustomer();
            customer.setName("John123"); // Invalid Name
            
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
                customerService.registerCustomer(customer);
            });
            assertTrue(ex.getMessage().contains("Name"));
        }

        @Test
        @DisplayName("Register - Throws Exception on Duplicate Phone")
        void testRegisterCustomer_DuplicatePhone() {
            // 1. Pre-fill mock repo
            Customer existing = createValidCustomer();
            existing.setPhoneNumber("0111111111");
            mockRepository.save(existing);

            // 2. Try to register new user with same phone
            Customer duplicate = createValidCustomer();
            duplicate.setPhoneNumber("0111111111");

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> 
                customerService.registerCustomer(duplicate)
            );
            assertEquals("Phone number already registered! Please use a different number.", ex.getMessage());
        }

        @Test
        @DisplayName("Login - Success")
        void testLogin_Success() {
            // Pre-fill
            Customer c = createValidCustomer();
            c.setCustomerId(100);
            c.setPassword("secret123");
            mockRepository.save(c);

            // Action
            Optional<Customer> result = customerService.login(100, "secret123");

            // Assert
            assertTrue(result.isPresent());
            assertEquals(100, result.get().getCustomerId());
        }

        @Test
        @DisplayName("Login - Failure (Wrong ID or Password)")
        void testLogin_Failure() {
            // Pre-fill
            Customer c = createValidCustomer();
            c.setCustomerId(100);
            c.setPassword("secret123");
            mockRepository.save(c);

            // Wrong Password
            assertFalse(customerService.login(100, "wrong").isPresent());
            // Wrong ID
            assertFalse(customerService.login(999, "secret123").isPresent());
        }
    }

    // ==========================================
    // Helpers & Mocks
    // ==========================================

    private Customer createValidCustomer() {
        Customer c = new Customer();
        c.setName("John Doe");
        c.setAge(25);
        c.setPhoneNumber("0123456789");
        c.setGender("Male");
        c.setPassword("password123");
        return c;
    }

    /**
     * Internal Mock Repository
     */
    private static class MockCustomerRepository implements ICustomerRepository {
        private final Map<Integer, Customer> db = new HashMap<>();
        private int idCounter = 1000;

        @Override
        public Optional<Customer> findById(int customerId) {
            return Optional.ofNullable(db.get(customerId));
        }

        @Override
        public Optional<Customer> findByPhoneNumber(String phoneNumber) {
            return db.values().stream()
                    .filter(c -> c.getPhoneNumber().equals(phoneNumber))
                    .findFirst();
        }

        @Override
        public Optional<Customer> authenticate(int customerId, String password) {
            Customer c = db.get(customerId);
            if (c != null && c.getPassword().equals(password)) {
                return Optional.of(c);
            }
            return Optional.empty();
        }

        @Override
        public Customer save(Customer customer) {
            // Mimic DB behavior: if ID is 0, generate new one
            if (customer.getCustomerId() == 0) {
                customer.setCustomerId(getNextCustomerId());
            }
            db.put(customer.getCustomerId(), customer);
            return customer;
        }

        @Override
        public int getNextCustomerId() {
            return ++idCounter;
        }

        @Override
        public boolean existsByPhoneNumber(String phoneNumber) {
            return db.values().stream().anyMatch(c -> c.getPhoneNumber().equals(phoneNumber));
        }
    }
}