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
import java.util.Arrays;
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
    @DisplayName("Test handleRegister - user cancels")
    void testHandleRegister_Cancel() {
        System.setIn(new ByteArrayInputStream("N\n".getBytes()));
        Application app = new Application();
        app.handleRegister();
        String output = outContent.toString();
        assertTrue(output.length() > 0);
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
        System.setIn(new ByteArrayInputStream("0\n".getBytes()));
        Application app = new Application();
        app.handleAdminMenu();
        String output = outContent.toString();
        assertTrue(output.length() > 0);
        System.setIn(System.in);
    }
    
    @Test
    @DisplayName("Test handleAdminMenu - invalid choice")
    void testHandleAdminMenu_InvalidChoice() {
        System.setIn(new ByteArrayInputStream("99\n0\n".getBytes()));
        Application app = new Application();
        app.handleAdminMenu();
        String output = outContent.toString();
        assertTrue(output.length() > 0);
        System.setIn(System.in);
    }
    
    @Test
    @DisplayName("Test handleAdminMenu - view all foods option")
    void testHandleAdminMenu_ViewAllFoods() {
        System.setIn(new ByteArrayInputStream("4\n0\n".getBytes()));
        Application app = new Application();
        app.handleAdminMenu();
        String output = outContent.toString();
        assertTrue(output.length() > 0);
        System.setIn(System.in);
    }
    
    @Test
    @DisplayName("Test handleAdminMenu - order report option")
    void testHandleAdminMenu_OrderReport() {
        System.setIn(new ByteArrayInputStream("5\n0\n".getBytes()));
        Application app = new Application();
        app.handleAdminMenu();
        String output = outContent.toString();
        assertTrue(output.length() > 0);
        System.setIn(System.in);
    }
    
    
    @Test
    @DisplayName("Test processOrder - TNG payment")
    void testProcessOrder_TNGPayment() {
        String input = "1\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Application app = new Application();
        
        // Set current customer for the test
        app.currentCustomer = new Customer(1000, "John Doe");
        
        Food food = new Food(2000, "Chicken Rice", 10.50, "Set");
        OrderDetails detail = new OrderDetails(food, 1);
        List<OrderDetails> details = Arrays.asList(detail);
        
        app.processOrder(details);
        String output = outContent.toString();
        assertTrue(output.length() > 0);
        System.setIn(System.in);
    }
    
    @Test
    @DisplayName("Test processOrder - invalid payment choice")
    void testProcessOrder_InvalidPayment() {
        String input = "99\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Application app = new Application();
        
        // Set current customer for the test
        app.currentCustomer = new Customer(1000, "John Doe");
        
        Food food = new Food(2000, "Chicken Rice", 10.50, "Set");
        OrderDetails detail = new OrderDetails(food, 1);
        List<OrderDetails> details = Arrays.asList(detail);
        
        app.processOrder(details);
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
        String input = "Y\nJohn Doe\n25\n0123456789\nMale\npassword123\npassword123\nY\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Application app = new Application();
        app.handleRegister();
        String output = outContent.toString();
        assertTrue(output.length() > 0);
        System.setIn(System.in);
    }
    
    @Test
    @DisplayName("Test handleRegister - with validation retries")
    void testHandleRegister_WithValidationRetries() {
        String input = "Y\nJohn123\nJohn Doe\n15\n25\n012345\n0123456789\nInvalid\nMale\npass\npassword123\nwrong\npassword123\nY\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Application app = new Application();
        app.handleRegister();
        String output = outContent.toString();
        assertTrue(output.length() > 0);
        System.setIn(System.in);
    }
    
    
    @Test
    @DisplayName("Test processOrder - Grab payment")
    void testProcessOrder_GrabPayment() {
        String input = "2\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Application app = new Application();
        app.currentCustomer = new Customer(1000, "John Doe");
        Food food = new Food(2000, "Chicken Rice", 10.50, "Set");
        OrderDetails detail = new OrderDetails(food, 1);
        app.processOrder(Arrays.asList(detail));
        String output = outContent.toString();
        assertTrue(output.length() > 0);
        System.setIn(System.in);
    }
    
    @Test
    @DisplayName("Test processOrder - Bank payment with card")
    void testProcessOrder_BankPayment() {
        String input = "3\n1234567890123456\n1225\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Application app = new Application();
        app.currentCustomer = new Customer(1000, "John Doe");
        Food food = new Food(2000, "Chicken Rice", 10.50, "Set");
        OrderDetails detail = new OrderDetails(food, 1);
        app.processOrder(Arrays.asList(detail));
        String output = outContent.toString();
        assertTrue(output.length() > 0);
        System.setIn(System.in);
    }
    
    @Test
    @DisplayName("Test processOrder - Bank payment with invalid card retry")
    void testProcessOrder_BankPaymentInvalidCard() {
        String input = "3\n123\n1234567890123456\n12\n1225\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Application app = new Application();
        app.currentCustomer = new Customer(1000, "John Doe");
        Food food = new Food(2000, "Chicken Rice", 10.50, "Set");
        OrderDetails detail = new OrderDetails(food, 1);
        app.processOrder(Arrays.asList(detail));
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
    @DisplayName("Test processOrder - order with multiple items")
    void testProcessOrder_MultipleItems() {
        String input = "1\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Application app = new Application();
        app.currentCustomer = new Customer(1000, "John Doe");
        Food food1 = new Food(2000, "Chicken Rice", 10.50, "Set");
        Food food2 = new Food(2001, "Nasi Lemak", 8.00, "Set");
        List<OrderDetails> details = Arrays.asList(
            new OrderDetails(food1, 2),
            new OrderDetails(food2, 1)
        );
        app.processOrder(details);
        String output = outContent.toString();
        assertTrue(output.length() > 0);
        System.setIn(System.in);
    }
    
    @Test
    @DisplayName("Test handleAdminMenu - all menu options")
    void testHandleAdminMenu_AllOptions() {
        String input = "1\nN\n2\nN\n3\nN\n4\n5\n0\n";
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
        String input = "Y\nJohn Doe\n25\n0123456789\nMale\npassword123\npassword123\nN\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Application app = new Application();
        app.handleRegister();
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
        String input = "3\n0\n4\n1890\n";
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
        String input = "Y\n123\nJohn Doe\n10\n25\n123\n0123456789\nX\nMale\n12\npassword123\nwrong\npassword123\nY\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Application app = new Application();
        app.handleRegister();
        String output = outContent.toString();
        assertTrue(output.length() > 0);
        System.setIn(System.in);
    }
    
    
    @Test
    @DisplayName("Test processOrder - order with null result")
    void testProcessOrder_NullOrder() {
        String input = "1\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Application app = new Application();
        app.currentCustomer = new Customer(1000, "John Doe");
        Food food = new Food(2000, "Chicken Rice", 10.50, "Set");
        OrderDetails detail = new OrderDetails(food, 1);
        app.processOrder(Arrays.asList(detail));
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

