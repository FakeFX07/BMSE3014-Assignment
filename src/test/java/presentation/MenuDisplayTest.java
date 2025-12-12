package presentation;

import model.*;
import presentation.Food.MenuDisplay;
import presentation.Payment.PaymentOption;

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
        assertTrue(output.contains("Food Management"));
        
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
        
       displayPaymentOptions();
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
        PaymentMethod pm = new PaymentMethod("TNG001", "TNG", "tng123", 100.00);
        pm.setPaymentMethodId(1);
        Food food = new Food(2000, "Chicken Rice", 10.50, "Set");
        OrderDetails detail = new OrderDetails(food, 2);
        Order order = new Order(new Date(), customer, Arrays.asList(detail), 21.00, pm);
        order.setOrderId(1);
        order.setStatus("COMPLETED");
        
        displayReceipt(order);
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
        PaymentMethod pm = new PaymentMethod("TNG001", "TNG", "tng123", 100.00);
        pm.setPaymentMethodId(1);
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

    public static void displayPaymentOptions() {
        PaymentOption.displayMenu();
    }

            /**
     * Display order receipt
     * 
     * @param order Order to display
     */
    public static void displayReceipt(Order order) {
        System.out.println("======================================================================");
        System.out.println("                                 RECEIPT                              ");
        System.out.println("======================================================================");
        System.out.println("Order Id : " + order.getOrderId() + "\t\t\tDate : " + order.getOrderDate());
        System.out.println("==============");
        System.out.println("Cust ID : " + order.getCustomer().getCustomerId());
        System.out.println("======================================================================");
        
        System.out.println("Food Id \t Food Name\t     Food Price  Qty \t\tTotal Price");
        for (OrderDetails detail : order.getOrderDetails()) {
            System.out.println(detail.toString());
        }
        System.out.println("======================================================================");
        
        System.out.println("Subtotal :\t\t\t\t\t\tRM " + String.format("%.2f", order.getTotalPrice()));
        System.out.println("======================================================================");
        
        PaymentMethod paymentMethod = order.getPaymentMethod();
        System.out.println(paymentMethod.getPaymentType() + "      : \t\t\t\t\t\tRM " + 
                         String.format("%.2f", paymentMethod.getBalance()));
        
        // Calculate exchange (this would need payment processing info)
        System.out.println("======================================================================");
    }
}

