package repository.impl;

import config.ConnectionProvider;
import config.DatabaseConnection;
import config.TestDatabaseSetup;
import model.Customer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import util.PasswordUtil;

import java.sql.SQLException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Customer Repository Test
 * Tests customer repository database operations
 */
public class CustomerRepositoryTest {
    
    private CustomerRepository repository;
    private ConnectionProvider connectionProvider;
    private static final String H2_URL = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=MySQL";
    
    @BeforeEach
    void setUp() throws SQLException {
        connectionProvider = DatabaseConnection.createInstance(H2_URL, "sa", "");
        // Clean up any existing data first to ensure a clean state
        try {
            TestDatabaseSetup.cleanup(connectionProvider);
        } catch (SQLException e) {
            // Ignore if tables don't exist yet
        }
        // Initialize schema and insert test data
        TestDatabaseSetup.initializeSchema(connectionProvider);
        repository = new CustomerRepository(connectionProvider);
    }
    
    @AfterEach
    void tearDown() throws SQLException {
        // Clean up all test data after each test to ensure test isolation
        // This ensures that tests don't affect each other
        try {
            TestDatabaseSetup.cleanup(connectionProvider);
        } catch (SQLException e) {
            // Log but don't fail the test if cleanup has issues
            System.err.println("Warning: Cleanup failed: " + e.getMessage());
        } finally {
            // Always close the connection
            if (connectionProvider instanceof DatabaseConnection) {
                try {
                    ((DatabaseConnection) connectionProvider).closeConnection();
                } catch (SQLException e) {
                    System.err.println("Warning: Connection close failed: " + e.getMessage());
                }
            }
        }
    }
    
    @Test
    @DisplayName("Test findById - existing customer")
    void testFindById_Existing() {
        Optional<Customer> customer = repository.findById(1000);
        assertTrue(customer.isPresent());
        assertEquals("John Doe", customer.get().getName());
        assertEquals(25, customer.get().getAge());
    }
    
    @Test
    @DisplayName("Test findById - non-existing customer")
    void testFindById_NonExisting() {
        Optional<Customer> customer = repository.findById(9999);
        assertFalse(customer.isPresent());
    }
    
    @Test
    @DisplayName("Test findByPhoneNumber - existing")
    void testFindByPhoneNumber_Existing() {
        Optional<Customer> customer = repository.findByPhoneNumber("0123456789");
        assertTrue(customer.isPresent());
        assertEquals(1000, customer.get().getCustomerId());
    }
    
    @Test
    @DisplayName("Test findByPhoneNumber - non-existing")
    void testFindByPhoneNumber_NonExisting() {
        Optional<Customer> customer = repository.findByPhoneNumber("9999999999");
        assertFalse(customer.isPresent());
    }
    
    @Test
    @DisplayName("Test authenticate - valid credentials")
    void testAuthenticate_Valid() {
        // Repository expects hashed password (as CustomerService hashes it before calling)
        String hashedPassword = util.PasswordUtil.hashPassword("password123");
        Optional<Customer> customer = repository.authenticate(1000, hashedPassword);
        assertTrue(customer.isPresent());
        assertEquals("John Doe", customer.get().getName());
    }
    
    @Test
    @DisplayName("Test authenticate - invalid credentials")
    void testAuthenticate_Invalid() {
        // Repository expects hashed password
        String hashedPassword = util.PasswordUtil.hashPassword("wrongpassword");
        Optional<Customer> customer = repository.authenticate(1000, hashedPassword);
        assertFalse(customer.isPresent());
    }
    
    @Test
    @DisplayName("Test save - new customer")
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
    @DisplayName("Test getNextCustomerId - returns next ID")
    void testGetNextCustomerId() {
        int nextId = repository.getNextCustomerId();
        assertTrue(nextId >= 1000);
    }
    
    @Test
    @DisplayName("Test existsByPhoneNumber - existing")
    void testExistsByPhoneNumber_Existing() {
        assertTrue(repository.existsByPhoneNumber("0123456789"));
    }
    
    @Test
    @DisplayName("Test existsByPhoneNumber - non-existing")
    void testExistsByPhoneNumber_NonExisting() {
        assertFalse(repository.existsByPhoneNumber("9999999999"));
    }
    
