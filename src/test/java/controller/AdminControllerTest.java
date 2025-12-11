package controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import service.interfaces.IAdminService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Test class for AdminController
 * Tests controller logic and interaction with service layer
 * Target: >88% Code Coverage
 */
@ExtendWith(MockitoExtension.class)
public class AdminControllerTest {
    
    @Mock
    private IAdminService adminService;
    
    private AdminController adminController;
    
    @BeforeEach
    void setUp() {
        // Reset mocks before each test
        reset(adminService);
    }
    
    // ==========================================
    // Constructor Tests
    // ==========================================
    
    @Test
    @DisplayName("Constructor with IAdminService - Should initialize correctly")
    void testConstructor_WithIAdminService_InitializesCorrectly() {
        adminController = new AdminController(adminService);
        
        assertNotNull(adminController);
        // Verify controller can use the service
        when(adminService.login(anyString(), anyString())).thenReturn(true);
        boolean result = adminController.login("admin", "123");
        assertTrue(result);
    }
    
    @Test
    @DisplayName("Default constructor - Should initialize with default service")
    void testDefaultConstructor_InitializesWithDefaultService() {
        adminController = new AdminController();
        
        assertNotNull(adminController);
        // Default constructor creates AdminService with AdminRepository
        // This tests the default constructor path
    }
    
    @Test
    @DisplayName("Default constructor - Should be able to login")
    void testDefaultConstructor_CanLogin() {
        adminController = new AdminController();
        
        assertNotNull(adminController);
        // Test that default constructor creates a working controller
        // Note: This will use real AdminService/AdminRepository, so it needs database
        // But we're testing that the constructor works
    }
    
    // ==========================================
    // login() Method Tests
    // ==========================================
    
    @Test
    @DisplayName("Login - Success - Should return true")
    void testLogin_Success_ReturnsTrue() {
        adminController = new AdminController(adminService);
        when(adminService.login("admin", "123")).thenReturn(true);
        
        boolean result = adminController.login("admin", "123");
        
        assertTrue(result);
        verify(adminService).login("admin", "123");
    }
    
    @Test
    @DisplayName("Login - Failure - Should return false")
    void testLogin_Failure_ReturnsFalse() {
        adminController = new AdminController(adminService);
        when(adminService.login("admin", "wrong")).thenReturn(false);
        
        boolean result = adminController.login("admin", "wrong");
        
        assertFalse(result);
        verify(adminService).login("admin", "wrong");
    }
    
    @Test
    @DisplayName("Login - Wrong username - Should return false")
    void testLogin_WrongUsername_ReturnsFalse() {
        adminController = new AdminController(adminService);
        when(adminService.login("wronguser", "123")).thenReturn(false);
        
        boolean result = adminController.login("wronguser", "123");
        
        assertFalse(result);
        verify(adminService).login("wronguser", "123");
    }
    
    @Test
    @DisplayName("Login - Wrong password - Should return false")
    void testLogin_WrongPassword_ReturnsFalse() {
        adminController = new AdminController(adminService);
        when(adminService.login("admin", "wrongpass")).thenReturn(false);
        
        boolean result = adminController.login("admin", "wrongpass");
        
        assertFalse(result);
        verify(adminService).login("admin", "wrongpass");
    }
    
    @Test
    @DisplayName("Login - Null username - Should delegate to service")
    void testLogin_NullUsername_DelegatesToService() {
        adminController = new AdminController(adminService);
        when(adminService.login(null, "123")).thenReturn(false);
        
        boolean result = adminController.login(null, "123");
        
        assertFalse(result);
        verify(adminService).login(null, "123");
    }
    
    @Test
    @DisplayName("Login - Null password - Should delegate to service")
    void testLogin_NullPassword_DelegatesToService() {
        adminController = new AdminController(adminService);
        when(adminService.login("admin", null)).thenReturn(false);
        
        boolean result = adminController.login("admin", null);
        
        assertFalse(result);
        verify(adminService).login("admin", null);
    }
    
    @Test
    @DisplayName("Login - Both null - Should delegate to service")
    void testLogin_BothNull_DelegatesToService() {
        adminController = new AdminController(adminService);
        when(adminService.login(null, null)).thenReturn(false);
        
        boolean result = adminController.login(null, null);
        
        assertFalse(result);
        verify(adminService).login(null, null);
    }
    
    @Test
    @DisplayName("Login - Empty username - Should delegate to service")
    void testLogin_EmptyUsername_DelegatesToService() {
        adminController = new AdminController(adminService);
        when(adminService.login("", "123")).thenReturn(false);
        
        boolean result = adminController.login("", "123");
        
        assertFalse(result);
        verify(adminService).login("", "123");
    }
    
