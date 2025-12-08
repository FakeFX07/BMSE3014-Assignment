package repository.impl;

import config.ConnectionProvider;
import config.DatabaseConnection;
import config.TestDatabaseSetup;
import model.PaymentMethod;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Payment Method Repository Test
 */
public class PaymentMethodRepositoryTest {
    
    private PaymentMethodRepository repository;
    private ConnectionProvider connectionProvider;
    private static final String H2_URL = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=MySQL";
    
    @BeforeEach
    void setUp() throws SQLException {
        connectionProvider = DatabaseConnection.createInstance(H2_URL, "sa", "");
        TestDatabaseSetup.initializeSchema(connectionProvider);
        repository = new PaymentMethodRepository(connectionProvider);
    }
    
    @AfterEach
    void tearDown() throws SQLException {
        TestDatabaseSetup.cleanup(connectionProvider);
        if (connectionProvider instanceof DatabaseConnection) {
            ((DatabaseConnection) connectionProvider).closeConnection();
        }
    }
    
    @Test
    @DisplayName("Test findById - existing")
    void testFindById_Existing() {
        Optional<PaymentMethod> pm = repository.findById(1);
        assertTrue(pm.isPresent());
        assertEquals("TNG", pm.get().getPaymentType());
    }
    
    @Test
    @DisplayName("Test findByCustomerId - returns all methods")
    void testFindByCustomerId() {
        List<PaymentMethod> methods = repository.findByCustomerId(1000);
        assertTrue(methods.size() >= 3);
    }
    
    @Test
    @DisplayName("Test findByCustomerIdAndType - existing")
    void testFindByCustomerIdAndType_Existing() {
        Optional<PaymentMethod> pm = repository.findByCustomerIdAndType(1000, "TNG");
        assertTrue(pm.isPresent());
        assertEquals("TNG", pm.get().getPaymentType());
    }
    
    @Test
    @DisplayName("Test save - new payment method")
    void testSave_NewPaymentMethod() {
        PaymentMethod pm = new PaymentMethod(1001, "Grab", 60.00);
        PaymentMethod saved = repository.save(pm);
        assertTrue(saved.getPaymentMethodId() > 0);
    }
    
    @Test
    @DisplayName("Test updateBalance - existing")
    void testUpdateBalance_Existing() {
        boolean updated = repository.updateBalance(1, 150.00);
        assertTrue(updated);
        
        Optional<PaymentMethod> pm = repository.findById(1);
        assertTrue(pm.isPresent());
        assertEquals(150.00, pm.get().getBalance(), 0.01);
    }
    
    @Test
    @DisplayName("Test default constructor")
    void testDefaultConstructor() {
        PaymentMethodRepository repo = new PaymentMethodRepository();
        assertNotNull(repo);
    }
    
    @Test
    @DisplayName("Test save - Bank payment with card details")
    void testSave_BankPayment() {
        PaymentMethod pm = new PaymentMethod(1, 1000, "Bank", 200.00, "1234567890123456", "1225");
        PaymentMethod saved = repository.save(pm);
        assertTrue(saved.getPaymentMethodId() > 0);
        assertEquals("Bank", saved.getPaymentType());
        assertEquals("1234567890123456", saved.getCardNumber());
    }
    
    @Test
    @DisplayName("Test findByCustomerId - empty result")
    void testFindByCustomerId_Empty() {
        List<PaymentMethod> methods = repository.findByCustomerId(9999);
        assertTrue(methods.isEmpty());
    }
    
    @Test
    @DisplayName("Test updateBalance - non-existing payment method")
    void testUpdateBalance_NonExisting() {
        boolean updated = repository.updateBalance(9999, 100.00);
        assertFalse(updated);
    }
    
    @Test
    @DisplayName("Test save - payment method with generated ID")
    void testSave_WithGeneratedId() {
        PaymentMethod pm = new PaymentMethod(1001, "Grab", 60.00);
        PaymentMethod saved = repository.save(pm);
        assertTrue(saved.getPaymentMethodId() > 0);
        
        Optional<PaymentMethod> found = repository.findById(saved.getPaymentMethodId());
        assertTrue(found.isPresent());
        assertEquals("Grab", found.get().getPaymentType());
    }
    
    @Test
    @DisplayName("Test findByCustomerId - returns empty for non-existing customer")
    void testFindByCustomerId_NonExisting() {
        List<PaymentMethod> methods = repository.findByCustomerId(9999);
        assertTrue(methods.isEmpty());
    }
    
    @Test
    @DisplayName("Test findByCustomerIdAndType - non-existing")
    void testFindByCustomerIdAndType_NonExisting() {
        Optional<PaymentMethod> pm = repository.findByCustomerIdAndType(9999, "TNG");
        assertFalse(pm.isPresent());
    }
    
