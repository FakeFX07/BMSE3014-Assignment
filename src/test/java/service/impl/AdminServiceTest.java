package service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import repository.interfaces.IAdminRepository;
import util.PasswordUtil;

class AdminServiceTest {

    @Mock
    private IAdminRepository adminRepository;

    @InjectMocks
    private AdminService adminService;

    @BeforeEach
    void setUp() {
        // Initialize Mocks
        MockitoAnnotations.openMocks(this);
    }

    // ==========================================
    // Test Logic: login()
    // ==========================================

    @Test
    @DisplayName("Login - Success (Repo returns true)")
    void testLogin_Success() {
        // Mock behavior: Repo returns true for valid credentials
        // AdminService hashes the password before calling repository
        String hashedPassword = PasswordUtil.hashPassword("123");
        when(adminRepository.authenticate("admin", hashedPassword)).thenReturn(true);

        // Execute
        boolean result = adminService.login("admin", "123");

        // Verify
        assertTrue(result, "Login should succeed when repo returns true");
        verify(adminRepository).authenticate("admin", hashedPassword);
    }

    @Test
    @DisplayName("Login - Failure (Repo returns false)")
    void testLogin_Failure_WrongCredentials() {
        // Mock behavior: Repo returns false for invalid credentials
        // AdminService hashes the password before calling repository
        String hashedPassword = PasswordUtil.hashPassword("wrong");
        when(adminRepository.authenticate("admin", hashedPassword)).thenReturn(false);

        // Execute
        boolean result = adminService.login("admin", "wrong");

        // Verify
        assertFalse(result, "Login should fail when repo returns false");
    }

    @Test
    @DisplayName("Login - Failure (Null Name)")
    void testLogin_NullName() {
        // Execute with null name
        boolean result = adminService.login(null, "123");

        // Verify result is false and REPO WAS NEVER CALLED
        assertFalse(result);
        verify(adminRepository, never()).authenticate(any(), any());
    }

    @Test
    @DisplayName("Login - Failure (Null Password)")
    void testLogin_NullPassword() {
        // Execute with null password
        boolean result = adminService.login("admin", null);

        // Verify result is false and REPO WAS NEVER CALLED
        assertFalse(result);
        verify(adminRepository, never()).authenticate(any(), any());
    }
}