package presentation.Food;

import model.Customer;
import model.Food;
import model.Order;
import model.PaymentMethod;
import org.junit.jupiter.api.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MenuDisplayTest {

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
    @DisplayName("Static Menu Views")
    class StaticMenuTests {
        
        @Test
        @DisplayName("Main Menu should contain login options")
        void shouldDisplayMainMenu() {
            MenuDisplay.displayMainMenu();
            String output = outputCaptor.toString();
            
            assertTrue(output.contains("JB Food Ordering System"));
            assertTrue(output.contains("Login"));
            assertTrue(output.contains("Register"));
        }

        @Test
        @DisplayName("Admin Menu should show management categories")
        void shouldDisplayAdminMenu() {
            MenuDisplay.displayAdminMenu();
            String output = outputCaptor.toString();
            
            assertTrue(output.contains("Admin"));
            assertTrue(output.contains("Food Management"));
        }

        @Test
        @DisplayName("Food Management Menu should list CRUD operations")
        void shouldDisplayFoodAdminMenu() {
            MenuDisplay.displayFoodAdminMenu();
            String output = outputCaptor.toString();
            
            assertAll("Menu Options",
                () -> assertTrue(output.contains("Register New Food")),
                () -> assertTrue(output.contains("Edit Food")),
                () -> assertTrue(output.contains("Delete Food")),
                () -> assertTrue(output.contains("View All Food")),
                () -> assertTrue(output.contains("Back to Admin Menu"))
            );
        }
    }

    @Nested
    @DisplayName("Food Table Display")
    class FoodTableTests {

        @Test
        @DisplayName("Should display formatted list of foods")
        void shouldDisplayFoodList() {
            List<Food> foods = getDummyFoods();
            
            MenuDisplay.displayAllFoods(foods);
            String output = outputCaptor.toString();

            assertAll("Table Content",
                () -> assertTrue(output.contains("Chicken Rice")),
                () -> assertTrue(output.contains("10.50")), // Check formatting
                () -> assertTrue(output.contains("Nasi Lemak")),
                () -> assertTrue(output.contains("Mee Goreng")),
                () -> assertTrue(output.contains("A la carte"))
            );
        }

        @Test
        @DisplayName("Should handle empty food list gracefully")
        void shouldDisplayHeaderOnlyForEmptyList() {
            MenuDisplay.displayAllFoods(Collections.emptyList());
            String output = outputCaptor.toString();
            
            assertTrue(output.contains("All Food Details"));
            assertFalse(output.contains("Chicken Rice"));
        }

        @Test
        @DisplayName("Should display food menu for customers")
        void shouldDisplayCustomerMenu() {
            MenuDisplay.displayFoodMenu(getDummyFoods());
            String output = outputCaptor.toString();
            
            assertTrue(output.contains("Menu"));
            assertTrue(output.contains("Exit Order"));
            assertTrue(output.contains("10.50"));
        }
    }

    @Nested
    @DisplayName("Order Reporting")
    class OrderReportTests {

        @Test
        @DisplayName("Should generate report with correct totals and formatting")
        void shouldDisplayOrderReport() {
            Order order1 = createMockOrder(1, "TNG", 21.50);
            Order order2 = createMockOrder(2, "Cash", 15.00);
            List<Order> orders = List.of(order1, order2);

            MenuDisplay.displayOrderReport(orders);
            String output = outputCaptor.toString();

            assertAll("Report Details",
                () -> assertTrue(output.contains("Order Report")),
                () -> assertTrue(output.contains("TNG")),
                () -> assertTrue(output.contains("Cash")),
                () -> assertTrue(output.contains("21.50")), // Check decimal formatting
                () -> assertTrue(output.contains("15.00"))
            );
        }

        @Test
        @DisplayName("Should display report headers even if order list is empty")
        void shouldHandleEmptyOrderList() {
            MenuDisplay.displayOrderReport(new ArrayList<>());
            String output = outputCaptor.toString();
            
            assertTrue(output.contains("Order Report"));
            assertTrue(output.contains("Order ID"));
        }
    }

    // Helper Methods to create test data
    private List<Food> getDummyFoods() {
        return List.of(
            new Food(2000, "Chicken Rice", 10.50, "Set"),
            new Food(2001, "Nasi Lemak", 8.00, "Set"),
            new Food(2002, "Mee Goreng", 7.50, "A la carte")
        );
    }

    private Order createMockOrder(int id, String paymentType, double price) {
        Customer customer = new Customer(1000 + id, "User " + id);
        PaymentMethod pm = new PaymentMethod(paymentType + "01", paymentType, "ref", 100.0);
        Order order = new Order(new Date(), customer, new ArrayList<>(), price, pm);
        order.setOrderId(id);
        return order;
    }
}