    @Test
    @DisplayName("Test findByCustomerId - verify all payment types returned")
    void testFindByCustomerId_AllTypes() {
        List<PaymentMethod> methods = repository.findByCustomerId(1000);
        assertTrue(methods.size() >= 3);
        
        // Verify different payment types
        long tngCount = methods.stream().filter(pm -> "TNG".equals(pm.getPaymentType())).count();
        long grabCount = methods.stream().filter(pm -> "Grab".equals(pm.getPaymentType())).count();
        long bankCount = methods.stream().filter(pm -> "Bank".equals(pm.getPaymentType())).count();
        
        assertTrue(tngCount > 0);
        assertTrue(grabCount > 0);
        assertTrue(bankCount > 0);
    }
    
    @Test
    @DisplayName("Test updateBalance - multiple updates")
    void testUpdateBalance_MultipleUpdates() {
        boolean updated1 = repository.updateBalance(1, 120.00);
        assertTrue(updated1);
        
        Optional<PaymentMethod> pm1 = repository.findById(1);
        assertTrue(pm1.isPresent());
        assertEquals(120.00, pm1.get().getBalance(), 0.01);
        
        boolean updated2 = repository.updateBalance(1, 150.00);
        assertTrue(updated2);
        
        Optional<PaymentMethod> pm2 = repository.findById(1);
        assertTrue(pm2.isPresent());
        assertEquals(150.00, pm2.get().getBalance(), 0.01);
    }
    
    @Test
    @DisplayName("Test save - verify card details saved for Bank payment")
    void testSave_BankWithCardDetails() {
        PaymentMethod pm = new PaymentMethod(1000, "Bank", 200.00);
        pm.setCardNumber("9876543210987654");
        pm.setExpiryDate("1230");
        
        PaymentMethod saved = repository.save(pm);
        Optional<PaymentMethod> found = repository.findById(saved.getPaymentMethodId());
        
        assertTrue(found.isPresent());
        assertEquals("Bank", found.get().getPaymentType());
        assertEquals("9876543210987654", found.get().getCardNumber());
        assertEquals("1230", found.get().getExpiryDate());
    }
    
    @Test
    @DisplayName("Test findById - verify all fields mapped")
    void testFindById_VerifyAllFields() {
        Optional<PaymentMethod> pm = repository.findById(3);
        assertTrue(pm.isPresent());
        PaymentMethod p = pm.get();
        assertEquals(3, p.getPaymentMethodId());
        assertEquals(1000, p.getCustomerId());
        assertEquals("Bank", p.getPaymentType());
        assertTrue(p.getBalance() > 0);
        assertNotNull(p.getCardNumber());
        assertNotNull(p.getExpiryDate());
    }
    
    @Test
    @DisplayName("Test findByCustomerId - verify while loop processes all")
    void testFindByCustomerId_WhileLoopProcessesAll() {
        List<PaymentMethod> methods = repository.findByCustomerId(1000);
        // Verify all methods are returned
        assertTrue(methods.size() >= 3);
        // Verify all have correct customer ID
        for (PaymentMethod pm : methods) {
            assertEquals(1000, pm.getCustomerId());
        }
    }
    
    @Test
    @DisplayName("Test save - verify generated keys path")
    void testSave_GeneratedKeysPath() {
        PaymentMethod pm = new PaymentMethod(1001, "TNG", 50.00);
        PaymentMethod saved = repository.save(pm);
        // Verify ID was generated
        assertTrue(saved.getPaymentMethodId() > 0);
    }
    
    @Test
    @DisplayName("Test updateBalance - executeUpdate returns 0")
    void testUpdateBalance_ExecuteUpdateReturnsZero() {
        // Update non-existent payment method
        boolean updated = repository.updateBalance(99999, 100.00);
        assertFalse(updated);
    }
    
    @Test
    @DisplayName("Test updateBalance - executeUpdate returns > 0")
    void testUpdateBalance_ExecuteUpdateReturnsPositive() {
        // Update existing payment method
        boolean updated = repository.updateBalance(1, 125.00);
        assertTrue(updated);
    }
    
    @Test
    @DisplayName("Test mapResultSetToPaymentMethod - all fields")
    void testMapResultSetToPaymentMethod_AllFields() {
        Optional<PaymentMethod> pm = repository.findById(3);
        assertTrue(pm.isPresent());
        PaymentMethod p = pm.get();
        // Verify all fields from ResultSet are mapped
        assertTrue(p.getPaymentMethodId() > 0);
        assertTrue(p.getCustomerId() > 0);
        assertNotNull(p.getPaymentType());
        assertTrue(p.getBalance() > 0);
    }
}