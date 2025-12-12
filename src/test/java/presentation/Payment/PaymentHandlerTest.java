package presentation.Payment;

import controller.PaymentController;
import model.Payment;
import model.TNGPayment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import presentation.General.UserInputHandler;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * PaymentHandlerTest
 * Tests the UI logic for Payment handling.
 * Achieves 100% coverage by mocking InputHandler and PaymentController.
 */
class PaymentHandlerTest {

    @Mock
    private PaymentController paymentController;

    @Mock
    private UserInputHandler inputHandler;

    private PaymentHandler paymentHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        paymentHandler = new PaymentHandler(paymentController, inputHandler);
    }

    @Test
    @DisplayName("Test: Invalid Payment Selection (Code 99)")
    void testHandlePayment_InvalidSelection() {
        // Mock user selecting invalid option
        when(inputHandler.readInt(anyString())).thenReturn(99);

        paymentHandler.handlePayment(1, 50.0);

        // Verify we asked for input but never proceeded to payment
        verify(inputHandler).readInt(anyString());
        verify(paymentController, never()).processPayment(anyInt(), anyString(), anyDouble(), any(), any());
    }

    @Test
    @DisplayName("Test: User Cancelled Confirmation (TNG)")
    void testHandlePayment_UserCancelled() {
        // Mock TNG Selection (1) but User says "No" (false) to confirmation
        when(inputHandler.readInt(anyString())).thenReturn(1); // TNG
        when(inputHandler.readYesNo(anyString())).thenReturn(false);

        paymentHandler.handlePayment(1, 50.0);

        verify(paymentController, never()).processPayment(anyInt(), anyString(), anyDouble(), any(), any());
    }

    @Test
    @DisplayName("Test: Successful Payment (TNG)")
    void testHandlePayment_Success_TNG() {
        // Mock TNG Selection (1) and User says "Yes"
        when(inputHandler.readInt(anyString())).thenReturn(1); // TNG
        when(inputHandler.readYesNo(anyString())).thenReturn(true);
        
        // Mock Controller success
        Payment mockPayment = new TNGPayment(40.0); // Remaining balance
        when(paymentController.processPayment(eq(1), eq("TNG"), eq(10.0), isNull(), isNull()))
                .thenReturn(mockPayment);

        paymentHandler.handlePayment(1, 10.0);

        // Verify controller was called correctly
        verify(paymentController).processPayment(1, "TNG", 10.0, null, null);
    }

    @Test
    @DisplayName("Test: Successful Payment (Grab)")
    void testHandlePayment_Success_Grab() {
        // Mock Grab Selection (2)
        when(inputHandler.readInt(anyString())).thenReturn(2); // Grab
        when(inputHandler.readYesNo(anyString())).thenReturn(true);
        
        Payment mockPayment = new TNGPayment(100.0); // Just using any payment object
        when(paymentController.processPayment(anyInt(), eq("Grab"), anyDouble(), any(), any()))
                .thenReturn(mockPayment);

        paymentHandler.handlePayment(1, 20.0);
        
        verify(paymentController).processPayment(1, "Grab", 20.0, null, null);
    }

    @Test
    @DisplayName("Test: Successful Payment (Bank with Card Details)")
    void testHandlePayment_Success_Bank() {
        // Mock Bank Selection (3) -> Needs Card Input
        when(inputHandler.readInt(anyString())).thenReturn(3); // Bank
        when(inputHandler.readString(contains("Card Number"))).thenReturn("1234567890123456");
        when(inputHandler.readString(contains("Expiry"))).thenReturn("1225");
        when(inputHandler.readYesNo(anyString())).thenReturn(true);
        
        Payment mockPayment = new TNGPayment(200.0);
        when(paymentController.processPayment(anyInt(), eq("Bank"), anyDouble(), anyString(), anyString()))
                .thenReturn(mockPayment);

        paymentHandler.handlePayment(1, 50.0);
        
        // Verify card details were passed to controller
        verify(paymentController).processPayment(1, "Bank", 50.0, "1234567890123456", "1225");
    }

    @Test
    @DisplayName("Test: Payment Failed (Controller returns null)")
    void testHandlePayment_Failure() {
        // Mock TNG Selection and Confirmation
        when(inputHandler.readInt(anyString())).thenReturn(1);
        when(inputHandler.readYesNo(anyString())).thenReturn(true);

        // Mock Controller returning null (failure)
        when(paymentController.processPayment(anyInt(), anyString(), anyDouble(), any(), any()))
                .thenReturn(null);

        paymentHandler.handlePayment(1, 10.0);

        // Verify method was called but resulted in failure (no exception thrown)
        verify(paymentController).processPayment(1, "TNG", 10.0, null, null);
    }
}