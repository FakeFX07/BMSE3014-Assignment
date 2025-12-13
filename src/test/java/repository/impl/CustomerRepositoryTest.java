package repository.impl;

import config.ConnectionProvider;
import config.DatabaseConnection;
import config.TestDatabaseSetup;
import model.Customer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.PasswordUtil;

import java.sql.SQLException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class CustomerRepositoryTest {
    
    private CustomerRepository repository;
    private ConnectionProvider connectionProvider;
    private static final String H2_URL = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=MySQL";
    
    @BeforeEach
    void setUp() throws SQLException {
        connectionProvider = DatabaseConnection.createInstance(H2_URL, "sa", "");
        
        //Reset DB state before each test
        try {
            TestDatabaseSetup.cleanup(connectionProvider);
        } catch (SQLException e) {
            //ignore if tables don't exist yet
        }
        
        TestDatabaseSetup.initializeSchema(connectionProvider);
        repository = new CustomerRepository(connectionProvider);
    }
    
    @AfterEach
    void tearDown() {
        // clean up after tests
        try {
            TestDatabaseSetup.cleanup(connectionProvider);
        } catch (SQLException e) {
            System.err.println("Cleanup warning: " + e.getMessage());
        } finally {
            if (connectionProvider instanceof DatabaseConnection) {
                try {
                    ((DatabaseConnection) connectionProvider).closeConnection();
                } catch (SQLException e) {
                    System.err.println("Connection close failed: " + e.getMessage());
                }
            }
        }
    }
    
    // Find Methods

    @Test
    void testFindById_Existing() {
        Optional<Customer> customer = repository.findById(1000);
        assertTrue(customer.isPresent());
        assertEquals("John Doe", customer.get().getName());
        assertEquals(25, customer.get().getAge());
    }
    
    @Test
    void testFindById_NonExisting() {
        Optional<Customer> customer = repository.findById(9999);
        assertFalse(customer.isPresent());
    }
    
    @Test
    void testFindByPhoneNumber_Existing() {
        Optional<Customer> customer = repository.findByPhoneNumber("0123456789");
        assertTrue(customer.isPresent());
        assertEquals(1000, customer.get().getCustomerId());
    }
    
    @Test
    void testFindByPhoneNumber_NonExisting() {
        Optional<Customer> customer = repository.findByPhoneNumber("9999999999");
        assertFalse(customer.isPresent());
    }
    
    //Auth Methods

    @Test
    void testAuthenticate_Valid() {
        // Repository checks against hashed password in DB
        String hashedPassword = util.PasswordUtil.hashPassword("password123");
        Optional<Customer> customer = repository.authenticate(1000, hashedPassword);
        
        assertTrue(customer.isPresent());
        assertEquals("John Doe", customer.get().getName());
    }
    
    @Test
    void testAuthenticate_Invalid() {
        String hashedPassword = util.PasswordUtil.hashPassword("wrongpassword");
        Optional<Customer> customer = repository.authenticate(1000, hashedPassword);
        assertFalse(customer.isPresent());
    }
    
    //Save Methods

    @Test
    void testSave_NewCustomer() {
        Customer customer = new Customer();
        customer.setName("Test User");
        customer.setAge(28);
        customer.setPhoneNumber("0129999999");
        customer.setGender("Male");
        customer.setPassword("testpass");
        
        Customer saved = repository.save(customer);
        assertTrue(saved.getCustomerId() > 0);
        assertEquals("Test User", saved.getName());
    }

    @Test
    void testSave_AllFields() {
        Customer customer = new Customer();
        customer.setName("Complete User");
        customer.setAge(35);
        customer.setPhoneNumber("0111111111");
        customer.setGender("Female");
        customer.setPassword("completepass");
        
        Customer saved = repository.save(customer);
        
        assertTrue(saved.getCustomerId() > 0);
        assertEquals("Complete User", saved.getName());
        assertEquals(35, saved.getAge());
        assertEquals("Female", saved.getGender());
    }

    @Test
    void testSave_WithGeneratedId() {
        Customer customer = new Customer();
        customer.setName("Auto ID User");
        customer.setAge(30);
        customer.setPhoneNumber("0133333333");
        customer.setGender("Female");
        customer.setPassword("autopass");
        
        Customer saved = repository.save(customer);
        
        Optional<Customer> found = repository.findById(saved.getCustomerId());
        assertTrue(found.isPresent());
        assertEquals("Auto ID User", found.get().getName());
    }

    @Test
    void testSave_MultipleCustomers() {
        Customer c1 = createCustomer("User One", "0161111111");
        Customer c2 = createCustomer("User Two", "0162222222");
        
        Customer saved1 = repository.save(c1);
        Customer saved2 = repository.save(c2);
        
        assertNotEquals(saved1.getCustomerId(), saved2.getCustomerId());
    }

    //ID Generation Logic

    @Test
    void testGetNextCustomerId() {
        int nextId = repository.getNextCustomerId();
        assertTrue(nextId >= 1000);
    }
    
    @Test
    void testGetNextCustomerId_NoCustomers() throws SQLException {
        TestDatabaseSetup.cleanup(connectionProvider); 
        int nextId = repository.getNextCustomerId();
        assertEquals(1000, nextId); 
    }

    @Test
    void testGetNextCustomerId_AfterCreation() {
        Customer customer = createCustomer("Test", "0144444444");
        repository.save(customer);
        
        int nextId = repository.getNextCustomerId();
        assertTrue(nextId > 1000);
    }

    @Test
    void testGetNextCustomerId_MaxIdLessThan1000() throws SQLException {
        TestDatabaseSetup.cleanup(connectionProvider);
        int nextId = repository.getNextCustomerId();
        assertEquals(1000, nextId);
    }

    @Test
    void testGetNextCustomerId_MaxIdExactly1000() throws SQLException {
        TestDatabaseSetup.cleanup(connectionProvider);
        insertRawCustomer(1000, "ID 1000", "0199999999");
        
        int nextId = repository.getNextCustomerId();
        assertEquals(1001, nextId);
    }

    @Test
    void testGetNextCustomerId_MaxIdLessThan1000Returns1000() throws SQLException {
        TestDatabaseSetup.cleanup(connectionProvider);
        //If max ID is 999, next should still snap to 1000
        insertRawCustomer(999, "ID 999", "0198888888");
        
        int nextId = repository.getNextCustomerId();
        assertEquals(1000, nextId);
    }

    //Utility/Existence Checks

    @Test
    void testExistsByPhoneNumber_Existing() {
        assertTrue(repository.existsByPhoneNumber("0123456789"));
    }
    
    @Test
    void testExistsByPhoneNumber_NonExisting() {
        assertFalse(repository.existsByPhoneNumber("9999999999"));
    }

    @Test
    void testDefaultConstructor() {
        CustomerRepository repo = new CustomerRepository();
        assertNotNull(repo);
    }

    //Mapping Validation

    @Test
    void testMapResultSetToCustomer_AllFields() {
        Optional<Customer> customer = repository.findById(1000);
        assertTrue(customer.isPresent());
        
        Customer c = customer.get();
        assertEquals(1000, c.getCustomerId());
        assertEquals("John Doe", c.getName());
        assertEquals(25, c.getAge());
        assertEquals("0123456789", c.getPhoneNumber());
        assertEquals("Male", c.getGender());
        
        assertNotNull(c.getPassword());
        assertEquals(64, c.getPassword().length()); 
    }

    //Helpers

    private Customer createCustomer(String name, String phone) {
        Customer c = new Customer();
        c.setName(name);
        c.setAge(25);
        c.setPhoneNumber(phone);
        c.setGender("Male");
        c.setPassword("pass");
        return c;
    }

    private void insertRawCustomer(int id, String name, String phone) throws SQLException {
        try (var conn = connectionProvider.getConnection();
             var stmt = conn.prepareStatement("INSERT INTO customers (customer_id, name, age, phone_number, gender, password) VALUES (?, ?, ?, ?, ?, ?)")) {
            stmt.setInt(1, id);
            stmt.setString(2, name);
            stmt.setInt(3, 25);
            stmt.setString(4, phone);
            stmt.setString(5, "Male");
            stmt.setString(6, PasswordUtil.hashPassword("pass"));
            stmt.executeUpdate();
        }
    }
}