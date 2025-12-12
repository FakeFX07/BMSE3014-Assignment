package util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Password Utility Class
 * Provides SHA-256 password hashing functionality
 * Follows SOLID: Single Responsibility Principle
 */
public class PasswordUtil {

    /**
     * Hashes a plain text password using SHA-256.
     *
     * @param password The plain text password.
     * @return The SHA-256 hashed password as a hexadecimal string.
     * @throws RuntimeException if SHA-256 algorithm is not available.
     */
    public static String hashPassword(String password) {
        if (password == null) {
            throw new IllegalArgumentException("Password cannot be null");
        }
        
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            // Convert byte array to hexadecimal string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            // This should ideally not happen as SHA-256 is a standard algorithm
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }

    /**
     * Verifies a plain text password against a stored SHA-256 hash.
     *
     * @param inputPassword The plain text password provided by the user.
     * @param storedHash The SHA-256 hash retrieved from the database.
     * @return true if the hashed input password matches the stored hash, false otherwise.
     */
    public static boolean verifyPassword(String inputPassword, String storedHash) {
        if (inputPassword == null || storedHash == null) {
            return false;
        }
        String hashedInputPassword = hashPassword(inputPassword);
        return hashedInputPassword.equals(storedHash);
    }
}

