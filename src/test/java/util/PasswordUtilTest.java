package util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * PasswordUtil Test
 * Tests SHA-256 password hashing and verification functionality
 */
public class PasswordUtilTest {

    @Test
    @DisplayName("Test hashPassword - valid password")
    void testHashPassword_Valid() {
        String password = "testPassword123";
        String hash = PasswordUtil.hashPassword(password);
        
        assertNotNull(hash);
        assertFalse(hash.isEmpty());
        assertEquals(64, hash.length()); // SHA-256 produces 64 hex characters
        // Verify it's hexadecimal
        assertTrue(hash.matches("[0-9a-f]{64}"));
    }

    @Test
    @DisplayName("Test hashPassword - null password throws exception")
    void testHashPassword_Null() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> PasswordUtil.hashPassword(null)
        );
        assertEquals("Password cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Test hashPassword - empty string")
    void testHashPassword_EmptyString() {
        String hash = PasswordUtil.hashPassword("");
        
        assertNotNull(hash);
        assertEquals(64, hash.length());
        // Empty string should produce a specific hash
        String expectedHash = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855";
        assertEquals(expectedHash, hash);
    }

    @Test
    @DisplayName("Test hashPassword - special characters")
    void testHashPassword_SpecialCharacters() {
        String password = "!@#$%^&*()_+-=[]{}|;:,.<>?";
        String hash = PasswordUtil.hashPassword(password);
        
        assertNotNull(hash);
        assertEquals(64, hash.length());
        assertTrue(hash.matches("[0-9a-f]{64}"));
    }

    @Test
    @DisplayName("Test hashPassword - very long password")
    void testHashPassword_VeryLong() {
        String password = "a".repeat(1000);
        String hash = PasswordUtil.hashPassword(password);
        
        assertNotNull(hash);
        assertEquals(64, hash.length());
        assertTrue(hash.matches("[0-9a-f]{64}"));
    }

    @Test
    @DisplayName("Test hashPassword - deterministic (same input produces same hash)")
    void testHashPassword_Deterministic() {
        String password = "testPassword";
        String hash1 = PasswordUtil.hashPassword(password);
        String hash2 = PasswordUtil.hashPassword(password);
        
        assertEquals(hash1, hash2, "Same password should produce same hash");
    }

    @Test
    @DisplayName("Test hashPassword - different passwords produce different hashes")
    void testHashPassword_DifferentPasswords() {
        String hash1 = PasswordUtil.hashPassword("password1");
        String hash2 = PasswordUtil.hashPassword("password2");
        
        assertNotEquals(hash1, hash2, "Different passwords should produce different hashes");
    }

    @Test
    @DisplayName("Test verifyPassword - valid password matches stored hash")
    void testVerifyPassword_ValidMatch() {
        String password = "testPassword123";
        String storedHash = PasswordUtil.hashPassword(password);
        
        boolean result = PasswordUtil.verifyPassword(password, storedHash);
        
        assertTrue(result, "Valid password should match stored hash");
    }

    @Test
    @DisplayName("Test verifyPassword - valid password doesn't match wrong hash")
    void testVerifyPassword_NoMatch() {
        String password = "testPassword123";
        String wrongHash = PasswordUtil.hashPassword("wrongPassword");
        
        boolean result = PasswordUtil.verifyPassword(password, wrongHash);
        
        assertFalse(result, "Password should not match wrong hash");
    }

    @Test
    @DisplayName("Test verifyPassword - null inputPassword returns false")
    void testVerifyPassword_NullInputPassword() {
        String storedHash = PasswordUtil.hashPassword("somePassword");
        
        boolean result = PasswordUtil.verifyPassword(null, storedHash);
        
        assertFalse(result, "Null input password should return false");
    }

    @Test
    @DisplayName("Test verifyPassword - null storedHash returns false")
    void testVerifyPassword_NullStoredHash() {
        String password = "testPassword";
        
        boolean result = PasswordUtil.verifyPassword(password, null);
        
        assertFalse(result, "Null stored hash should return false");
    }

    @Test
    @DisplayName("Test verifyPassword - both null returns false")
    void testVerifyPassword_BothNull() {
        boolean result = PasswordUtil.verifyPassword(null, null);
        
        assertFalse(result, "Both null should return false");
    }

    @Test
    @DisplayName("Test verifyPassword - empty strings")
    void testVerifyPassword_EmptyStrings() {
        String emptyHash = PasswordUtil.hashPassword("");
        
        boolean result1 = PasswordUtil.verifyPassword("", emptyHash);
        assertTrue(result1, "Empty password should match empty hash");
        
        boolean result2 = PasswordUtil.verifyPassword("", "wrongHash");
        assertFalse(result2, "Empty password should not match wrong hash");
    }

    @Test
    @DisplayName("Test verifyPassword - case sensitive")
    void testVerifyPassword_CaseSensitive() {
        String password = "TestPassword";
        String storedHash = PasswordUtil.hashPassword(password);
        
        boolean result1 = PasswordUtil.verifyPassword("TestPassword", storedHash);
        assertTrue(result1, "Exact case match should work");
        
        boolean result2 = PasswordUtil.verifyPassword("testpassword", storedHash);
        assertFalse(result2, "Different case should not match");
    }

    @Test
    @DisplayName("Test hashPassword - unicode characters")
    void testHashPassword_Unicode() {
        String password = "密码123🔒";
        String hash = PasswordUtil.hashPassword(password);
        
        assertNotNull(hash);
        assertEquals(64, hash.length());
        assertTrue(hash.matches("[0-9a-f]{64}"));
    }

    @Test
    @DisplayName("Test verifyPassword - unicode characters")
    void testVerifyPassword_Unicode() {
        String password = "密码123🔒";
        String storedHash = PasswordUtil.hashPassword(password);
        
        boolean result = PasswordUtil.verifyPassword(password, storedHash);
        assertTrue(result, "Unicode password should verify correctly");
    }

    @Test
    @DisplayName("Test hashPassword - ensures hex padding logic is covered")
    void testHashPassword_HexPadding() {
        // Test multiple passwords to ensure we hit the hex padding case
        // (when byte < 16, hex.length() == 1 and needs padding)
        String[] passwords = {
            "0", "1", "a", "test", "password", 
            "\u0000", "\u0001", "\u000F", // Bytes that produce single hex chars
            "!", "@", "#", "$" // Various special chars
        };
        
        for (String password : passwords) {
            String hash = PasswordUtil.hashPassword(password);
            assertNotNull(hash);
            assertEquals(64, hash.length(), "Hash for password '" + password + "' should be 64 chars");
            assertTrue(hash.matches("[0-9a-f]{64}"), "Hash should be hexadecimal");
        }
    }

    @Test
    @DisplayName("Test hashPassword - whitespace handling")
    void testHashPassword_Whitespace() {
        String password1 = " password ";
        String password2 = "password";
        
        String hash1 = PasswordUtil.hashPassword(password1);
        String hash2 = PasswordUtil.hashPassword(password2);
        
        assertNotEquals(hash1, hash2, "Whitespace should affect hash");
        assertEquals(64, hash1.length());
        assertEquals(64, hash2.length());
    }

    @Test
    @DisplayName("Test verifyPassword - whitespace sensitive")
    void testVerifyPassword_WhitespaceSensitive() {
        String password = "test";
        String hash = PasswordUtil.hashPassword(password);
        
        assertTrue(PasswordUtil.verifyPassword("test", hash));
        assertFalse(PasswordUtil.verifyPassword(" test", hash));
        assertFalse(PasswordUtil.verifyPassword("test ", hash));
        assertFalse(PasswordUtil.verifyPassword(" test ", hash));
    }

    @Test
    @DisplayName("Test hashPassword - newline and tab characters")
    void testHashPassword_NewlineAndTab() {
        String passwordWithNewline = "test\npassword";
        String passwordWithTab = "test\tpassword";
        String passwordNormal = "testpassword";
        
        String hash1 = PasswordUtil.hashPassword(passwordWithNewline);
        String hash2 = PasswordUtil.hashPassword(passwordWithTab);
        String hash3 = PasswordUtil.hashPassword(passwordNormal);
        
        assertNotEquals(hash1, hash2);
        assertNotEquals(hash1, hash3);
        assertNotEquals(hash2, hash3);
        assertEquals(64, hash1.length());
        assertEquals(64, hash2.length());
        assertEquals(64, hash3.length());
    }

    @Test
    @DisplayName("Test verifyPassword - newline and tab characters")
    void testVerifyPassword_NewlineAndTab() {
        String password = "test\npassword";
        String hash = PasswordUtil.hashPassword(password);
        
        assertTrue(PasswordUtil.verifyPassword("test\npassword", hash));
        assertFalse(PasswordUtil.verifyPassword("testpassword", hash));
    }
}

