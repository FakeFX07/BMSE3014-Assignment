package presentation.Admin;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import controller.AdminController;
import controller.OrderController;
import presentation.Food.FoodHandler;
import presentation.Order.OrderHandler;
import presentation.General.UserInputHandler;
import model.Customer;

import java.util.ArrayList;

class AdminHandlerTest {

    @Mock
    private AdminController adminController;
    @Mock
    private FoodHandler foodHandler;
    @Mock
    private OrderController orderController;
    @Mock
    private UserInputHandler inputHandler;
    @Mock
    private OrderHandler orderHandler; 
    @Mock
    private Customer currentCustomer;

    @InjectMocks
    private AdminHandler adminHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testHandleAdminMenu_LoginFail() {
        // input
        when(inputHandler.readString(contains("Username"))).thenReturn("wrongUser");
        when(inputHandler.readPassword(contains("Password"))).thenReturn("wrongPass");
        
        //controller rejecting login
        when(adminController.login("wrongUser", "wrongPass")).thenReturn(false);

        //Execute
        adminHandler.handleAdminMenu(orderHandler, currentCustomer);

        //Verify
        verify(inputHandler, never()).readInt(contains("Enter your choice"));
    }

    @Test
    void testHandleAdminMenu_LoginSuccess_ImmediateExit() {
        //login success
        when(inputHandler.readString(anyString())).thenReturn("admin");
        when(inputHandler.readPassword(anyString())).thenReturn("admin123");
        when(adminController.login("admin", "admin123")).thenReturn(true);

        when(inputHandler.readInt(anyString())).thenReturn(0); 

        //Execute
        adminHandler.handleAdminMenu(orderHandler, currentCustomer);

        //Verify login was checked and loop exited
        verify(adminController).login("admin", "admin123");
    }

    @Test
    void testHandleAdminMenu_OrderReport() {
        // Mock login success
        when(inputHandler.readString(anyString())).thenReturn("admin");
        when(inputHandler.readPassword(anyString())).thenReturn("pass");
        when(adminController.login(anyString(), anyString())).thenReturn(true);

        when(inputHandler.readInt(anyString())).thenReturn(2, 0);
        
        when(orderController.getAllOrders()).thenReturn(new ArrayList<>());

        // Execute
        adminHandler.handleAdminMenu(orderHandler, currentCustomer);

        //Verify report was requested
        verify(orderController).getAllOrders();
    }

    @Test
    void testHandleAdminMenu_FoodManagement_AllOptions() {
        
        //login success
        when(inputHandler.readString(anyString())).thenReturn("admin");
        when(inputHandler.readPassword(anyString())).thenReturn("pass");
        when(adminController.login(anyString(), anyString())).thenReturn(true);

        when(inputHandler.readInt(anyString())).thenReturn(1, 1, 2, 3, 4, 99, 0, 0);

        // Execute
        adminHandler.handleAdminMenu(orderHandler, currentCustomer);

        //Verify all food handler methods were called
        verify(foodHandler).handleRegisterFood();
        verify(foodHandler).handleEditFood();
        verify(foodHandler).handleDeleteFood();
        verify(foodHandler).handleDisplayAllFoods();
    }

    @Test
    void testHandleAdminMenu_InvalidOption() {
        // Mock login success
        when(inputHandler.readString(anyString())).thenReturn("admin");
        when(inputHandler.readPassword(anyString())).thenReturn("pass");
        when(adminController.login(anyString(), anyString())).thenReturn(true);

        when(inputHandler.readInt(anyString())).thenReturn(99, 0);

        //Execute
        adminHandler.handleAdminMenu(orderHandler, currentCustomer);

        // Verify
        verify(inputHandler, times(2)).readInt(anyString());
    }
}