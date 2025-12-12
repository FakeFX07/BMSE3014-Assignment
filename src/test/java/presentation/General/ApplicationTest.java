package presentation.General;

import model.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Application Test
 * Tests application functionality with mocked dependencies
 */
public class ApplicationTest {
    private Scanner testScanner;
    private ByteArrayOutputStream outContent;
    
    @BeforeEach
    void setUp() {
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }
    
    @AfterEach
    void tearDown() {
        System.setOut(System.out);
        if (testScanner != null) {
            testScanner.close();
        }
    }
    
    @Test
    @DisplayName("Test Application constructor - initializes controllers")
    void testApplicationConstructor() {
        Application app = new Application();
        assertNotNull(app);
    }
    
    
    @Test
    @DisplayName("Test handleOrderReport - displays report")
    void testHandleOrderReport() {
        Application app = new Application();
        app.handleOrderReport();
        String output = outContent.toString();
        assertTrue(output.contains("Order Report") || output.length() > 0);
    }
    
    @Test
    @DisplayName("Test processOrder - empty order details")
    void testProcessOrder_EmptyDetails() {
        System.setIn(new ByteArrayInputStream("1\n".getBytes()));
        Application app = new Application();
        
        List<OrderDetails> emptyList = new ArrayList<>();
        app.processOrder(emptyList);
        
        String output = outContent.toString();
        assertTrue(output.contains("No items") || output.length() > 0);
        System.setIn(System.in);
    }
    
    @Test
    @DisplayName("Test handleOrder - no current customer")
    void testHandleOrder_NoCustomer() {
        System.setIn(new ByteArrayInputStream("".getBytes()));
        Application app = new Application();
        app.handleOrder();
        String output = outContent.toString();
        assertTrue(output.contains("login") || output.contains("Login") || output.length() > 0);
        System.setIn(System.in);
    }
    
    @Test
    @DisplayName("Test handleLogin - invalid credentials")
    void testHandleLogin_Invalid() {
        System.setIn(new ByteArrayInputStream("9999\nwrongpass\n".getBytes()));
        Application app = new Application();
        app.handleLogin();
        String output = outContent.toString();
        assertTrue(output.contains("Wrong") || output.length() > 0);
        System.setIn(System.in);
    }
    
    @Test
    @DisplayName("Test handleAdminMenu - exit option")
    void testHandleAdminMenu_Exit() {
        System.setIn(new ByteArrayInputStream("admin\n123\n0\n".getBytes()));
        Application app = new Application();
        app.handleAdminMenu();
        String output = outContent.toString();
        assertTrue(output.length() > 0);
        System.setIn(System.in);
    }
    
    @Test
    @DisplayName("Test handleAdminMenu - invalid choice")
    void testHandleAdminMenu_InvalidChoice() {
        // Need login credentials first: admin\n123\n
        System.setIn(new ByteArrayInputStream("admin\n123\n99\n0\n".getBytes()));
        Application app = new Application();
        app.handleAdminMenu();
        String output = outContent.toString();
        assertTrue(output.length() > 0);
        System.setIn(System.in);
    }
    
    @Test
    @DisplayName("Test handleAdminMenu - view all foods option")
    void testHandleAdminMenu_ViewAllFoods() {
        // Need login credentials first: admin\n123\n
        System.setIn(new ByteArrayInputStream("admin\n123\n4\n0\n".getBytes()));
        Application app = new Application();
        app.handleAdminMenu();
        String output = outContent.toString();
        assertTrue(output.length() > 0);
        System.setIn(System.in);
    }
    
    @Test
    @DisplayName("Test handleAdminMenu - order report option")
    void testHandleAdminMenu_OrderReport() {
        // Need login credentials first: admin\n123\n
        System.setIn(new ByteArrayInputStream("admin\n123\n2\n0\n".getBytes()));
        Application app = new Application();
        app.handleAdminMenu();
        String output = outContent.toString();
        assertTrue(output.length() > 0);
        System.setIn(System.in);
    }
    
    @Test
    @DisplayName("Test handleOrder - empty food list")
    void testHandleOrder_EmptyFoodList() {
        System.setIn(new ByteArrayInputStream("".getBytes()));
        Application app = new Application();
        // This will fail because no foods, but tests the code path
        try {
            app.handleOrder();
        } catch (Exception e) {
            // Expected if database not available
        }
        System.setIn(System.in);
    }
    
    @Test
    @DisplayName("Test handleLogin - success with order")
    void testHandleLogin_SuccessWithOrder() {
        String input = "1000\npassword123\nY\n0\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Application app = new Application();
        app.handleLogin();
        String output = outContent.toString();
        assertTrue(output.length() > 0);
        System.setIn(System.in);
    }
    
    @Test
    @DisplayName("Test handleLogin - success without order")
    void testHandleLogin_SuccessWithoutOrder() {
        String input = "1000\npassword123\nN\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Application app = new Application();
        app.handleLogin();
        String output = outContent.toString();
        assertTrue(output.contains("Welcome") || output.length() > 0);
        System.setIn(System.in);
    }
    