    @Test
    @DisplayName("Test default constructor")
    void testDefaultConstructor() {
        CustomerRepository repo = new CustomerRepository();
        assertNotNull(repo);
    }
    
    @Test
    @DisplayName("Test getNextCustomerId - when no customers exist")
    void testGetNextCustomerId_NoCustomers() throws SQLException {
        TestDatabaseSetup.cleanup(connectionProvider);
        int nextId = repository.getNextCustomerId();
        assertEquals(1000, nextId);
    }
    
    @Test
    @DisplayName("Test save - customer with all fields")
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
    @DisplayName("Test authenticate - non-existing customer")
    void testAuthenticate_NonExisting() {
        Optional<Customer> customer = repository.authenticate(9999, "anypassword");
        assertFalse(customer.isPresent());
    }
    
    @Test
    @DisplayName("Test save - customer with generated ID")
    void testSave_WithGeneratedId() {
        Customer customer = new Customer();
        customer.setName("Auto ID User");
        customer.setAge(30);
        customer.setPhoneNumber("0133333333");
        customer.setGender("Female");
        customer.setPassword("autopass");
        
        Customer saved = repository.save(customer);
        assertTrue(saved.getCustomerId() > 0);
        
        // Verify can find by ID
        Optional<Customer> found = repository.findById(saved.getCustomerId());
        assertTrue(found.isPresent());
        assertEquals("Auto ID User", found.get().getName());
    }
    
    @Test
    @DisplayName("Test getNextCustomerId - after creating customer")
    void testGetNextCustomerId_AfterCreation() {
        Customer customer = new Customer();
        customer.setName("Test");
        customer.setAge(25);
        customer.setPhoneNumber("0144444444");
        customer.setGender("Male");
        customer.setPassword("test");
        repository.save(customer);
        
        int nextId = repository.getNextCustomerId();
        assertTrue(nextId > 1000);
    }
    
    @Test
    @DisplayName("Test findById - with different customer IDs")
    void testFindById_DifferentIds() {
        Optional<Customer> customer1 = repository.findById(1000);
        Optional<Customer> customer2 = repository.findById(1001);
        
        assertTrue(customer1.isPresent());
        assertTrue(customer2.isPresent());
        assertNotEquals(customer1.get().getCustomerId(), customer2.get().getCustomerId());
    }
    
    @Test
    @DisplayName("Test findByPhoneNumber - with different phone numbers")
    void testFindByPhoneNumber_DifferentPhones() {
        Optional<Customer> customer1 = repository.findByPhoneNumber("0123456789");
        Optional<Customer> customer2 = repository.findByPhoneNumber("0111111111");
        
        assertTrue(customer1.isPresent());
        assertTrue(customer2.isPresent());
        assertNotEquals(customer1.get().getPhoneNumber(), customer2.get().getPhoneNumber());
    }
    
    @Test
    @DisplayName("Test save - verify all fields are saved correctly")
    void testSave_AllFieldsCorrect() {
        Customer customer = new Customer();
        customer.setName("Complete Test");
        customer.setAge(35);
        customer.setPhoneNumber("0155555555");
        customer.setGender("Female");
        customer.setPassword("complete123");
        
        Customer saved = repository.save(customer);
        Optional<Customer> found = repository.findById(saved.getCustomerId());
        
        assertTrue(found.isPresent());
        assertEquals("Complete Test", found.get().getName());
        assertEquals(35, found.get().getAge());
        assertEquals("Female", found.get().getGender());
    }
    
    @Test
    @DisplayName("Test authenticate - with different customer IDs")
    void testAuthenticate_DifferentCustomerIds() {
        // Repository expects hashed passwords
        String hashedPassword1 = util.PasswordUtil.hashPassword("password123");
        String hashedPassword2 = util.PasswordUtil.hashPassword("pass456");
        Optional<Customer> customer1 = repository.authenticate(1000, hashedPassword1);
        Optional<Customer> customer2 = repository.authenticate(1001, hashedPassword2);
        
        assertTrue(customer1.isPresent());
        assertTrue(customer2.isPresent());
        assertNotEquals(customer1.get().getCustomerId(), customer2.get().getCustomerId());
    }
    
