package presentation.Customer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import controller.CustomerController;
import model.Customer;
import presentation.General.UserCancelledException;
import presentation.General.UserInputHandler;

class CustomerHandlerTest {

    @Mock
    private CustomerController customerController;

    @Mock
    private UserInputHandler inputHandler;

    @InjectMocks
    private CustomerHandler customerHandler;

    @BeforeEach
    void setUp() {
        // Initialize Mocks
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Login - Fail (Wrong Credentials)")
    void testHandleLogin_Failure() throws UserCancelledException {
        // Mock Input
        when(inputHandler.readInt(anyString())).thenReturn(999);
        when(inputHandler.readString(anyString())).thenReturn("wrong");

        // Mock Controller behavior (Return null)
        when(customerController.login(999, "wrong")).thenReturn(null);

        // Execute
        Customer result = customerHandler.handleLogin();

        // Verify
        assertNull(result);
    }

    @Test
    @DisplayName("Login - User Cancelled (Throws UserCancelledException)")
    void testHandleLogin_UserCancel() throws UserCancelledException {
        // Simulate user typing 'X' which throws exception in InputHandler
        when(inputHandler.readInt(anyString())).thenThrow(new UserCancelledException());

        // Execute
        Customer result = customerHandler.handleLogin();

        // Verify exception was caught and null returned
        assertNull(result);
        // Ensure controller login was NEVER called
        verify(customerController, never()).login(anyInt(), anyString());
    }

    // ==========================================
    // 2. Register Scenarios (Complex)
    // ==========================================

    @Test
    @DisplayName("Register - Happy Path (Success)")
    void testHandleRegister_Success() throws UserCancelledException {
        // 1. Mock field inputs (Order matters!)
        when(inputHandler.readString(contains("name"))).thenReturn("John");
        when(inputHandler.readInt(contains("age"))).thenReturn(25);
        when(inputHandler.readString(contains("phone"))).thenReturn("0123456789");
        when(inputHandler.readString(contains("gender"))).thenReturn("Male");
        when(inputHandler.readString(contains("password"))).thenReturn("pass");
        when(inputHandler.readString(contains("Confirm"))).thenReturn("pass");

        // 2. Final confirmation (after displaying details)
        when(inputHandler.readYesNo(contains("sure want to register"))).thenReturn(true);

        // 4. Mock Controller Success Return
        Customer createdCustomer = new Customer(1, "John");
        createdCustomer.setAge(25);
        createdCustomer.setPhoneNumber("0123456789");
        createdCustomer.setGender("Male");
        
        when(customerController.registerCustomer(any(Customer.class))).thenReturn(createdCustomer);

        // Execute
        customerHandler.handleRegister();

        // Verify flow
        verify(customerController).checkName("John");
        verify(customerController).checkAge(25);
        verify(customerController).registerCustomer(any(Customer.class));
    }

    @Test
    @DisplayName("Register - User Declines After Viewing Details")
    void testHandleRegister_UserDeclinesAfterDetails() {
        // User enters all details but declines at final confirmation
        when(inputHandler.readString(contains("name"))).thenReturn("John");
        when(inputHandler.readInt(contains("age"))).thenReturn(25);
        when(inputHandler.readString(contains("phone"))).thenReturn("0123456789");
        when(inputHandler.readString(contains("gender"))).thenReturn("Male");
        when(inputHandler.readString(contains("password"))).thenReturn("pass");
        when(inputHandler.readString(contains("Confirm"))).thenReturn("pass");
        
        // User declines at final confirmation
        when(inputHandler.readYesNo(contains("sure want to register"))).thenReturn(false);

        customerHandler.handleRegister();

        // Verify details were collected but registration was not called
        verify(customerController, never()).registerCustomer(any(Customer.class));
    }

    @Test
    @DisplayName("Register - Validation Retry Loop (Invalid then Valid)")
    void testHandleRegister_ValidationRetry() throws UserCancelledException {

        // --- Simulate Name Loop ---
        // 1st call returns "BadName", 2nd returns "GoodName"
        when(inputHandler.readString(contains("name"))).thenReturn("BadName", "GoodName");
        
        // Controller throws error for "BadName", accepts "GoodName"
        doThrow(new IllegalArgumentException("Invalid Name")).when(customerController).checkName("BadName");
        doNothing().when(customerController).checkName("GoodName");

        // --- Fill other fields normally to finish flow ---
        when(inputHandler.readInt(contains("age"))).thenReturn(20);
        when(inputHandler.readString(contains("phone"))).thenReturn("012");
        when(inputHandler.readString(contains("gender"))).thenReturn("Male");
        when(inputHandler.readString(contains("password"))).thenReturn("p");
        when(inputHandler.readString(contains("Confirm"))).thenReturn("p");
        when(inputHandler.readYesNo(contains("sure"))).thenReturn(false); // Quit at end to save setup

        // Execute
        customerHandler.handleRegister();

        // Verify checkName was called twice (once failed, once succeeded)
        verify(customerController, times(1)).checkName("BadName");
        verify(customerController, times(1)).checkName("GoodName");
    }

    @Test
    @DisplayName("Register - User Cancelled Midway")
    void testHandleRegister_UserCancelMidway() throws UserCancelledException {
        when(inputHandler.readYesNo(contains("confirm"))).thenReturn(true);

        // Name is fine
        when(inputHandler.readString(contains("name"))).thenReturn("John");
        
        // Age throws Cancelled Exception (User types X)
        when(inputHandler.readInt(contains("age"))).thenThrow(new UserCancelledException());

        // Execute
        customerHandler.handleRegister();

        // Verify flow stopped
        verify(customerController).checkName("John"); // Name was checked
        verify(customerController, never()).checkAge(anyInt()); // Age check never reached controller
        verify(customerController, never()).registerCustomer(any()); // Registration never happened
    }
}