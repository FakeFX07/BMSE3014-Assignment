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

        paymentHandler.handlePayment(50.0);

        // Verify we asked for input but never proceeded to payment
        verify(inputHandler).readInt(anyString());
        verify(paymentController, never()).processPayment(anyString(), anyString(), anyString(), anyDouble());
    }

    @Test
    @DisplayName("Test: User Cancelled Confirmation (TNG)")
    void testHandlePayment_UserCancelled() {
        // Mock TNG Selection (1) with wallet ID and password but User says "No" (false) to confirmation
        when(inputHandler.readInt(anyString())).thenReturn(1); // TNG
        when(inputHandler.readString(anyString())).thenReturn("TNG001");
        when(inputHandler.readPassword(anyString())).thenReturn("tng123");
        when(inputHandler.readYesNo(anyString())).thenReturn(false);

        paymentHandler.handlePayment(50.0);

        verify(paymentController, never()).processPayment(anyString(), anyString(), anyString(), anyDouble());
    }

    @Test
    @DisplayName("Test: Successful Payment (TNG)")
    void testHandlePayment_Success_TNG() {
        // Mock TNG Selection (1) and User says "Yes"
        when(inputHandler.readInt(anyString())).thenReturn(1); // TNG
        when(inputHandler.readString(contains("Wallet"))).thenReturn("TNG001");
        when(inputHandler.readPassword(anyString())).thenReturn("tng123");
        when(inputHandler.readYesNo(anyString())).thenReturn(true);
        
        // Mock Controller success
        Payment mockPayment = new TNGPayment(40.0); // Remaining balance
        when(paymentController.processPayment(eq("TNG"), eq("TNG001"), eq("tng123"), eq(10.0)))
                .thenReturn(mockPayment);

        paymentHandler.handlePayment(10.0);

        // Verify controller was called correctly
        verify(paymentController).processPayment("TNG", "TNG001", "tng123", 10.0);
    }

    @Test
    @DisplayName("Test: Successful Payment (Grab)")
    void testHandlePayment_Success_Grab() {
        // Mock Grab Selection (2)
        when(inputHandler.readInt(anyString())).thenReturn(2); // Grab
        when(inputHandler.readString(contains("Wallet"))).thenReturn("GRAB001");
        when(inputHandler.readPassword(anyString())).thenReturn("grab456");
        when(inputHandler.readYesNo(anyString())).thenReturn(true);
        
        Payment mockPayment = new TNGPayment(100.0); // Just using any payment object
        when(paymentController.processPayment(eq("Grab"), eq("GRAB001"), eq("grab456"), eq(20.0)))
                .thenReturn(mockPayment);

        paymentHandler.handlePayment(20.0);
        
        verify(paymentController).processPayment("Grab", "GRAB001", "grab456", 20.0);
    }

    @Test
    @DisplayName("Test: Successful Payment (Bank with Card Number and Password)")
    void testHandlePayment_Success_Bank() {
        // Mock Bank Selection (3) -> Needs Card Number and Password
        when(inputHandler.readInt(anyString())).thenReturn(3); // Bank
        when(inputHandler.readString(contains("Card Number"))).thenReturn("1234567890123456");
        when(inputHandler.readPassword(anyString())).thenReturn("bank789");
        when(inputHandler.readYesNo(anyString())).thenReturn(true);
        
        Payment mockPayment = new TNGPayment(200.0);
        when(paymentController.processPayment(eq("Bank"), eq("1234567890123456"), eq("bank789"), eq(50.0)))
                .thenReturn(mockPayment);

        paymentHandler.handlePayment(50.0);
        
        // Verify card number and password were passed to controller
        verify(paymentController).processPayment("Bank", "1234567890123456", "bank789", 50.0);
    }

    @Test
    @DisplayName("Test: Payment Failed (Controller returns null)")
    void testHandlePayment_Failure() {
        // Mock TNG Selection and Confirmation
        when(inputHandler.readInt(anyString())).thenReturn(1);
        when(inputHandler.readString(anyString())).thenReturn("TNG001");
        when(inputHandler.readPassword(anyString())).thenReturn("tng123");
        when(inputHandler.readYesNo(anyString())).thenReturn(true);

        // Mock Controller returning null (failure)
        when(paymentController.processPayment(anyString(), anyString(), anyString(), anyDouble()))
                .thenReturn(null);

        paymentHandler.handlePayment(10.0);

        // Verify method was called but resulted in failure (no exception thrown)
        verify(paymentController).processPayment("TNG", "TNG001", "tng123", 10.0);
    }
}