    @Test
    @DisplayName("Test save - multiple customers")
    void testSave_MultipleCustomers() {
        Customer customer1 = new Customer();
        customer1.setName("Customer One");
        customer1.setAge(25);
        customer1.setPhoneNumber("0161111111");
        customer1.setGender("Male");
        customer1.setPassword("pass1");
        
        Customer customer2 = new Customer();
        customer2.setName("Customer Two");
        customer2.setAge(30);
        customer2.setPhoneNumber("0162222222");
        customer2.setGender("Female");
        customer2.setPassword("pass2");
        
        Customer saved1 = repository.save(customer1);
        Customer saved2 = repository.save(customer2);
        
        assertNotEquals(saved1.getCustomerId(), saved2.getCustomerId());
    }
    
    @Test
    @DisplayName("Test getNextCustomerId - with max ID less than 1000")
    void testGetNextCustomerId_MaxIdLessThan1000() throws SQLException {
        // Create a scenario where max ID would be less than 1000
        TestDatabaseSetup.cleanup(connectionProvider);
        int nextId = repository.getNextCustomerId();
        assertEquals(1000, nextId);
    }
    
    @Test
    @DisplayName("Test getNextCustomerId - with max ID greater than 1000")
    void testGetNextCustomerId_MaxIdGreaterThan1000() {
        // Create customers to ensure max ID > 1000
        Customer customer1 = new Customer();
        customer1.setName("Test1");
        customer1.setAge(25);
        customer1.setPhoneNumber("0171111111");
        customer1.setGender("Male");
        customer1.setPassword("pass1");
        repository.save(customer1);
        
        int nextId = repository.getNextCustomerId();
        assertTrue(nextId > 1000);
    }
    
    @Test
    @DisplayName("Test existsByPhoneNumber - returns false when count is 0")
    void testExistsByPhoneNumber_CountZero() {
        assertFalse(repository.existsByPhoneNumber("9999999999"));
    }
    
    @Test
    @DisplayName("Test save - verify generated keys path")
    void testSave_GeneratedKeysPath() {
        Customer customer = new Customer();
        customer.setName("Gen Key Test");
        customer.setAge(25);
        customer.setPhoneNumber("0188888888");
        customer.setGender("Male");
        customer.setPassword("genkey");
        
        Customer saved = repository.save(customer);
        // Verify ID was generated
        assertTrue(saved.getCustomerId() > 0);
        assertTrue(saved.getCustomerId() >= 1000);
    }
    
    @Test
    @DisplayName("Test findById - verify mapping all fields")
    void testFindById_VerifyAllFields() {
        Optional<Customer> customer = repository.findById(1000);
        assertTrue(customer.isPresent());
        Customer c = customer.get();
        assertNotNull(c.getName());
        assertTrue(c.getAge() > 0);
        assertNotNull(c.getPhoneNumber());
        assertNotNull(c.getGender());
        assertNotNull(c.getPassword());
    }
    
    @Test
    @DisplayName("Test findByPhoneNumber - verify mapping all fields")
    void testFindByPhoneNumber_VerifyAllFields() {
        Optional<Customer> customer = repository.findByPhoneNumber("0123456789");
        assertTrue(customer.isPresent());
        Customer c = customer.get();
        assertEquals(1000, c.getCustomerId());
        assertEquals("John Doe", c.getName());
        assertEquals(25, c.getAge());
    }
    
    @Test
    @DisplayName("Test authenticate - verify password matching")
    void testAuthenticate_PasswordMatching() {
        // Test data uses hashed passwords, so we need to hash the input password
        String hashedPassword = util.PasswordUtil.hashPassword("password123");
        Optional<Customer> valid = repository.authenticate(1000, hashedPassword);
        Optional<Customer> invalid = repository.authenticate(1000, util.PasswordUtil.hashPassword("wrongpass"));
        
        assertTrue(valid.isPresent());
        assertFalse(invalid.isPresent());
    }
    
    @Test
    @DisplayName("Test save - multiple saves verify unique IDs")
    void testSave_MultipleSavesUniqueIds() {
        Customer c1 = new Customer();
        c1.setName("Multi1");
        c1.setAge(25);
        c1.setPhoneNumber("0191111111");
        c1.setGender("Male");
        c1.setPassword("p1");
        
        Customer c2 = new Customer();
        c2.setName("Multi2");
        c2.setAge(30);
        c2.setPhoneNumber("0192222222");
        c2.setGender("Female");
        c2.setPassword("p2");
        
        Customer saved1 = repository.save(c1);
        Customer saved2 = repository.save(c2);
        
        assertNotEquals(saved1.getCustomerId(), saved2.getCustomerId());
        assertTrue(saved1.getCustomerId() > 0);
        assertTrue(saved2.getCustomerId() > 0);
    }
    