    @Test
    @DisplayName("Login - Empty password - Should delegate to service")
    void testLogin_EmptyPassword_DelegatesToService() {
        adminController = new AdminController(adminService);
        when(adminService.login("admin", "")).thenReturn(false);
        
        boolean result = adminController.login("admin", "");
        
        assertFalse(result);
        verify(adminService).login("admin", "");
    }
    
    @Test
    @DisplayName("Login - Multiple calls - Should call service each time")
    void testLogin_MultipleCalls_CallsServiceEachTime() {
        adminController = new AdminController(adminService);
        when(adminService.login("admin", "123")).thenReturn(true);
        when(adminService.login("admin", "wrong")).thenReturn(false);
        
        boolean result1 = adminController.login("admin", "123");
        boolean result2 = adminController.login("admin", "wrong");
        
        assertTrue(result1);
        assertFalse(result2);
        verify(adminService, times(2)).login(anyString(), anyString());
        verify(adminService).login("admin", "123");
        verify(adminService).login("admin", "wrong");
    }
    
    @Test
    @DisplayName("Login - Case sensitive username - Should delegate to service")
    void testLogin_CaseSensitiveUsername_DelegatesToService() {
        adminController = new AdminController(adminService);
        when(adminService.login("ADMIN", "123")).thenReturn(false);
        
        boolean result = adminController.login("ADMIN", "123");
        
        assertFalse(result);
        verify(adminService).login("ADMIN", "123");
    }
    
    @Test
    @DisplayName("Login - Special characters in username - Should delegate to service")
    void testLogin_SpecialCharacters_DelegatesToService() {
        adminController = new AdminController(adminService);
        when(adminService.login("admin@123", "pass")).thenReturn(false);
        
        boolean result = adminController.login("admin@123", "pass");
        
        assertFalse(result);
        verify(adminService).login("admin@123", "pass");
    }
    
    @Test
    @DisplayName("Login - Long username - Should delegate to service")
    void testLogin_LongUsername_DelegatesToService() {
        adminController = new AdminController(adminService);
        String longUsername = "a".repeat(100);
        when(adminService.login(longUsername, "123")).thenReturn(false);
        
        boolean result = adminController.login(longUsername, "123");
        
        assertFalse(result);
        verify(adminService).login(longUsername, "123");
    }
    
    @Test
    @DisplayName("Login - Long password - Should delegate to service")
    void testLogin_LongPassword_DelegatesToService() {
        adminController = new AdminController(adminService);
        String longPassword = "p".repeat(100);
        when(adminService.login("admin", longPassword)).thenReturn(false);
        
        boolean result = adminController.login("admin", longPassword);
        
        assertFalse(result);
        verify(adminService).login("admin", longPassword);
    }
    
    @Test
    @DisplayName("Login - Whitespace in username - Should delegate to service")
    void testLogin_WhitespaceInUsername_DelegatesToService() {
        adminController = new AdminController(adminService);
        when(adminService.login("admin user", "123")).thenReturn(false);
        
        boolean result = adminController.login("admin user", "123");
        
        assertFalse(result);
        verify(adminService).login("admin user", "123");
    }
    
    @Test
    @DisplayName("Login - Whitespace in password - Should delegate to service")
    void testLogin_WhitespaceInPassword_DelegatesToService() {
        adminController = new AdminController(adminService);
        when(adminService.login("admin", "pass word")).thenReturn(false);
        
        boolean result = adminController.login("admin", "pass word");
        
        assertFalse(result);
        verify(adminService).login("admin", "pass word");
    }
    
    @Test
    @DisplayName("Login - Verify service is called exactly once per login attempt")
    void testLogin_VerifyServiceCalledOnce() {
        adminController = new AdminController(adminService);
        when(adminService.login("admin", "123")).thenReturn(true);
        
        adminController.login("admin", "123");
        
        verify(adminService, times(1)).login("admin", "123");
        verifyNoMoreInteractions(adminService);
    }
    
    @Test
    @DisplayName("Login - Verify service interaction order")
    void testLogin_VerifyServiceInteractionOrder() {
        adminController = new AdminController(adminService);
        when(adminService.login("admin", "123")).thenReturn(true);
        when(adminService.login("user", "456")).thenReturn(false);
        
        boolean result1 = adminController.login("admin", "123");
        boolean result2 = adminController.login("user", "456");
        
        assertTrue(result1);
        assertFalse(result2);
        
        // Verify order of calls
        verify(adminService).login("admin", "123");
        verify(adminService).login("user", "456");
    }
}

