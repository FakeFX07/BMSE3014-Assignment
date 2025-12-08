package controller;

import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import service.interfaces.IPaymentService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Payment Controller Test
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
    @DisplayName("Test processPayment - success")
    void testProcessPayment_Success() {
        Payment payment = new TNGPayment(100.00);
        when(mockService.processPayment(1000, "TNG", 50.00, null, null)).thenReturn(payment);
        
        Payment result = controller.processPayment(1000, "TNG", 50.00, null, null);
        assertNotNull(result);
        assertEquals("TNG", result.paymentName());
    }
    
    @Test
    @DisplayName("Test processPayment - failure")
    void testProcessPayment_Failure() {
        when(mockService.processPayment(1000, "TNG", 200.00, null, null))
            .thenThrow(new IllegalArgumentException("Insufficient balance"));
        
        Payment result = controller.processPayment(1000, "TNG", 200.00, null, null);
        assertNull(result);
    }
    
    @Test
    @DisplayName("Test getPaymentMethod - existing")
    void testGetPaymentMethod_Existing() {
        PaymentMethod pm = new PaymentMethod(1, 1000, "TNG", 100.00, null, null);
        when(mockService.getPaymentMethod(1000, "TNG")).thenReturn(Optional.of(pm));
        
        PaymentMethod result = controller.getPaymentMethod(1000, "TNG");
        assertNotNull(result);
        assertEquals("TNG", result.getPaymentType());
    }
    
    @Test
    @DisplayName("Test getPaymentMethod - not found")
    void testGetPaymentMethod_NotFound() {
        when(mockService.getPaymentMethod(9999, "TNG")).thenReturn(Optional.empty());
        assertNull(controller.getPaymentMethod(9999, "TNG"));
    }
}

