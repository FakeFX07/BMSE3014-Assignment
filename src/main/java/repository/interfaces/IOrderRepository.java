package main.java.repository.interfaces;

import java.util.List;
import java.util.Optional;

import main.java.model.Order;

/**
 * Order Repository Interface
 * Defines contract for order data access operations
 * Follows SOLID: Interface Segregation Principle, Dependency Inversion Principle
 */
public interface IOrderRepository {
    
    /**
     * Find order by ID
     * 
     * @param orderId Order ID
     * @return Optional containing order if found
     */
    Optional<Order> findById(int orderId);
    
    /**
     * Find orders by customer ID
     * 
     * @param customerId Customer ID
     * @return List of orders
     */
    List<Order> findByCustomerId(int customerId);
    
    /**
     * Find all orders
     * 
     * @return List of all orders
     */
    List<Order> findAll();
    
    /**
     * Save order (create)
     * 
     * @param order Order to save
     * @return Saved order with generated ID
     */
    Order save(Order order);
    
    /**
     * Get next available order ID
     * 
     * @return Next order ID
     */
    int getNextOrderId();
}