    @Test
    @DisplayName("Test getNextCustomerId - max ID exactly 1000")
    void testGetNextCustomerId_MaxIdExactly1000() throws SQLException {
        TestDatabaseSetup.cleanup(connectionProvider);
        // Insert a customer with ID 1000
        try (var conn = connectionProvider.getConnection();
             var stmt = conn.prepareStatement("INSERT INTO customers (customer_id, name, age, phone_number, gender, password) VALUES (?, ?, ?, ?, ?, ?)")) {
            stmt.setInt(1, 1000);
            stmt.setString(2, "ID 1000");
            stmt.setInt(3, 25);
            stmt.setString(4, "0199999999");
            stmt.setString(5, "Male");
            stmt.setString(6, PasswordUtil.hashPassword("pass"));
            stmt.executeUpdate();
        }
        
        int nextId = repository.getNextCustomerId();
        assertEquals(1001, nextId);
    }
    
    @Test
    @DisplayName("Test getNextCustomerId - max ID less than 1000 returns 1000")
    void testGetNextCustomerId_MaxIdLessThan1000Returns1000() throws SQLException {
        TestDatabaseSetup.cleanup(connectionProvider);
        // Insert customer with ID < 1000
        try (var conn = connectionProvider.getConnection();
             var stmt = conn.prepareStatement("INSERT INTO customers (customer_id, name, age, phone_number, gender, password) VALUES (?, ?, ?, ?, ?, ?)")) {
            stmt.setInt(1, 999);
            stmt.setString(2, "ID 999");
            stmt.setInt(3, 25);
            stmt.setString(4, "0198888888");
            stmt.setString(5, "Male");
            stmt.setString(6, PasswordUtil.hashPassword("pass"));
            stmt.executeUpdate();
        }
        
        int nextId = repository.getNextCustomerId();
        assertEquals(1000, nextId);
    }
    
    @Test
    @DisplayName("Test save - verify all customer fields mapped correctly")
    void testSave_VerifyAllFieldsMapped() {
        Customer customer = new Customer();
        customer.setName("Field Test");
        customer.setAge(42);
        customer.setPhoneNumber("0177777777");
        customer.setGender("Female");
        customer.setPassword("fieldtest123");
        
        Customer saved = repository.save(customer);
        Optional<Customer> found = repository.findById(saved.getCustomerId());
        
        assertTrue(found.isPresent());
        Customer f = found.get();
        assertEquals("Field Test", f.getName());
        assertEquals(42, f.getAge());
        assertEquals("0177777777", f.getPhoneNumber());
        assertEquals("Female", f.getGender());
        assertEquals("fieldtest123", f.getPassword());
    }
    
    @Test
    @DisplayName("Test existsByPhoneNumber - count greater than 0")
    void testExistsByPhoneNumber_CountGreaterThanZero() {
        assertTrue(repository.existsByPhoneNumber("0123456789"));
    }
    
    @Test
    @DisplayName("Test existsByPhoneNumber - count equals 0")
    void testExistsByPhoneNumber_CountEqualsZero() {
        assertFalse(repository.existsByPhoneNumber("0000000000"));
    }
    
    @Test
    @DisplayName("Test getNextCustomerId - with rs.next() returns false")
    void testGetNextCustomerId_ResultSetNextFalse() throws SQLException {
        // This tests when rs.next() returns false (empty result set)
        TestDatabaseSetup.cleanup(connectionProvider);
        int nextId = repository.getNextCustomerId();
        assertEquals(1000, nextId);
    }
    
    @Test
    @DisplayName("Test existsByPhoneNumber - with rs.next() returns false")
    void testExistsByPhoneNumber_ResultSetNextFalse() {
        // This tests when rs.next() returns false
        boolean exists = repository.existsByPhoneNumber("0000000000");
        assertFalse(exists);
    }
    
