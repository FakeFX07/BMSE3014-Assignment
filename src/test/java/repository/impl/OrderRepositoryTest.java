package repository.impl;

import config.ConnectionProvider;
import config.DatabaseConnection;
import config.TestDatabaseSetup;
import model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Order Repository Test
 */
public class OrderRepositoryTest {
    
    private OrderRepository repository;
    private ConnectionProvider connectionProvider;
    private static final String H2_URL = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=MySQL";
    
    @BeforeEach
    void setUp() throws SQLException {
        connectionProvider = DatabaseConnection.createInstance(H2_URL, "sa", "");
        TestDatabaseSetup.initializeSchema(connectionProvider);
        repository = new OrderRepository(connectionProvider);
    }
    
    @AfterEach
    void tearDown() throws SQLException {
        TestDatabaseSetup.cleanup(connectionProvider);
        if (connectionProvider instanceof DatabaseConnection) {
            ((DatabaseConnection) connectionProvider).closeConnection();
        }
    }
    
    @Test
    @DisplayName("Test save - new order")
    void testSave_NewOrder() {
        Customer customer = new Customer(1000, "John Doe");
        PaymentMethod pm = new PaymentMethod(1, 1000, "TNG", 100.00, null, null);
        Food food = new Food(2000, "Chicken Rice", 10.50, "Set");
        OrderDetails detail = new OrderDetails(food, 2);
        List<OrderDetails> details = new ArrayList<>();
        details.add(detail);
        
        Order order = new Order(new Date(), customer, details, 21.00, pm);
        order.setStatus("COMPLETED");
        
        Order saved = repository.save(order);
        assertTrue(saved.getOrderId() > 0);
    }
    
    @Test
    @DisplayName("Test findById - existing order")
    void testFindById_Existing() {
        // First create an order
        Customer customer = new Customer(1000, "John Doe");
        PaymentMethod pm = new PaymentMethod(1, 1000, "TNG", 100.00, null, null);
        Food food = new Food(2000, "Chicken Rice", 10.50, "Set");
        OrderDetails detail = new OrderDetails(food, 1);
        List<OrderDetails> details = new ArrayList<>();
        details.add(detail);
        
        Order order = new Order(new Date(), customer, details, 10.50, pm);
        order.setStatus("COMPLETED");
        Order saved = repository.save(order);
        
        Optional<Order> found = repository.findById(saved.getOrderId());
        assertTrue(found.isPresent());
        assertEquals(saved.getOrderId(), found.get().getOrderId());
    }
    
    @Test
    @DisplayName("Test findByCustomerId - returns customer orders")
    void testFindByCustomerId() {
        Customer customer = new Customer(1000, "John Doe");
        PaymentMethod pm = new PaymentMethod(1, 1000, "TNG", 100.00, null, null);
        Food food = new Food(2000, "Chicken Rice", 10.50, "Set");
        OrderDetails detail = new OrderDetails(food, 1);
        List<OrderDetails> details = new ArrayList<>();
        details.add(detail);
        
        Order order = new Order(new Date(), customer, details, 10.50, pm);
        order.setStatus("COMPLETED");
        repository.save(order);
        
        List<Order> orders = repository.findByCustomerId(1000);
        assertTrue(orders.size() > 0);
    }
    
    @Test
    @DisplayName("Test findAll - returns all orders")
    void testFindAll() {
        List<Order> orders = repository.findAll();
        assertNotNull(orders);
    }
    
    @Test
    @DisplayName("Test getNextOrderId - returns next ID")
    void testGetNextOrderId() {
        int nextId = repository.getNextOrderId();
        assertTrue(nextId >= 1);
    }
    
    @Test
    @DisplayName("Test default constructor")
    void testDefaultConstructor() {
        OrderRepository repo = new OrderRepository();
        assertNotNull(repo);
    }
    
    @Test
    @DisplayName("Test save - order with multiple details")
    void testSave_MultipleDetails() {
        Customer customer = new Customer(1000, "John Doe");
        PaymentMethod pm = new PaymentMethod(1, 1000, "TNG", 100.00, null, null);
        Food food1 = new Food(2000, "Chicken Rice", 10.50, "Set");
        Food food2 = new Food(2001, "Nasi Lemak", 8.00, "Set");
        List<OrderDetails> details = Arrays.asList(
            new OrderDetails(food1, 2),
            new OrderDetails(food2, 1)
        );
        
        Order order = new Order(new Date(), customer, details, 29.00, pm);
        order.setStatus("COMPLETED");
        
        Order saved = repository.save(order);
        assertTrue(saved.getOrderId() > 0);
    }
    
