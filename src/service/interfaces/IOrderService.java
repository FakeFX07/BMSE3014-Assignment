package service.interfaces;

import model.Order;
import model.OrderDetails;
import java.util.List;

/**
 * Order Service Interface
 * Defines contract for order business operations
 * Follows SOLID: Interface Segregation Principle, Dependency Inversion Principle
 */
public interface IOrderService {
    
    /**
     * Create a new order
     * 
     * @param customerId Customer ID
     * @param orderDetailsList List of order details
     * @param paymentType Payment type
     * @param cardNumber Card number (for Bank, null for others)
     * @param expiryDate Expiry date (for Bank, null for others)
     * @return Created order
     * @throws IllegalArgumentException if order creation fails
     */
    Order createOrder(int customerId, List<OrderDetails> orderDetailsList, 
                     String paymentType, String cardNumber, String expiryDate) throws IllegalArgumentException;
    
    /**
     * Get all orders
     * 
     * @return List of all orders
     */
    List<Order> getAllOrders();
    
    /**
     * Get orders by customer ID
     * 
     * @param customerId Customer ID
     * @return List of orders
     */
    List<Order> getOrdersByCustomerId(int customerId);
    
    /**
     * Calculate total price for order details
     * 
     * @param orderDetailsList List of order details
     * @return Total price
     */
    double calculateTotalPrice(List<OrderDetails> orderDetailsList);
}
