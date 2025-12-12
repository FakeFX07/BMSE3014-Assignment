package presentation.Food;

import model.Food;
import model.Order;
import model.Customer;
import model.PaymentMethod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test class for MenuDisplay
 * Aims for 100% code coverage of Food module display methods
 */
public class MenuDisplayTest {
    
    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;
    
    @BeforeEach
    void setUp() {
        outputStream = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
    }
    
    @Test
    @DisplayName("displayMainMenu - Should display main menu")
    void testDisplayMainMenu() {
        MenuDisplay.displayMainMenu();
        String output = outputStream.toString();
        
        assertTrue(output.contains("JB Food Ordering System"));
        assertTrue(output.contains("Login"));
        assertTrue(output.contains("Register"));
        
        System.setOut(originalOut);
    }
    
    @Test
    @DisplayName("displayAdminMenu - Should display admin menu")
    void testDisplayAdminMenu() {
        MenuDisplay.displayAdminMenu();
        String output = outputStream.toString();
        
        assertTrue(output.contains("Admin"));
        assertTrue(output.contains("Food Management"));
        
        System.setOut(originalOut);
    }
    
    @Test
    @DisplayName("displayFoodAdminMenu - Should display food management menu")
    void testDisplayFoodAdminMenu() {
        MenuDisplay.displayFoodAdminMenu();
        String output = outputStream.toString();
        
        assertTrue(output.contains("Food Management"));
        assertTrue(output.contains("Register New Food"));
        assertTrue(output.contains("Edit Food"));
        assertTrue(output.contains("Delete Food"));
        assertTrue(output.contains("View All Food"));
        assertTrue(output.contains("Back to Admin Menu"));
        
        System.setOut(originalOut);
    }
    
    @Test
    @DisplayName("displayFoodMenu - With multiple foods - Should display all")
    void testDisplayFoodMenu_MultipleFoods() {
        List<Food> foods = Arrays.asList(
            new Food(2000, "Chicken Rice", 10.50, "Set"),
            new Food(2001, "Nasi Lemak", 8.00, "Set"),
            new Food(2002, "Mee Goreng", 7.50, "A la carte")
        );
        
        MenuDisplay.displayFoodMenu(foods);
        String output = outputStream.toString();
        
        assertTrue(output.contains("Menu"));
        assertTrue(output.contains("Chicken Rice"));
        assertTrue(output.contains("10.5"));
        assertTrue(output.contains("Nasi Lemak"));
        assertTrue(output.contains("8.0"));
        assertTrue(output.contains("Mee Goreng"));
        assertTrue(output.contains("7.5"));
        assertTrue(output.contains("Exit Order"));
        
        System.setOut(originalOut);
    }
    
    @Test
    @DisplayName("displayFoodMenu - With empty list - Should display headers only")
    void testDisplayFoodMenu_EmptyList() {
        List<Food> foods = new ArrayList<>();
        
        MenuDisplay.displayFoodMenu(foods);
        String output = outputStream.toString();
        
        assertTrue(output.contains("Menu"));
        assertTrue(output.contains("Exit Order"));
        
        System.setOut(originalOut);
    }
    
    @Test
    @DisplayName("displayOrderReport - With multiple orders - Should display all")
    void testDisplayOrderReport_MultipleOrders() {
        Customer customer1 = new Customer(1000, "John Doe");
        Customer customer2 = new Customer(1001, "Jane Smith");
        PaymentMethod pm1 = new PaymentMethod("TNG001", "TNG", "tng123", 100.00);
        pm1.setPaymentMethodId(1);
        PaymentMethod pm2 = new PaymentMethod("CASH001", "Cash", "cash123", 50.00);
        pm2.setPaymentMethodId(2);
        
        Order order1 = new Order(new Date(), customer1, new ArrayList<>(), 21.00, pm1);
        order1.setOrderId(1);
        
        Order order2 = new Order(new Date(), customer2, new ArrayList<>(), 15.00, pm2);
        order2.setOrderId(2);
        
        List<Order> orders = Arrays.asList(order1, order2);
        
        MenuDisplay.displayOrderReport(orders);
        String output = outputStream.toString();
        
        assertTrue(output.contains("Order Report"));
        assertTrue(output.contains("Order ID"));
        assertTrue(output.contains("Customer Id"));
        assertTrue(output.contains("Payment Method"));
        assertTrue(output.contains("Total Price"));
        assertTrue(output.contains("TNG"));
        assertTrue(output.contains("Cash"));
        assertTrue(output.contains("21.00"));
        assertTrue(output.contains("15.00"));
        
        System.setOut(originalOut);
    }
    
    @Test
    @DisplayName("displayOrderReport - With single order - Should display correctly")
    void testDisplayOrderReport_SingleOrder() {
        Customer customer = new Customer(1000, "John Doe");
        PaymentMethod pm = new PaymentMethod("TNG001", "TNG", "tng123", 100.00);
        pm.setPaymentMethodId(1);
        Order order = new Order(new Date(), customer, new ArrayList<>(), 21.00, pm);
        order.setOrderId(1);
        
        MenuDisplay.displayOrderReport(Arrays.asList(order));
        String output = outputStream.toString();
        
        assertTrue(output.contains("Order Report"));
        assertTrue(output.contains("1"));
        assertTrue(output.contains("1000"));
        assertTrue(output.contains("TNG"));
        
        System.setOut(originalOut);
    }
    
