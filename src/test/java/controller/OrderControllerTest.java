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


 //Tests for OrderController
 
public class OrderControllerTest {

    private OrderController controller;
    private IOrderService serviceMock;

    @BeforeEach
    void setUp() {
        serviceMock = mock(IOrderService.class);
        controller = new OrderController(serviceMock);
    }

    // --- createOrder ---
    @Test
    @DisplayName("Create order successfully")
    void createOrderSuccess() {
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

        when(serviceMock.createOrder(1000, details, "TNG", null, null)).thenReturn(order);

        Order result = controller.createOrder(1000, details, "TNG", null, null);

        assertNotNull(result);
        assertEquals(1, result.getOrderId());
        verify(serviceMock).createOrder(1000, details, "TNG", null, null);
    }

    @Test
    @DisplayName("Fail to create order for invalid customer")
    void createOrderFailure() {
        List<OrderDetails> details = new ArrayList<>();
        when(serviceMock.createOrder(9999, details, "TNG", null, null))
            .thenThrow(new IllegalArgumentException("Customer not found"));

        Order result = controller.createOrder(9999, details, "TNG", null, null);

        assertNull(result);
        verify(serviceMock).createOrder(9999, details, "TNG", null, null);
    }

    // --- getAllOrders ---
    @Test
    @DisplayName("Get all orders")
    void getAllOrders() {
        List<Order> orders = Arrays.asList(new Order(), new Order());
        when(serviceMock.getAllOrders()).thenReturn(orders);

        List<Order> result = controller.getAllOrders();

        assertEquals(2, result.size());
        verify(serviceMock).getAllOrders();
    }

    // --- getOrdersByCustomerId ---
    @Test
    @DisplayName("Get orders by customer ID")
    void getOrdersByCustomerId() {
        List<Order> orders = Arrays.asList(new Order());
        when(serviceMock.getOrdersByCustomerId(1000)).thenReturn(orders);

        List<Order> result = controller.getOrdersByCustomerId(1000);

        assertEquals(1, result.size());
        verify(serviceMock).getOrdersByCustomerId(1000);
    }

    // --- calculateTotalPrice ---
    @Test
    @DisplayName("Calculate total price for order details")
    void calculateTotalPrice() {
        Food food = new Food(2000, "Food", 10.00, "Set");
        OrderDetails detail = new OrderDetails(food, 2);
        List<OrderDetails> details = Arrays.asList(detail);

        when(serviceMock.calculateTotalPrice(details)).thenReturn(20.00);

        double total = controller.calculateTotalPrice(details);

        assertEquals(20.00, total, 0.01);
        verify(serviceMock).calculateTotalPrice(details);
    }
}
