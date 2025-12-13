package presentation.General;

import org.junit.jupiter.api.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

//Unit tests for UserInputHandler.
class UserInputHandlerTest {

    private final ByteArrayOutputStream outputCaptor = new ByteArrayOutputStream();
    private final PrintStream standardOut = System.out;

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outputCaptor));
    }

    @AfterEach
    void tearDown() {
        System.setOut(standardOut);
    }

    @Nested
    @DisplayName("Integer Input Tests")
    class IntegerTests {
        
        @Test
        @DisplayName("Should read valid integer immediately")
        void shouldReadValidInteger() {
            UserInputHandler handler = createHandlerWithInput("42\n");
            
            int result = handler.readInt("Enter number: ");
            assertEquals(42, result);
        }

        @Test
        @DisplayName("Should loop until valid integer is provided")
        void shouldHandleInvalidIntegerInput() {
            // Input: "abc" (invalid) -> "42" (valid)
            UserInputHandler handler = createHandlerWithInput("abc\n42\n");
            
            int result = handler.readInt("Enter number: ");
            
            assertEquals(42, result);
            assertTrue(outputCaptor.toString().contains("Invalid input"), "Should warn user about invalid input");
        }
    }

    @Nested
    @DisplayName("Decimal Input Tests")
    class DoubleTests {
        
        @Test
        @DisplayName("Should read valid double with precision")
        void shouldReadValidDouble() {
            UserInputHandler handler = createHandlerWithInput("10.50\n");
            
            double result = handler.readDouble("Enter price: ");
            assertEquals(10.50, result, 0.001);
        }

        @Test
        @DisplayName("Should handle non-numeric input gracefully")
        void shouldRetryOnInvalidDouble() {
            UserInputHandler handler = createHandlerWithInput("free\n0.00\n");
            
            double result = handler.readDouble("Enter price: ");
            assertEquals(0.00, result, 0.001);
            assertTrue(outputCaptor.toString().contains("Invalid input"));
        }
    }

    @Nested
    @DisplayName("String & Character Tests")
    class TextTests {

        @Test
        @DisplayName("Should read full line of text")
        void shouldReadStringLine() {
            UserInputHandler handler = createHandlerWithInput("John Doe\n");
            assertEquals("John Doe", handler.readString("Enter name: "));
        }

        @Test
        @DisplayName("Should read single character")
        void shouldReadFirstChar() {
            UserInputHandler handler = createHandlerWithInput("Alpha\n");
            assertEquals('A', handler.readChar("Enter char: "));
        }

        @Test
        @DisplayName("Should return default space for empty char input")
        void shouldHandleEmptyCharInput() {
            UserInputHandler handler = createHandlerWithInput("\n");
            assertEquals(' ', handler.readChar("Enter char: "));
        }
    }

    @Nested
    @DisplayName("Boolean Logic Tests")
    class BooleanTests {

        @Test
        @DisplayName("Should parse 'Y' as true")
        void shouldReturnTrueForYes() {
            UserInputHandler handler = createHandlerWithInput("Y\n");
            assertTrue(handler.readYesNo("Confirm?"));
        }

        @Test
        @DisplayName("Should parse 'N' as false")
        void shouldReturnFalseForNo() {
            UserInputHandler handler = createHandlerWithInput("N\n");
            assertFalse(handler.readYesNo("Confirm?"));
        }

        @Test
        @DisplayName("Should retry on ambiguous input")
        void shouldRetryUntilYesOrNo() {
            // Input: "maybe" -> "Y"
            UserInputHandler handler = createHandlerWithInput("maybe\nY\n");
            
            assertTrue(handler.readYesNo("Confirm?"));
            assertTrue(outputCaptor.toString().contains("Please enter Y"));
        }
    }

    private UserInputHandler createHandlerWithInput(String data) {
        Scanner mockScanner = new Scanner(new ByteArrayInputStream(data.getBytes()));
        return new UserInputHandler(mockScanner);
    }
}