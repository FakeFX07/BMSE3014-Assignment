package presentation.Admin;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import controller.AdminController;
import controller.OrderController;
import presentation.Food.FoodHandler;
import presentation.Order.OrderHandler;
import presentation.General.UserInputHandler;

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
    private OrderHandler orderHandler; // Dependency for method arg

    private AdminHandler adminHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        adminHandler = new AdminHandler(adminController, foodHandler, orderController, inputHandler);
    }

    // ==========================================
    // 1. Login Logic Tests
    // ==========================================

    @Test
    @DisplayName("Admin Menu - Access Denied (Login Fail)")
    void testHandleAdminMenu_LoginFail() {
        // 1. Mock Login Inputs
        when(inputHandler.readString(contains("Username"))).thenReturn("wrongUser");
        when(inputHandler.readPassword(contains("Password"))).thenReturn("wrongPass");

        // 2. Mock Controller to return false
        when(adminController.login("wrongUser", "wrongPass")).thenReturn(false);

        // 3. Execute
        adminHandler.handleAdminMenu(orderHandler, null);

        // 4. Verify controller called
        verify(adminController).login("wrongUser", "wrongPass");
        // 5. Verify NO menu interaction happened (Early exit)
        verify(inputHandler, never()).readInt(anyString());
    }

    @Test
    @DisplayName("Admin Menu - Login Success then Exit")
    void testHandleAdminMenu_LoginSuccess_ThenExit() {
        // 1. Mock Login Success
        when(inputHandler.readString(contains("Username"))).thenReturn("admin");
        when(inputHandler.readPassword(contains("Password"))).thenReturn("123");
        when(adminController.login("admin", "123")).thenReturn(true);

        // 2. Mock Menu Input: 0 (Back Main Menu)
        // Assuming 0 is BACK_MAIN_MENU based on your enum logic
        when(inputHandler.readInt(anyString())).thenReturn(0);

        // 3. Execute
        adminHandler.handleAdminMenu(orderHandler, null);

        // 4. Verify login and menu interaction
        verify(adminController).login("admin", "123");
        verify(inputHandler, times(1)).readInt(anyString());
    }

    // ==========================================
    // 2. Food Management Flow Tests
    // ==========================================

    @Test
    @DisplayName("Food Management - Register, Edit, Delete, View, Exit")
    void testHandleFoodManagement_FullFlow() {
        // --- Setup Login ---
        when(inputHandler.readString(contains("Username"))).thenReturn("admin");
        when(inputHandler.readPassword(contains("Password"))).thenReturn("123");
        when(adminController.login(anyString(), anyString())).thenReturn(true);

        // --- Setup Menu Sequence ---
        // 1. Main Menu: Select 1 (Food Management)
        // 2. Food Menu: Select 1 (Register)
        // 3. Food Menu: Select 2 (Edit)
        // 4. Food Menu: Select 3 (Delete)
        // 5. Food Menu: Select 4 (View All)
        // 6. Food Menu: Select 0 (Exit Food Menu)
        // 7. Main Menu: Select 0 (Back Main Menu)
        when(inputHandler.readInt(anyString()))
            .thenReturn(1) // Enter Food Mgmt
            .thenReturn(1) // Register
            .thenReturn(2) // Edit
            .thenReturn(3) // Delete
            .thenReturn(4) // View
            .thenReturn(0) // Exit Food Mgmt
            .thenReturn(0); // Exit Admin Menu

        // Execute
        adminHandler.handleAdminMenu(orderHandler, null);

        // Verify all FoodHandler methods were called
        verify(foodHandler).handleRegisterFood();
        verify(foodHandler).handleEditFood();
        verify(foodHandler).handleDeleteFood();
        verify(foodHandler).handleDisplayAllFoods();
    }

    @Test
    @DisplayName("Food Management - Invalid Input")
    void testHandleFoodManagement_InvalidInput() {
        // Login
        when(inputHandler.readString(anyString())).thenReturn("admin", "123");
        when(adminController.login(anyString(), anyString())).thenReturn(true);

        // Menu Sequence:
        // 1. Enter Food Mgmt (1)
        // 2. Enter Invalid Option (99)
        // 3. Enter Exit (0)
        // 4. Exit Admin (0)
        when(inputHandler.readInt(anyString()))
            .thenReturn(1, 99, 0, 0);

        adminHandler.handleAdminMenu(orderHandler, null);

        // Verify loop continued and we eventually exited
        verify(foodHandler, never()).handleRegisterFood(); // Invalid input shouldn't trigger actions
    }
}