    @Test
    @DisplayName("displayOrderReport - With empty list - Should display headers only")
    void testDisplayOrderReport_EmptyList() {
        MenuDisplay.displayOrderReport(new ArrayList<>());
        String output = outputStream.toString();
        
        assertTrue(output.contains("Order Report"));
        assertTrue(output.contains("Order ID"));
        
        System.setOut(originalOut);
    }
    
    @Test
    @DisplayName("displayAllFoods - With multiple foods - Should display in table format")
    void testDisplayAllFoods_MultipleFoods() {
        List<Food> foods = Arrays.asList(
            new Food(2000, "Chicken Rice", 10.50, "Set"),
            new Food(2001, "Nasi Lemak", 8.00, "Set"),
            new Food(2002, "Mee Goreng", 7.50, "A la carte")
        );
        
        MenuDisplay.displayAllFoods(foods);
        String output = outputStream.toString();
        
        assertTrue(output.contains("All Food Details"));
        assertTrue(output.contains("No"));
        assertTrue(output.contains("Food Id"));
        assertTrue(output.contains("Food Name"));
        assertTrue(output.contains("Food Price"));
        assertTrue(output.contains("Food Type"));
        assertTrue(output.contains("Chicken Rice"));
        assertTrue(output.contains("10.50"));
        assertTrue(output.contains("Nasi Lemak"));
        assertTrue(output.contains("8.00"));
        assertTrue(output.contains("Mee Goreng"));
        assertTrue(output.contains("7.50"));
        assertTrue(output.contains("A la carte"));
        
        System.setOut(originalOut);
    }
    
    @Test
    @DisplayName("displayAllFoods - With single food - Should display correctly")
    void testDisplayAllFoods_SingleFood() {
        List<Food> foods = Arrays.asList(
            new Food(2000, "Chicken Rice", 10.50, "Set")
        );
        
        MenuDisplay.displayAllFoods(foods);
        String output = outputStream.toString();
        
        assertTrue(output.contains("1"));
        assertTrue(output.contains("2000"));
        assertTrue(output.contains("Chicken Rice"));
        assertTrue(output.contains("10.50"));
        assertTrue(output.contains("Set"));
        
        System.setOut(originalOut);
    }
    
    @Test
    @DisplayName("displayAllFoods - With empty list - Should display headers only")
    void testDisplayAllFoods_EmptyList() {
        MenuDisplay.displayAllFoods(new ArrayList<>());
        String output = outputStream.toString();
        
        assertTrue(output.contains("All Food Details"));
        assertTrue(output.contains("No"));
        assertTrue(output.contains("Food Id"));
        
        System.setOut(originalOut);
    }
    
    @Test
    @DisplayName("displayAllFoods - Should increment index correctly")
    void testDisplayAllFoods_IndexIncrement() {
        List<Food> foods = Arrays.asList(
            new Food(2000, "Food A", 10.00, "Set"),
            new Food(2001, "Food B", 11.00, "Set"),
            new Food(2002, "Food C", 12.00, "Set")
        );
        
        MenuDisplay.displayAllFoods(foods);
        String output = outputStream.toString();
        
        // Check that indices 1, 2, 3 appear in the output
        assertTrue(output.contains("1"));
        assertTrue(output.contains("2"));
        assertTrue(output.contains("3"));
        
        System.setOut(originalOut);
    }
    
    @Test
    @DisplayName("displayFoodAdminMenu - Should skip code 0 in loop")
    void testDisplayFoodAdminMenu_SkipsExitInLoop() {
        MenuDisplay.displayFoodAdminMenu();
        String output = outputStream.toString();
        
        // Verify that 0.Back to Admin Menu appears after the loop
        int backToAdminIndex = output.indexOf("0.Back to Admin Menu");
        int registerIndex = output.indexOf("1.Register New Food");
        
        // "Back to Admin Menu" should appear after the other options
        assertTrue(backToAdminIndex > registerIndex);
        
        System.setOut(originalOut);
    }
    
    @Test
    @DisplayName("displayOrderReport - Should format prices with 2 decimals")
    void testDisplayOrderReport_FormattedPrices() {
        Customer customer = new Customer(1000, "John Doe");
        PaymentMethod pm = new PaymentMethod("TNG001", "TNG", "tng123", 100.00);
        pm.setPaymentMethodId(1);
        Order order = new Order(new Date(), customer, new ArrayList<>(), 21.5, pm);
        order.setOrderId(1);
        
        MenuDisplay.displayOrderReport(Arrays.asList(order));
        String output = outputStream.toString();
        
        assertTrue(output.contains("21.50"));
        
        System.setOut(originalOut);
    }
    
    @Test
    @DisplayName("displayAllFoods - Should format prices with 2 decimals")
    void testDisplayAllFoods_FormattedPrices() {
        List<Food> foods = Arrays.asList(
            new Food(2000, "Food", 10.5, "Set")
        );
        
        MenuDisplay.displayAllFoods(foods);
        String output = outputStream.toString();
        
        assertTrue(output.contains("10.50"));
        
        System.setOut(originalOut);
    }
}

