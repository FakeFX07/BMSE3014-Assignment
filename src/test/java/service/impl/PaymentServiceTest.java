package service.impl;
// Tests for PaymentService

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import model.*;
import repository.interfaces.IPaymentMethodRepository;
import service.impl.PaymentService;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Optional;

/**
 * Payment Service Test
 * Tests payment service business logic
 * Follows TDD principles
 */
public class PaymentServiceTest {
    
    private PaymentService paymentService;
    private MockPaymentMethodRepository mockRepository;
    
    @BeforeEach
    void setUp() {
        mockRepository = new MockPaymentMethodRepository();
        paymentService = new PaymentService(mockRepository);
    }
    
    // ==========================================
    // Test: Create Payment (Factory Logic)
    // ==========================================
    
    @Test
    @DisplayName("Factory: Should create TNG Payment")
    void testCreatePayment_TNG() {
        PaymentMethod pm = new PaymentMethod(1, "TNG", 100.0);
        Payment payment = paymentService.createPayment(pm);
        assertTrue(payment instanceof TNGPayment);
    }

    @Test
    @DisplayName("Factory: Should create Grab Payment")
    void testCreatePayment_Grab() {
        PaymentMethod pm = new PaymentMethod(1, "Grab", 100.0);
        Payment payment = paymentService.createPayment(pm);
        assertTrue(payment instanceof GrabPayment);
    }

    @Test
    @DisplayName("Factory: Should create Bank Payment")
    void testCreatePayment_Bank() {
        PaymentMethod pm = new PaymentMethod(1, "Bank", 100.0);
        Payment payment = paymentService.createPayment(pm);
        assertTrue(payment instanceof BankPayment);
    }
    
    @Test
    @DisplayName("Factory: Should throw exception for unknown type")
    void testCreatePayment_UnknownType() {
        PaymentMethod pm = new PaymentMethod(1, "Crypto", 100.0);
        assertThrows(IllegalArgumentException.class, () -> paymentService.createPayment(pm));
    }

    // ==========================================
    // Test: Process Payment (Main Logic)
    // ==========================================

    @Test
    @DisplayName("Process: Success TNG Payment")
    void testProcessPayment_Success() {
        PaymentMethod pm = new PaymentMethod(1, 1001, "TNG", 100.0, null, null);
        mockRepository.save(pm); // Add to mock DB

        Payment result = paymentService.processPayment(1001, "TNG", 50.0, null, null);
        
        assertNotNull(result);
        assertEquals(50.0, result.getBalance(), 0.01);
    }

    @Test
    @DisplayName("Process: Fail - Method not found")
    void testProcessPayment_MethodNotFound() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> 
            paymentService.processPayment(9999, "TNG", 50.0, null, null));
        assertEquals("Payment method not found for customer", e.getMessage());
    }

    @Test
    @DisplayName("Process: Fail - Insufficient Balance")
    void testProcessPayment_InsufficientBalance() {
        PaymentMethod pm = new PaymentMethod(2, 1002, "TNG", 10.0, null, null);
        mockRepository.save(pm);

        Exception e = assertThrows(IllegalArgumentException.class, () -> 
            paymentService.processPayment(1002, "TNG", 50.0, null, null));
        assertEquals("Insufficient balance", e.getMessage());
    }

    @Test
    @DisplayName("Process: Fail - Database Update Error")
    void testProcessPayment_DBError() {
        PaymentMethod pm = new PaymentMethod(3, 1003, "TNG", 100.0, null, null);
        mockRepository.save(pm);
        mockRepository.setShouldFailUpdate(true); // Force DB failure

        Exception e = assertThrows(RuntimeException.class, () -> 
            paymentService.processPayment(1003, "TNG", 10.0, null, null));
        assertTrue(e.getMessage().contains("System Error"));
    }

    // ==========================================
    // Test: Bank Validation Logic
    // ==========================================

    @Test
    @DisplayName("Validation: Bank Success")
    void testProcessPayment_Bank_Success() {
        PaymentMethod pm = new PaymentMethod(4, 1004, "Bank", 200.0, "1234567890123456", "1225");
        mockRepository.save(pm);

        Payment result = paymentService.processPayment(1004, "Bank", 50.0, "1234567890123456", "1225");
        assertNotNull(result);
    }

    @Test
    @DisplayName("Validation: Fail - Null Card")
    void testProcessPayment_Bank_NullCard() {
        PaymentMethod pm = new PaymentMethod(5, 1005, "Bank", 200.0, null, null);
        mockRepository.save(pm);

        assertThrows(IllegalArgumentException.class, () -> 
            paymentService.processPayment(1005, "Bank", 50.0, null, "1225"));
    }

    @Test
    @DisplayName("Validation: Fail - Invalid Card Length")
    void testProcessPayment_Bank_InvalidCardLength() {
        PaymentMethod pm = new PaymentMethod(5, 1005, "Bank", 200.0, null, null);
        mockRepository.save(pm);

        assertThrows(IllegalArgumentException.class, () -> 
            paymentService.processPayment(1005, "Bank", 50.0, "123", "1225"));
    }

    @Test
    @DisplayName("Validation: Fail - Null Expiry")
    void testProcessPayment_Bank_NullExpiry() {
        PaymentMethod pm = new PaymentMethod(5, 1005, "Bank", 200.0, null, null);
        mockRepository.save(pm);

        assertThrows(IllegalArgumentException.class, () -> 
            paymentService.processPayment(1005, "Bank", 50.0, "1234567890123456", null));
    }

    @Test
    @DisplayName("Validation: Fail - Invalid Expiry Length")
    void testProcessPayment_Bank_InvalidExpiryLength() {
        PaymentMethod pm = new PaymentMethod(5, 1005, "Bank", 200.0, null, null);
        mockRepository.save(pm);

        assertThrows(IllegalArgumentException.class, () -> 
            paymentService.processPayment(1005, "Bank", 50.0, "1234567890123456", "1"));
    }
    
    @Test
    @DisplayName("Getter: Get Payment Method")
    void testGetPaymentMethod() {
        PaymentMethod pm = new PaymentMethod(6, 1006, "TNG", 100.0, null, null);
        mockRepository.save(pm);
        
        assertTrue(paymentService.getPaymentMethod(1006, "TNG").isPresent());
        assertFalse(paymentService.getPaymentMethod(9999, "TNG").isPresent());
    }

    // ==========================================
    // Mock Repository Class
    // ==========================================
    static class MockPaymentMethodRepository implements IPaymentMethodRepository {
        private java.util.Map<Integer, PaymentMethod> data = new java.util.HashMap<>();
        private boolean shouldFailUpdate = false;

        void setShouldFailUpdate(boolean fail) { this.shouldFailUpdate = fail; }

        @Override
        public Optional<PaymentMethod> findByCustomerIdAndType(int customerId, String paymentType) {
            return data.values().stream()
                    .filter(p -> p.getCustomerId() == customerId && p.getPaymentType().equalsIgnoreCase(paymentType))
                    .findFirst();
        }

        @Override
        public boolean updateBalance(int id, double newBalance) {
            if (shouldFailUpdate) return false;
            if (data.containsKey(id)) {
                data.get(id).setBalance(newBalance);
                return true;
            }
            return false;
        }

        @Override
        public PaymentMethod save(PaymentMethod pm) {
            data.put(pm.getPaymentMethodId(), pm);
            return pm;
        }
        
        // Unused methods for this test context but required by interface
        @Override public Optional<PaymentMethod> findById(int id) { return Optional.empty(); }
        @Override public java.util.List<PaymentMethod> findByCustomerId(int id) { return null; }
    }
}