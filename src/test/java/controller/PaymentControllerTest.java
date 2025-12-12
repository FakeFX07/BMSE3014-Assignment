package controller;

import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import service.interfaces.IPaymentService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Payment Controller Test - Updated for new payment authentication system
 */
public class PaymentControllerTest {
    
    private PaymentController controller;
    private IPaymentService mockService;
    
    @BeforeEach
    void setUp() {
        mockService = mock(IPaymentService.class);
        controller = new PaymentController(mockService);
    }
    
    @Test
    @DisplayName("Test processPayment - TNG success")
    void testProcessPayment_TNG_Success() {
        Payment payment = new TNGPayment(100.00);
        when(mockService.processPayment("TNG", "TNG001", "tng123", 50.00)).thenReturn(payment);
        
        Payment result = controller.processPayment("TNG", "TNG001", "tng123", 50.00);
        assertNotNull(result);
        assertEquals("TNG", result.paymentName());
    }
    
    @Test
    @DisplayName("Test processPayment - Grab success")
    void testProcessPayment_Grab_Success() {
        Payment payment = new GrabPayment(75.00);
        when(mockService.processPayment("Grab", "GRAB001", "grab456", 30.00)).thenReturn(payment);
        
        Payment result = controller.processPayment("Grab", "GRAB001", "grab456", 30.00);
        assertNotNull(result);
        assertEquals("Grab", result.paymentName());
    }
    
    @Test
    @DisplayName("Test processPayment - Bank success")
    void testProcessPayment_Bank_Success() {
        Payment payment = new BankPayment(200.00);
        when(mockService.processPayment("Bank", "1234567890123456", "bank789", 100.00))
            .thenReturn(payment);
        
        Payment result = controller.processPayment("Bank", "1234567890123456", "bank789", 100.00);
        assertNotNull(result);
        assertEquals("Bank", result.paymentName());
    }
    
    @Test
    @DisplayName("Test processPayment - insufficient balance")
    void testProcessPayment_InsufficientBalance() {
        when(mockService.processPayment("TNG", "TNG001", "tng123", 200.00))
            .thenThrow(new IllegalArgumentException("Insufficient balance"));
        
        Payment result = controller.processPayment("TNG", "TNG001", "tng123", 200.00);
        assertNull(result);
    }
    
    @Test
    @DisplayName("Test processPayment - invalid credentials")
    void testProcessPayment_InvalidCredentials() {
        when(mockService.processPayment("TNG", "TNG001", "wrongpassword", 50.00))
            .thenThrow(new IllegalArgumentException("Invalid wallet ID or password"));
        
        Payment result = controller.processPayment("TNG", "TNG001", "wrongpassword", 50.00);
        assertNull(result);
    }
}

