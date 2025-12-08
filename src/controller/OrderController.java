package controller;     

import model.Order;
import model.OrderDetails;
import service.interfaces.IOrderService;

import java.util.List;

/**
 * Order Controller
 * Handles order-related user interactions
 * Follows SOLID: Single Responsibility Principle, Dependency Inversion Principle
 */
public class OrderController {
    
    private final IOrderService orderService;
    
    public OrderController(IOrderService orderService) {
        this.orderService = orderService;
    }
    
    /**
     * Create a new order
     * 
     * @param customerId Customer ID
     * @param orderDetailsList List of order details
     * @param paymentType Payment type
     * @param cardNumber Card number (for Bank, null for others)
     * @param expiryDate Expiry date (for Bank, null for others)
     * @return Created order if successful, null if creation fails
     */
    public Order createOrder(int customerId, List<OrderDetails> orderDetailsList, 
                           String paymentType, String cardNumber, String expiryDate) {
        try {
            return orderService.createOrder(customerId, orderDetailsList, paymentType, cardNumber, expiryDate);
        } catch (IllegalArgumentException e) {
            System.out.println("Order creation failed: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Get all orders
     * 
     * @return List of all orders
     */
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }
    
    /**
     * Get orders by customer ID
     * 
     * @param customerId Customer ID
     * @return List of orders
     */
    public List<Order> getOrdersByCustomerId(int customerId) {
        return orderService.getOrdersByCustomerId(customerId);
    }
    
    /**
     * Calculate total price for order details
     * 
     * @param orderDetailsList List of order details
     * @return Total price
     */
    public double calculateTotalPrice(List<OrderDetails> orderDetailsList) {
        return orderService.calculateTotalPrice(orderDetailsList);
    }
}
