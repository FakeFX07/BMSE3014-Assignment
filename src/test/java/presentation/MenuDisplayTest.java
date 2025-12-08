package presentation;

import model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Menu Display Test
 */
public class MenuDisplayTest {
    
    @Test
    @DisplayName("Test displayMainMenu - prints menu")
    void testDisplayMainMenu() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        
        MenuDisplay.displayMainMenu();
        String output = out.toString();
        
        assertTrue(output.contains("JB Food Ordering System"));
        assertTrue(output.contains("Login"));
        assertTrue(output.contains("Register"));
        
        System.setOut(System.out);
    }
    
    @Test
    @DisplayName("Test displayAdminMenu - prints admin menu")
    void testDisplayAdminMenu() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        
        MenuDisplay.displayAdminMenu();
        String output = out.toString();
        
        assertTrue(output.contains("Admin"));
        assertTrue(output.contains("Register New Food"));
        
        System.setOut(System.out);
    }
    
    @Test
    @DisplayName("Test displayFoodMenu - displays foods")
    void testDisplayFoodMenu() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        
        List<Food> foods = Arrays.asList(
            new Food(2000, "Chicken Rice", 10.50, "Set"),
            new Food(2001, "Nasi Lemak", 8.00, "Set")
        );
        
        MenuDisplay.displayFoodMenu(foods);
        String output = out.toString();
        
        assertTrue(output.contains("Chicken Rice"));
        assertTrue(output.contains("10.5"));
        
        System.setOut(System.out);
    }
    
    @Test
    @DisplayName("Test displayPaymentOptions - prints payment options")
    void testDisplayPaymentOptions() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        
        MenuDisplay.displayPaymentOptions();
        String output = out.toString();
        
        assertTrue(output.contains("Payment"));
        assertTrue(output.contains("TNG"));
        
        System.setOut(System.out);
    }
    
    @Test
    @DisplayName("Test displayReceipt - displays receipt")
    void testDisplayReceipt() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        
        Customer customer = new Customer(1000, "John Doe");
        PaymentMethod pm = new PaymentMethod(1, 1000, "TNG", 100.00, null, null);
        Food food = new Food(2000, "Chicken Rice", 10.50, "Set");
        OrderDetails detail = new OrderDetails(food, 2);
        Order order = new Order(new Date(), customer, Arrays.asList(detail), 21.00, pm);
        order.setOrderId(1);
        order.setStatus("COMPLETED");
        
        MenuDisplay.displayReceipt(order);
        String output = out.toString();
        
        assertTrue(output.contains("RECEIPT"));
        assertTrue(output.contains("Chicken Rice"));
        
        System.setOut(System.out);
    }
    
    @Test
    @DisplayName("Test displayOrderReport - displays report")
    void testDisplayOrderReport() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        
        Customer customer = new Customer(1000, "John Doe");
        PaymentMethod pm = new PaymentMethod(1, 1000, "TNG", 100.00, null, null);
        Order order = new Order(new Date(), customer, new java.util.ArrayList<>(), 21.00, pm);
        order.setOrderId(1);
        
        MenuDisplay.displayOrderReport(Arrays.asList(order));
        String output = out.toString();
        
        assertTrue(output.contains("Order Report"));
        
        System.setOut(System.out);
    }
    
    @Test
    @DisplayName("Test displayAllFoods - displays all foods")
    void testDisplayAllFoods() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        
        List<Food> foods = Arrays.asList(
            new Food(2000, "Chicken Rice", 10.50, "Set")
        );
        
        MenuDisplay.displayAllFoods(foods);
        String output = out.toString();
        
        assertTrue(output.contains("All Food Details"));
        assertTrue(output.contains("Chicken Rice"));
        
        System.setOut(System.out);
    }
}

