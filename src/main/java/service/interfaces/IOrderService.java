package service.interfaces;

import java.util.List;

import model.Order;
import model.OrderDetails;

public interface IOrderService {
    
    Order createOrder(int customerId, List<OrderDetails> orderDetailsList, 
                     String paymentType, String identifier, String password) throws IllegalArgumentException;
    
    /**
     * Get all orders
     */
    List<Order> getAllOrders();
    
    /**
     * Get orders by customer ID
     */
    List<Order> getOrdersByCustomerId(int customerId);
    
    /**
     * Calculate total price for order details
     */
    double calculateTotalPrice(List<OrderDetails> orderDetailsList);
}
