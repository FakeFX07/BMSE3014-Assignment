package test;

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
    
    @Test
    @DisplayName("Test createPayment - TNG")
    void testCreatePayment_TNG() {
        PaymentMethod pm = new PaymentMethod(1000, "TNG", 100.00);
        pm.setPaymentMethodId(1);
        Payment payment = paymentService.createPayment(pm);
        
        assertNotNull(payment);
        assertEquals("TNG", payment.paymentName());
        assertEquals(100.00, payment.getBalance(), 0.01);
    }
    
    @Test
    @DisplayName("Test createPayment - Grab")
    void testCreatePayment_Grab() {
        PaymentMethod pm = new PaymentMethod(1000, "Grab", 100.00);
        pm.setPaymentMethodId(1);
        Payment payment = paymentService.createPayment(pm);
        
        assertNotNull(payment);
        assertEquals("Grab", payment.paymentName());
    }
    
    @Test
    @DisplayName("Test createPayment - Bank")
    void testCreatePayment_Bank() {
        PaymentMethod pm = new PaymentMethod(1, 1000, "Bank", 100.00, "1234567890123456", "1225");
        Payment payment = paymentService.createPayment(pm);
        
        assertNotNull(payment);
        assertEquals("Bank", payment.paymentName());
    }
    
    @Test
    @DisplayName("Test processPayment - sufficient balance")
    void testProcessPayment_SufficientBalance() {
        PaymentMethod pm = new PaymentMethod(1000, "TNG", 100.00);
        pm.setPaymentMethodId(1);
        mockRepository.addPaymentMethod(pm);
        
        Payment payment = paymentService.processPayment(1000, "TNG", 50.00, null, null);
        
        assertNotNull(payment);
        assertEquals(50.00, payment.getBalance(), 0.01);
    }
    
    @Test
    @DisplayName("Test processPayment - insufficient balance")
    void testProcessPayment_InsufficientBalance() {
        PaymentMethod pm = new PaymentMethod(1000, "TNG", 50.00);
        pm.setPaymentMethodId(1);
        mockRepository.addPaymentMethod(pm);
        
        assertThrows(IllegalArgumentException.class, () -> {
            paymentService.processPayment(1000, "TNG", 100.00, null, null);
        });
    }
    
    @Test
    @DisplayName("Test processPayment - Bank with card validation")
    void testProcessPayment_BankWithCard() {
        PaymentMethod pm = new PaymentMethod(1, 1000, "Bank", 100.00, "1234567890123456", "1225");
        mockRepository.addPaymentMethod(pm);
        
        Payment payment = paymentService.processPayment(1000, "Bank", 50.00, "1234567890123456", "1225");
        
        assertNotNull(payment);
    }
    
    @Test
    @DisplayName("Test processPayment - Bank invalid card")
    void testProcessPayment_BankInvalidCard() {
        PaymentMethod pm = new PaymentMethod(1, 1000, "Bank", 100.00, "1234567890123456", "1225");
        mockRepository.addPaymentMethod(pm);
        
        assertThrows(IllegalArgumentException.class, () -> {
            paymentService.processPayment(1000, "Bank", 50.00, "123", "1225");
        });
    }

    @Test
    @DisplayName("Test getPaymentMethod")
    void testGetPaymentMethod() {
        PaymentMethod pm = new PaymentMethod(2000, "Grab", 75.00);
        pm.setPaymentMethodId(5);
        mockRepository.addPaymentMethod(pm);

        assertTrue(paymentService.getPaymentMethod(2000, "Grab").isPresent());
        assertTrue(paymentService.getPaymentMethod(2000, "grab").isPresent());
        assertTrue(paymentService.getPaymentMethod(9999, "Grab").isEmpty());
    }
    
    // Mock repository for testing
    private static class MockPaymentMethodRepository implements IPaymentMethodRepository {
        private java.util.Map<Integer, PaymentMethod> paymentMethods = new java.util.HashMap<>();
        private int nextId = 1;
        
        @Override
        public Optional<PaymentMethod> findById(int paymentMethodId) {
            return Optional.ofNullable(paymentMethods.get(paymentMethodId));
        }
        
        @Override
        public java.util.List<PaymentMethod> findByCustomerId(int customerId) {
            return paymentMethods.values().stream()
                    .filter(pm -> pm.getCustomerId() == customerId)
                    .collect(java.util.stream.Collectors.toList());
        }
        
        @Override
        public Optional<PaymentMethod> findByCustomerIdAndType(int customerId, String paymentType) {
            return paymentMethods.values().stream()
                    .filter(pm -> pm.getCustomerId() == customerId && 
                                 pm.getPaymentType().equalsIgnoreCase(paymentType))
                    .findFirst();
        }
        
        @Override
        public PaymentMethod save(PaymentMethod paymentMethod) {
            if (paymentMethod.getPaymentMethodId() == 0) {
                paymentMethod.setPaymentMethodId(nextId++);
            }
            paymentMethods.put(paymentMethod.getPaymentMethodId(), paymentMethod);
            return paymentMethod;
        }
        
        @Override
        public boolean updateBalance(int paymentMethodId, double newBalance) {
            PaymentMethod pm = paymentMethods.get(paymentMethodId);
            if (pm != null) {
                pm.setBalance(newBalance);
                return true;
            }
            return false;
        }
        
        public void addPaymentMethod(PaymentMethod pm) {
            paymentMethods.put(pm.getPaymentMethodId(), pm);
        }
    }
}
