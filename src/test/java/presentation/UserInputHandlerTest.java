package presentation;

import org.junit.jupiter.api.Test;

import presentation.General.UserInputHandler;

import org.junit.jupiter.api.DisplayName;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

/**
 * User Input Handler Test
 */
public class UserInputHandlerTest {
    
    @Test
    @DisplayName("Test readInt - valid input")
    void testReadInt_Valid() {
        String input = "42\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        UserInputHandler handler = new UserInputHandler(scanner);
        
        int result = handler.readInt("Enter number: ");
        assertEquals(42, result);
    }
    
    @Test
    @DisplayName("Test readInt - invalid then valid")
    void testReadInt_InvalidThenValid() {
        String input = "abc\n42\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        UserInputHandler handler = new UserInputHandler(scanner);
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        
        int result = handler.readInt("Enter number: ");
        assertEquals(42, result);
        assertTrue(out.toString().contains("Invalid input"));
        
        System.setOut(System.out);
    }
    
    @Test
    @DisplayName("Test readDouble - valid input")
    void testReadDouble_Valid() {
        String input = "10.50\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        UserInputHandler handler = new UserInputHandler(scanner);
        
        double result = handler.readDouble("Enter price: ");
        assertEquals(10.50, result, 0.01);
    }
    
    @Test
    @DisplayName("Test readString - returns input")
    void testReadString() {
        String input = "John Doe\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        UserInputHandler handler = new UserInputHandler(scanner);
        
        String result = handler.readString("Enter name: ");
        assertEquals("John Doe", result);
    }
    
    @Test
    @DisplayName("Test readYesNo - Yes")
    void testReadYesNo_Yes() {
        String input = "Y\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        UserInputHandler handler = new UserInputHandler(scanner);
        
        boolean result = handler.readYesNo("Confirm? (Y/N): ");
        assertTrue(result);
    }
    
    @Test
    @DisplayName("Test readYesNo - No")
    void testReadYesNo_No() {
        String input = "N\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        UserInputHandler handler = new UserInputHandler(scanner);
        
        boolean result = handler.readYesNo("Confirm? (Y/N): ");
        assertFalse(result);
    }
    
    @Test
    @DisplayName("Test readYesNo - invalid then valid")
    void testReadYesNo_InvalidThenValid() {
        String input = "maybe\nY\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        UserInputHandler handler = new UserInputHandler(scanner);
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        
        boolean result = handler.readYesNo("Confirm? (Y/N): ");
        assertTrue(result);
        assertTrue(out.toString().contains("Please enter Y"));
        
        System.setOut(System.out);
    }
    
    @Test
    @DisplayName("Test readChar - returns first character")
    void testReadChar() {
        String input = "A\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        UserInputHandler handler = new UserInputHandler(scanner);
        
        char result = handler.readChar("Enter char: ");
        assertEquals('A', result);
    }
    
    @Test
    @DisplayName("Test readChar - empty string")
    void testReadChar_Empty() {
        String input = "\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        UserInputHandler handler = new UserInputHandler(scanner);
        
        char result = handler.readChar("Enter char: ");
        assertEquals(' ', result);
    }
}

