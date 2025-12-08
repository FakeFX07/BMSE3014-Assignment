package controller;

import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import service.interfaces.IOrderService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Order Controller Test
 */
public class OrderControllerTest {
    
    private OrderController controller;
    private IOrderService mockService;
    
    @BeforeEach
    void setUp() {
        mockService = mock(IOrderService.class);
        controller = new OrderController(mockService);
    }
    
    @Test
    @DisplayName("Test createOrder - success")
    void testCreateOrder_Success() {
        Customer customer = new Customer(1000, "John Doe");
        Food food = new Food(2000, "Chicken Rice", 10.50, "Set");
        OrderDetails detail = new OrderDetails(food, 2);
        List<OrderDetails> details = new ArrayList<>();
        details.add(detail);
        
        Order order = new Order();
        order.setOrderId(1);
        order.setCustomer(customer);
        order.setTotalPrice(21.00);
        order.setStatus("COMPLETED");
        
        when(mockService.createOrder(1000, details, "TNG", null, null)).thenReturn(order);
        
        Order result = controller.createOrder(1000, details, "TNG", null, null);
        assertNotNull(result);
        assertEquals(1, result.getOrderId());
    }
    
    @Test
    @DisplayName("Test createOrder - failure")
    void testCreateOrder_Failure() {
        List<OrderDetails> details = new ArrayList<>();
        when(mockService.createOrder(9999, details, "TNG", null, null))
            .thenThrow(new IllegalArgumentException("Customer not found"));
        
        Order result = controller.createOrder(9999, details, "TNG", null, null);
        assertNull(result);
    }
    
    @Test
    @DisplayName("Test getAllOrders - returns list")
    void testGetAllOrders() {
        List<Order> orders = Arrays.asList(new Order(), new Order());
        when(mockService.getAllOrders()).thenReturn(orders);
        
        List<Order> result = controller.getAllOrders();
        assertEquals(2, result.size());
    }
    
    @Test
    @DisplayName("Test getOrdersByCustomerId - returns list")
    void testGetOrdersByCustomerId() {
        List<Order> orders = Arrays.asList(new Order());
        when(mockService.getOrdersByCustomerId(1000)).thenReturn(orders);
        
        List<Order> result = controller.getOrdersByCustomerId(1000);
        assertEquals(1, result.size());
    }
    
    @Test
    @DisplayName("Test calculateTotalPrice - delegates to service")
    void testCalculateTotalPrice() {
        Food food = new Food(2000, "Food", 10.00, "Set");
        OrderDetails detail = new OrderDetails(food, 2);
        List<OrderDetails> details = Arrays.asList(detail);
        
        when(mockService.calculateTotalPrice(details)).thenReturn(20.00);
        assertEquals(20.00, controller.calculateTotalPrice(details), 0.01);
    }
}