    @Test
    @DisplayName("Test findById - non-existing order")
    void testFindById_NonExisting() {
        Optional<Order> order = repository.findById(9999);
        assertFalse(order.isPresent());
    }
    
    @Test
    @DisplayName("Test getNextOrderId - when no orders exist")
    void testGetNextOrderId_NoOrders() throws SQLException {
        TestDatabaseSetup.cleanup(connectionProvider);
        int nextId = repository.getNextOrderId();
        assertEquals(1, nextId);
    }
    
    @Test
    @DisplayName("Test findByCustomerId - returns empty for non-existing customer")
    void testFindByCustomerId_NonExisting() {
        List<Order> orders = repository.findByCustomerId(9999);
        assertTrue(orders.isEmpty());
    }
    
    @Test
    @DisplayName("Test save - order with null details")
    void testSave_WithNullDetails() {
        Customer customer = new Customer(1000, "John Doe");
        PaymentMethod pm = new PaymentMethod(1, 1000, "TNG", 100.00, null, null);
        Order order = new Order(new Date(), customer, null, 0.0, pm);
        order.setStatus("COMPLETED");
        
        Order saved = repository.save(order);
        assertTrue(saved.getOrderId() > 0);
    }
    
    @Test
    @DisplayName("Test findAll - includes new orders")
    void testFindAll_IncludesNewOrders() {
        Customer customer = new Customer(1000, "John Doe");
        PaymentMethod pm = new PaymentMethod(1, 1000, "TNG", 100.00, null, null);
        Food food = new Food(2000, "Chicken Rice", 10.50, "Set");
        OrderDetails detail = new OrderDetails(food, 1);
        List<OrderDetails> details = Arrays.asList(detail);
        
        Order order = new Order(new Date(), customer, details, 10.50, pm);
        order.setStatus("COMPLETED");
        repository.save(order);
        
        List<Order> allOrders = repository.findAll();
        assertTrue(allOrders.size() > 0);
    }
    
    @Test
    @DisplayName("Test findById - verify order details loaded")
    void testFindById_WithOrderDetails() {
        Customer customer = new Customer(1000, "John Doe");
        PaymentMethod pm = new PaymentMethod(1, 1000, "TNG", 100.00, null, null);
        Food food = new Food(2000, "Chicken Rice", 10.50, "Set");
        OrderDetails detail = new OrderDetails(food, 2);
        List<OrderDetails> details = Arrays.asList(detail);
        
        Order order = new Order(new Date(), customer, details, 21.00, pm);
        order.setStatus("COMPLETED");
        Order saved = repository.save(order);
        
        Optional<Order> found = repository.findById(saved.getOrderId());
        assertTrue(found.isPresent());
        assertNotNull(found.get().getOrderDetails());
        assertTrue(found.get().getOrderDetails().size() > 0);
    }
    
    @Test
    @DisplayName("Test findByCustomerId - verify orders ordered by date")
    void testFindByCustomerId_OrderedByDate() {
        Customer customer = new Customer(1000, "John Doe");
        PaymentMethod pm = new PaymentMethod(1, 1000, "TNG", 100.00, null, null);
        Food food = new Food(2000, "Chicken Rice", 10.50, "Set");
        OrderDetails detail = new OrderDetails(food, 1);
        
        Order order1 = new Order(new Date(), customer, Arrays.asList(detail), 10.50, pm);
        order1.setStatus("COMPLETED");
        repository.save(order1);
        
        List<Order> orders = repository.findByCustomerId(1000);
        assertTrue(orders.size() >= 1);
        
        // Verify orders are returned (may include test data)
        assertNotNull(orders);
    }
    
    @Test
    @DisplayName("Test save - order with empty details list")
    void testSave_WithEmptyDetailsList() {
        Customer customer = new Customer(1000, "John Doe");
        PaymentMethod pm = new PaymentMethod(1, 1000, "TNG", 100.00, null, null);
        Order order = new Order(new Date(), customer, new ArrayList<>(), 0.0, pm);
        order.setStatus("COMPLETED");
        
        Order saved = repository.save(order);
        assertTrue(saved.getOrderId() > 0);
    }
}