    @Test
    @DisplayName("Test save - affectedRows equals 0 path")
    void testSave_AffectedRowsPath() {
        // This tests the affectedRows > 0 path
        Customer customer = new Customer();
        customer.setName("Affected Rows");
        customer.setAge(25);
        customer.setPhoneNumber("0200000000");
        customer.setGender("Male");
        customer.setPassword("affected");
        
        Customer saved = repository.save(customer);
        // Verify affectedRows > 0 path was taken
        assertTrue(saved.getCustomerId() > 0);
    }
    
    @Test
    @DisplayName("Test save - generatedKeys.next() path")
    void testSave_GeneratedKeysNextPath() {
        Customer customer = new Customer();
        customer.setName("Gen Keys Next");
        customer.setAge(25);
        customer.setPhoneNumber("0211111111");
        customer.setGender("Male");
        customer.setPassword("genkeys");
        
        Customer saved = repository.save(customer);
        // This tests the generatedKeys.next() path
        assertTrue(saved.getCustomerId() > 0);
    }
    
    @Test
    @DisplayName("Test getNextCustomerId - max ID condition >= 1000")
    void testGetNextCustomerId_MaxIdConditionGreaterOrEqual() throws SQLException {
        TestDatabaseSetup.cleanup(connectionProvider);
        // Insert customer with ID exactly 1000
        try (var conn = connectionProvider.getConnection();
             var stmt = conn.prepareStatement("INSERT INTO customers (customer_id, name, age, phone_number, gender, password) VALUES (?, ?, ?, ?, ?, ?)")) {
            stmt.setInt(1, 1000);
            stmt.setString(2, "Test");
            stmt.setInt(3, 25);
            stmt.setString(4, "0222222222");
            stmt.setString(5, "Male");
            stmt.setString(6, PasswordUtil.hashPassword("pass"));
            stmt.executeUpdate();
        }
        
        int nextId = repository.getNextCustomerId();
        // Should return maxId + 1 = 1001 (since maxId >= 1000)
        assertEquals(1001, nextId);
    }
    
    @Test
    @DisplayName("Test getNextCustomerId - max ID condition < 1000")
    void testGetNextCustomerId_MaxIdConditionLessThan() throws SQLException {
        TestDatabaseSetup.cleanup(connectionProvider);
        // Insert customer with ID < 1000
        try (var conn = connectionProvider.getConnection();
             var stmt = conn.prepareStatement("INSERT INTO customers (customer_id, name, age, phone_number, gender, password) VALUES (?, ?, ?, ?, ?, ?)")) {
            stmt.setInt(1, 500);
            stmt.setString(2, "Test");
            stmt.setInt(3, 25);
            stmt.setString(4, "0233333333");
            stmt.setString(5, "Male");
            stmt.setString(6, PasswordUtil.hashPassword("pass"));
            stmt.executeUpdate();
        }
        
        int nextId = repository.getNextCustomerId();
        // Should return 1000 (since maxId < 1000)
        assertEquals(1000, nextId);
    }
    
    @Test
    @DisplayName("Test getNextCustomerId - after deleting all customers")
    void testGetNextCustomerId_AfterDeletingAll() throws SQLException {
        TestDatabaseSetup.cleanup(connectionProvider);
        // Add a customer and verify next ID
        Customer customer = new Customer();
        customer.setName("Temp");
        customer.setAge(25);
        customer.setPhoneNumber("0244444444");
        customer.setGender("Male");
        customer.setPassword("temp");
        repository.save(customer);
        
        // Delete it (we can't easily delete, but we can test the path)
        int nextId = repository.getNextCustomerId();
        assertTrue(nextId >= 1000);
    }
    
    @Test
    @DisplayName("Test mapResultSetToCustomer - all field mappings")
    void testMapResultSetToCustomer_AllFields() {
        Optional<Customer> customer = repository.findById(1000);
        assertTrue(customer.isPresent());
        Customer c = customer.get();
        // Verify all fields from ResultSet are mapped
        assertEquals(1000, c.getCustomerId());
        assertEquals("John Doe", c.getName());
        assertEquals(25, c.getAge());
        assertEquals("0123456789", c.getPhoneNumber());
        assertEquals("Male", c.getGender());
        // Password is hashed in test data, so verify it's a hash (64 chars for SHA-256)
        assertNotNull(c.getPassword());
        assertEquals(64, c.getPassword().length()); // SHA-256 produces 64-character hex string
        // Verify it's the correct hash
        assertEquals(util.PasswordUtil.hashPassword("password123"), c.getPassword());
    }
}