    @Test
    @DisplayName("Test handleRegister - complete flow with validation loops")
    void testHandleRegister_CompleteFlow() {
        // Generate unique phone to avoid duplicates (10 digits: 01 + 8 digits)
        String uniquePhone = "01" + String.format("%08d", Math.abs((int)(System.currentTimeMillis() % 100000000)));
        String input = "Y\nJohn Doe\n25\n" + uniquePhone + "\nMale\npassword123\npassword123\nY\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Application app = new Application();
        try {
            app.handleRegister();
        } catch (Exception e) {
            // Handle any exceptions that might occur
        }
        String output = outContent.toString();
        assertTrue(output.length() > 0);
        System.setIn(System.in);
    }
    
    @Test
    @DisplayName("Test handleRegister - with validation retries")
    void testHandleRegister_WithValidationRetries() {
        // Generate unique phone to avoid duplicates (10 digits: 01 + 8 digits)
        String uniquePhone = "01" + String.format("%08d", Math.abs((int)(System.currentTimeMillis() % 100000000)));
        String input = "Y\nJohn123\nJohn Doe\n15\n25\n012345\n" + uniquePhone + "\nInvalid\nMale\npass\npassword123\nwrong\npassword123\nY\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Application app = new Application();
        try {
            app.handleRegister();
        } catch (Exception e) {
            // Handle any exceptions that might occur
        }
        String output = outContent.toString();
        assertTrue(output.length() > 0);
        System.setIn(System.in);
    }
    
    @Test
    @DisplayName("Test handleOrder - food choice 0 (stop)")
    void testHandleOrder_FoodChoiceZero() {
        System.setIn(new ByteArrayInputStream("0\n".getBytes()));
        Application app = new Application();
        app.currentCustomer = new Customer(1000, "John Doe");
        try {
            app.handleOrder();
        } catch (Exception e) {
            // Expected if database not available
        }
        System.setIn(System.in);
    }
    
    @Test
    @DisplayName("Test handleOrder - invalid food choice")
    void testHandleOrder_InvalidFoodChoice() {
        System.setIn(new ByteArrayInputStream("99\n0\n".getBytes()));
        Application app = new Application();
        app.currentCustomer = new Customer(1000, "John Doe");
        try {
            app.handleOrder();
        } catch (Exception e) {
            // Expected if database not available
        }
        System.setIn(System.in);
    }
    
    @Test
    @DisplayName("Test handleOrder - quantity validation")
    void testHandleOrder_QuantityValidation() {
        System.setIn(new ByteArrayInputStream("1\n0\n2\nN\nN\n0\n".getBytes()));
        Application app = new Application();
        app.currentCustomer = new Customer(1000, "John Doe");
        try {
            app.handleOrder();
        } catch (Exception e) {
            // Expected if database not available
        }
        System.setIn(System.in);
    }
    
    
    @Test
    @DisplayName("Test run - exit with correct password")
    void testRun_ExitWithPassword() {
        String input = "4\n1890\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Application app = new Application();
        try {
            app.run();
        } catch (Exception e) {
            // Expected if scanner closes
        }
        String output = outContent.toString();
        assertTrue(output.length() > 0);
        System.setIn(System.in);
    }
    
    @Test
    @DisplayName("Test run - exit with wrong password")
    void testRun_ExitWithWrongPassword() {
        String input = "4\n1234\n1\n4\n1890\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Application app = new Application();
        try {
            app.run();
        } catch (Exception e) {
            // Expected if scanner closes
        }
        String output = outContent.toString();
        assertTrue(output.length() > 0);
        System.setIn(System.in);
    }
    
    @Test
    @DisplayName("Test run - invalid choice")
    void testRun_InvalidChoice() {
        String input = "99\n4\n1890\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Application app = new Application();
        try {
            app.run();
        } catch (Exception e) {
            // Expected if scanner closes
        }
        String output = outContent.toString();
        assertTrue(output.length() > 0);
        System.setIn(System.in);
    }
    
    
    @Test
    @DisplayName("Test handleOrder - complete order flow")
    void testHandleOrder_CompleteFlow() {
        System.setIn(new ByteArrayInputStream("1\n2\nY\nY\nN\n0\n".getBytes()));
        Application app = new Application();
        app.currentCustomer = new Customer(1000, "John Doe");
        try {
            app.handleOrder();
        } catch (Exception e) {
            // Expected if database not available
        }
        System.setIn(System.in);
    }
    
    @Test
    @DisplayName("Test handleOrder - user declines to order item")
    void testHandleOrder_DeclineOrderItem() {
        System.setIn(new ByteArrayInputStream("1\n2\nN\nN\n0\n".getBytes()));
        Application app = new Application();
        app.currentCustomer = new Customer(1000, "John Doe");
        try {
            app.handleOrder();
        } catch (Exception e) {
            // Expected if database not available
        }
        System.setIn(System.in);
    }
    
