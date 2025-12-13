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
import util.PasswordUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class CustomerServiceTest {

    private CustomerService customerService;
    private MockCustomerRepository mockRepository;

    @BeforeEach
    void setUp() {
        mockRepository = new MockCustomerRepository();
        customerService = new CustomerService(mockRepository);
    }

    // Validation Tests using Parameterized inputs to cover more ground
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
            "12345, false",       
            "012345678, false",    
            "012345678901, false",
            "0223456789, false",   
            "abcdefghij, false"    
        })
        void testValidatePhoneNumber(String phone, boolean expected) {
            assertEquals(expected, customerService.validatePhoneNumber(phone));
        }

        @Test
        void testValidatePhone_Null() {
            assertFalse(customerService.validatePhoneNumber(null));
        }

        @ParameterizedTest(name = "Gender '{0}' should be valid: {1}")
        @CsvSource({
            "Male, true",
            "Female, true",
            "male, true",   
            "FEMALE, true",
            "Robot, false",
            "Other, false"
        })
        void testValidateGender(String gender, boolean expected) {
            assertEquals(expected, customerService.validateGender(gender));
        }

        @ParameterizedTest
        @NullAndEmptySource
        void testValidateGender_NullOrEmpty(String gender) {
            assertFalse(customerService.validateGender(gender));
        }

        @ParameterizedTest(name = "Password '{0}' should be valid: {1}")
        @CsvSource({
            "12345, true",
            "password, true",
            "1234, false",
            ", false"     
        })
        void testValidatePassword(String password, boolean expected) {
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

    @Nested
    @DisplayName("Service Flow Tests")
    class ServiceFlowTests {

        @Test
        void testRegisterCustomer_Success() {
            Customer customer = createValidCustomer();
            
            Customer registered = customerService.registerCustomer(customer);
            
            assertNotNull(registered);
            assertTrue(registered.getCustomerId() > 0);
            assertEquals("John Doe", registered.getName());
        }

        @Test
        void testRegisterCustomer_InvalidData() {
            Customer customer = createValidCustomer();
            customer.setName("John123"); 
            
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
                customerService.registerCustomer(customer);
            });
            assertTrue(ex.getMessage().contains("Name"));
        }

        @Test
        void testRegisterCustomer_DuplicatePhone() {
            //Add existing user first
            Customer existing = createValidCustomer();
            existing.setPhoneNumber("0111111111");
            mockRepository.save(existing);

            //Try to register new user with same phone
            Customer duplicate = createValidCustomer();
            duplicate.setPhoneNumber("0111111111");

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> 
                customerService.registerCustomer(duplicate)
            );
            assertEquals("Phone number already registered! Please use a different number.", ex.getMessage());
        }

        @Test
        void testLogin_Success() {
            Customer c = createValidCustomer();
            c.setCustomerId(100);
            //Simulate hashed password in DB
            c.setPassword(PasswordUtil.hashPassword("secret123"));
            mockRepository.save(c);

            //Login with raw password
            Optional<Customer> result = customerService.login(100, "secret123");

            assertTrue(result.isPresent());
            assertEquals(100, result.get().getCustomerId());
        }

        @Test
        void testLogin_Failure() {
            Customer c = createValidCustomer();
            c.setCustomerId(100);
            c.setPassword(PasswordUtil.hashPassword("secret123"));
            mockRepository.save(c);

            // Wrong Password
            assertFalse(customerService.login(100, "wrong").isPresent());
            // Wrong ID
            assertFalse(customerService.login(999, "secret123").isPresent());
        }
    }

    //Helpers

    private Customer createValidCustomer() {
        Customer c = new Customer();
        c.setName("John Doe");
        c.setAge(25);
        c.setPhoneNumber("0123456789");
        c.setGender("Male");
        c.setPassword("password123");
        return c;
    }

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
            // Auto-generate ID if new
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