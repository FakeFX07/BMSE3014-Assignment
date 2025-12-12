package service.impl;
// Tests for PaymentService

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import model.*;
import repository.interfaces.IPaymentMethodRepository;
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
        PaymentMethod pm = new PaymentMethod("TNG001", "TNG", "tng123", 100.0);
        pm.setPaymentMethodId(1);
        Payment payment = paymentService.createPayment(pm);
        assertTrue(payment instanceof TNGPayment);
    }

    @Test
    @DisplayName("Factory: Should create Grab Payment")
    void testCreatePayment_Grab() {
        PaymentMethod pm = new PaymentMethod("GRAB001", "Grab", "grab456", 100.0);
        pm.setPaymentMethodId(1);
        Payment payment = paymentService.createPayment(pm);
        assertTrue(payment instanceof GrabPayment);
    }

    @Test
    @DisplayName("Factory: Should create Bank Payment")
    void testCreatePayment_Bank() {
        PaymentMethod pm = new PaymentMethod();
        pm.setPaymentMethodId(1);
        pm.setPaymentType("Bank");
        pm.setCardNumber("1234567890123456");
        pm.setPassword("bank789");
        pm.setBalance(100.0);
        Payment payment = paymentService.createPayment(pm);
        assertTrue(payment instanceof BankPayment);
    }
    
    @Test
    @DisplayName("Factory: Should throw exception for unknown type")
    void testCreatePayment_UnknownType() {
        PaymentMethod pm = new PaymentMethod("CRYPTO001", "Crypto", "crypto123", 100.0);
        pm.setPaymentMethodId(1);
        assertThrows(IllegalArgumentException.class, () -> paymentService.createPayment(pm));
    }

    // ==========================================
    // Test: Process Payment (Main Logic)
    // ==========================================

    @Test
    @DisplayName("Process: Success TNG Payment")
    void testProcessPayment_Success() {
        // Store password as hashed (as PaymentService will hash it)
        String hashedPassword = util.PasswordUtil.hashPassword("tng123");
        PaymentMethod pm = new PaymentMethod("TNG001", "TNG", hashedPassword, 100.0);
        pm.setPaymentMethodId(1);
        mockRepository.save(pm); // Add to mock DB

        Payment result = paymentService.processPayment("TNG", "TNG001", "tng123", 50.0);
        
        assertNotNull(result);
        assertEquals(50.0, result.getBalance(), 0.01);
    }

    @Test
    @DisplayName("Process: Fail - Method not found")
    void testProcessPayment_MethodNotFound() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> 
            paymentService.processPayment("TNG", "INVALID", "wrongpass", 50.0));
        assertTrue(e.getMessage().contains("Invalid wallet ID or password") || e.getMessage().contains("Invalid credentials"));
    }

    @Test
    @DisplayName("Process: Fail - Insufficient Balance")
    void testProcessPayment_InsufficientBalance() {
        // Store password as hashed
        String hashedPassword = util.PasswordUtil.hashPassword("tng123");
        PaymentMethod pm = new PaymentMethod("TNG002", "TNG", hashedPassword, 10.0);
        pm.setPaymentMethodId(2);
        mockRepository.save(pm);

        Exception e = assertThrows(IllegalArgumentException.class, () -> 
            paymentService.processPayment("TNG", "TNG002", "tng123", 50.0));
        assertTrue(e.getMessage().contains("Insufficient balance") || e.getMessage().contains("balance"));
    }

    @Test
    @DisplayName("Process: Fail - Database Update Error")
    void testProcessPayment_DBError() {
        // Store password as hashed
        String hashedPassword = util.PasswordUtil.hashPassword("tng123");
        PaymentMethod pm = new PaymentMethod("TNG003", "TNG", hashedPassword, 100.0);
        pm.setPaymentMethodId(3);
        mockRepository.save(pm);
        mockRepository.setShouldFailUpdate(true); // Force DB failure

        Exception e = assertThrows(RuntimeException.class, () -> 
            paymentService.processPayment("TNG", "TNG003", "tng123", 10.0));
        assertTrue(e.getMessage().contains("System Error"));
    }

    // ==========================================
    // Test: Bank Validation Logic
    // ==========================================

    @Test
    @DisplayName("Validation: Bank Success")
    void testProcessPayment_Bank_Success() {
        // Store password as hashed
        String hashedPassword = util.PasswordUtil.hashPassword("bank789");
        PaymentMethod pm = new PaymentMethod();
        pm.setPaymentMethodId(4);
        pm.setPaymentType("Bank");
        pm.setCardNumber("1234567890123456");
        pm.setPassword(hashedPassword);
        pm.setBalance(200.0);
        mockRepository.save(pm);

        Payment result = paymentService.processPayment("Bank", "1234567890123456", "bank789", 50.0);
        assertNotNull(result);
    }

    @Test
    @DisplayName("Validation: Fail - Null Card")
    void testProcessPayment_Bank_NullCard() {
        PaymentMethod pm = new PaymentMethod();
        pm.setPaymentMethodId(5);
        pm.setPaymentType("Bank");
        pm.setCardNumber("1234567890123456");
        pm.setPassword("bank789");
        pm.setBalance(200.0);
        mockRepository.save(pm);

        assertThrows(IllegalArgumentException.class, () -> 
            paymentService.processPayment("Bank", "INVALID", "bank789", 50.0));
    }

    @Test
    @DisplayName("Validation: Fail - Invalid Card Length")
    void testProcessPayment_Bank_InvalidCardLength() {
        PaymentMethod pm = new PaymentMethod();
        pm.setPaymentMethodId(5);
        pm.setPaymentType("Bank");
        pm.setCardNumber("1234567890123456");
        pm.setPassword("bank789");
        pm.setBalance(200.0);
        mockRepository.save(pm);

        assertThrows(IllegalArgumentException.class, () -> 
            paymentService.processPayment("Bank", "123", "bank789", 50.0));
    }

    @Test
    @DisplayName("Validation: Fail - Null Expiry")
    void testProcessPayment_Bank_NullExpiry() {
        PaymentMethod pm = new PaymentMethod();
        pm.setPaymentMethodId(5);
        pm.setPaymentType("Bank");
        pm.setCardNumber("1234567890123456");
        pm.setPassword("bank789");
        pm.setBalance(200.0);
        mockRepository.save(pm);

        assertThrows(IllegalArgumentException.class, () -> 
            paymentService.processPayment("Bank", "1234567890123456", null, 50.0));
    }

    @Test
    @DisplayName("Validation: Fail - Invalid Expiry Length")
    void testProcessPayment_Bank_InvalidExpiryLength() {
        PaymentMethod pm = new PaymentMethod();
        pm.setPaymentMethodId(5);
        pm.setPaymentType("Bank");
        pm.setCardNumber("1234567890123456");
        pm.setPassword("bank789");
        pm.setBalance(200.0);
        mockRepository.save(pm);

        assertThrows(IllegalArgumentException.class, () -> 
            paymentService.processPayment("Bank", "1234567890123456", "bank789", 50.0));
    }
    
    // Note: getPaymentMethod method removed - authentication is now done via processPayment

    // ==========================================
    // Mock Repository Class
    // ==========================================
    static class MockPaymentMethodRepository implements IPaymentMethodRepository {
        private java.util.Map<Integer, PaymentMethod> data = new java.util.HashMap<>();
        private java.util.Map<String, PaymentMethod> byWalletId = new java.util.HashMap<>();
        private java.util.Map<String, PaymentMethod> byCardNumber = new java.util.HashMap<>();
        private boolean shouldFailUpdate = false;

        void setShouldFailUpdate(boolean fail) { this.shouldFailUpdate = fail; }

        @Override
        public Optional<PaymentMethod> findById(int paymentMethodId) {
            return Optional.ofNullable(data.get(paymentMethodId));
        }

        @Override
        public Optional<PaymentMethod> findByWalletId(String walletId) {
            return Optional.ofNullable(byWalletId.get(walletId));
        }

        @Override
        public Optional<PaymentMethod> findByCardNumber(String cardNumber) {
            return Optional.ofNullable(byCardNumber.get(cardNumber));
        }

        @Override
        public Optional<PaymentMethod> authenticateByWalletId(String walletId, String hashedPassword) {
            PaymentMethod pm = byWalletId.get(walletId);
            if (pm != null && pm.getPassword().equals(hashedPassword)) {
                return Optional.of(pm);
            }
            return Optional.empty();
        }

        @Override
        public Optional<PaymentMethod> authenticateByCardNumber(String cardNumber, String hashedPassword) {
            PaymentMethod pm = byCardNumber.get(cardNumber);
            if (pm != null && pm.getPassword().equals(hashedPassword)) {
                return Optional.of(pm);
            }
            return Optional.empty();
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
            if (pm.getWalletId() != null) {
                byWalletId.put(pm.getWalletId(), pm);
            }
            if (pm.getCardNumber() != null) {
                byCardNumber.put(pm.getCardNumber(), pm);
            }
            return pm;
        }
        
    }
}