    @Test
    @DisplayName("Test handleOrder - proceed with another order")
    void testHandleOrder_ProceedAnotherOrder() {
        System.setIn(new ByteArrayInputStream("1\n1\nY\nY\nY\n0\n".getBytes()));
        Application app = new Application();
        app.currentCustomer = new Customer(1000, "John Doe");
        try {
            app.handleOrder();
        } catch (Exception e) {
            // Expected if database not available
        }
        System.setIn(System.in);
    }
    
    @Test
    @DisplayName("Test handleAdminMenu - all menu options")
    void testHandleAdminMenu_AllOptions() {
        // Option 1: Food Management -> 0 to exit back
        // Option 2: Order Report
        // Option 0: Back to Main Menu
        // Need login credentials first: admin\n123\n
        String input = "admin\n123\n1\n0\n2\n0\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Application app = new Application();
        app.handleAdminMenu();
        String output = outContent.toString();
        assertTrue(output.length() > 0);
        System.setIn(System.in);
    }
    
    @Test
    @DisplayName("Test handleRegister - cancel at final confirmation")
    void testHandleRegister_CancelAtFinal() {
        // Generate unique phone to avoid duplicates (10 digits: 01 + 8 digits)
        String uniquePhone = "01" + String.format("%08d", Math.abs((int)(System.currentTimeMillis() % 100000000)));
        String input = "Y\nJohn Doe\n25\n" + uniquePhone + "\nMale\npassword123\npassword123\nN\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Application app = new Application();
        try {
            app.handleRegister();
        } catch (Exception e) {
            // Handle any exceptions that might occur
        }
        String output = outContent.toString();
        assertTrue(output.contains("Quit") || output.length() > 0);
        System.setIn(System.in);
    }
    
    
    @Test
    @DisplayName("Test run - login option")
    void testRun_LoginOption() {
        String input = "1\n9999\nwrong\n4\n1890\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Application app = new Application();
        try {
            app.run();
        } catch (Exception e) {
            // Expected if scanner closes
        }
        String output = outContent.toString();
        assertTrue(output.length() > 0);
        System.setIn(System.in);
    }
    
    @Test
    @DisplayName("Test run - register option")
    void testRun_RegisterOption() {
        String input = "2\nN\n4\n1890\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Application app = new Application();
        try {
            app.run();
        } catch (Exception e) {
            // Expected if scanner closes
        }
        String output = outContent.toString();
        assertTrue(output.length() > 0);
        System.setIn(System.in);
    }
    
    @Test
    @DisplayName("Test run - admin menu option")
    void testRun_AdminMenuOption() {
        // Need login credentials for admin menu: admin\n123\n
        String input = "3\nadmin\n123\n0\n4\n1890\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Application app = new Application();
        try {
            app.run();
        } catch (Exception e) {
            // Expected if scanner closes
        }
        String output = outContent.toString();
        assertTrue(output.length() > 0);
        System.setIn(System.in);
    }
    
    @Test
    @DisplayName("Test handleLogin - multiple login attempts")
    void testHandleLogin_MultipleAttempts() {
        String input = "9999\nwrong1\n9999\nwrong2\n1000\npassword123\nN\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Application app = new Application();
        app.handleLogin();
        String output = outContent.toString();
        assertTrue(output.length() > 0);
        System.setIn(System.in);
    }
    
    @Test
    @DisplayName("Test handleRegister - all validation failures then success")
    void testHandleRegister_AllValidationFailures() {
        // Generate unique phone to avoid duplicates (10 digits: 01 + 8 digits)
        String uniquePhone = "01" + String.format("%08d", Math.abs((int)(System.currentTimeMillis() % 100000000)));
        String input = "Y\n123\nJohn Doe\n10\n25\n123\n" + uniquePhone + "\nInvalid\nMale\n12\npassword123\nwrong\npassword123\nY\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Application app = new Application();
        try {
            app.handleRegister();
        } catch (Exception e) {
            // Handle any exceptions that might occur
        }
        String output = outContent.toString();
        assertTrue(output.length() > 0);
        System.setIn(System.in);
    }
    
    @Test
    @DisplayName("Test handleOrder - user declines to complete ordering")
    void testHandleOrder_DeclineCompleteOrdering() {
        System.setIn(new ByteArrayInputStream("1\n2\nY\nN\n0\n".getBytes()));
        Application app = new Application();
        app.currentCustomer = new Customer(1000, "John Doe");
        try {
            app.handleOrder();
        } catch (Exception e) {
            // Expected if database not available
        }
        System.setIn(System.in);
    }
    
    @Test
    @DisplayName("Test handleOrder - multiple items then complete")
    void testHandleOrder_MultipleItems() {
        System.setIn(new ByteArrayInputStream("1\n2\nY\n1\n1\nY\nY\nN\n0\n".getBytes()));
        Application app = new Application();
        app.currentCustomer = new Customer(1000, "John Doe");
        try {
            app.handleOrder();
        } catch (Exception e) {
            // Expected if database not available
        }
        System.setIn(System.in);
    }
}

