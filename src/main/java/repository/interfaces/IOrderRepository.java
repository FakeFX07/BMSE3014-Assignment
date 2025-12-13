package repository.interfaces;

import java.util.List;
import java.util.Optional;

import model.Order;

public interface IOrderRepository {
    
    //Find order by ID
    Optional<Order> findById(int orderId);
    
    //Find orders by customer ID
    List<Order> findByCustomerId(int customerId);
    
    //Find all orders
    List<Order> findAll();
    
    //Save order (create)
    Order save(Order order);
    
    //Get next available order ID
    int getNextOrderId();
}
