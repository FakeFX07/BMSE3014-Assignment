package main.java.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import main.java.model.*;
import main.java.repository.interfaces.ICustomerRepository;
import main.java.repository.interfaces.IOrderRepository;
import main.java.repository.interfaces.IPaymentMethodRepository;
import main.java.service.interfaces.IOrderService;
import main.java.service.interfaces.IPaymentService;

/**
 * Order Service Implementation
 * Contains business logic for order operations
 * Follows SOLID: Single Responsibility Principle, Dependency Inversion Principle
 */
public class OrderService implements IOrderService {
    
    private final IOrderRepository orderRepository;
    private final ICustomerRepository customerRepository;
    private final IPaymentMethodRepository paymentMethodRepository;
    private final IPaymentService paymentService;
    
    public OrderService(IOrderRepository orderRepository, 
                       ICustomerRepository customerRepository,
                       IPaymentMethodRepository paymentMethodRepository,
                       IPaymentService paymentService) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.paymentMethodRepository = paymentMethodRepository;
        this.paymentService = paymentService;
    }
    
    @Override
    public Order createOrder(int customerId, List<OrderDetails> orderDetailsList, 
                            String paymentType, String cardNumber, String expiryDate) throws IllegalArgumentException {
        // Validate customer exists
        Optional<Customer> customerOpt = customerRepository.findById(customerId);
        if (customerOpt.isEmpty()) {
            throw new IllegalArgumentException("Customer not found");
        }
        Customer customer = customerOpt.get();
        
        // Validate order details
        if (orderDetailsList == null || orderDetailsList.isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item");
        }
        
        // Calculate total price
        double totalPrice = calculateTotalPrice(orderDetailsList);
        
        // Get payment method
        Optional<PaymentMethod> paymentMethodOpt = paymentMethodRepository.findByCustomerIdAndType(customerId, paymentType);
        if (paymentMethodOpt.isEmpty()) {
            throw new IllegalArgumentException("Payment method not found");
        }
        PaymentMethod paymentMethod = paymentMethodOpt.get();
        
        // Process payment
        try {
            paymentService.processPayment(customerId, paymentType, totalPrice, cardNumber, expiryDate);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Payment failed: " + e.getMessage());
        }
        
        // Create order
        Order order = new Order(new Date(), customer, orderDetailsList, totalPrice, paymentMethod);
        order.setStatus("COMPLETED");
        
        // Save order
        return orderRepository.save(order);
    }
    
    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
    
    @Override
    public List<Order> getOrdersByCustomerId(int customerId) {
        return orderRepository.findByCustomerId(customerId);
    }
    
    @Override
    public double calculateTotalPrice(List<OrderDetails> orderDetailsList) {
        double total = 0.0;
        for (OrderDetails detail : orderDetailsList) {
            total += detail.getSubtotal();
        }
        return total;
    }
}
