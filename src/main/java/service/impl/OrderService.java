package service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import model.*;
import repository.interfaces.ICustomerRepository;
import repository.interfaces.IFoodRepository;
import repository.interfaces.IOrderRepository;
import repository.interfaces.IPaymentMethodRepository;
import service.interfaces.IOrderService;
import service.interfaces.IPaymentService;

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
    private final IFoodRepository foodRepository;

    public OrderService(IOrderRepository orderRepository, 
                            ICustomerRepository customerRepository, 
                            IPaymentMethodRepository paymentMethodRepository, 
                            IPaymentService paymentService,
                            IFoodRepository foodRepository) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.paymentMethodRepository = paymentMethodRepository;
        this.paymentService = paymentService;
        this.foodRepository = foodRepository;
    }

    @Override
    public Order createOrder(int customerId, List<OrderDetails> orderDetailsList, 
                             String paymentType, String identifier, String password) throws IllegalArgumentException {
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

        // Recompute authoritative total using BigDecimal and validate details
        BigDecimal computedTotal = BigDecimal.ZERO;
        for (OrderDetails detail : orderDetailsList) {
            if (detail == null)
                throw new IllegalArgumentException("Order detail cannot be null");

            if (detail.getFood() == null)
                throw new IllegalArgumentException("Food item cannot be null");

            if (detail.getQuantity() <= 0)
                throw new IllegalArgumentException("Quantity must be > 0");

            if (detail.getQuantity() > 100)
                throw new IllegalArgumentException(
                    "Quantity too large for item: " + detail.getFood().getFoodId()
                );

            // Validate available quantity
            Optional<Food> foodOpt = foodRepository.findById(detail.getFood().getFoodId());
            if (foodOpt.isEmpty()) {
                throw new IllegalArgumentException("Food item not found: " + detail.getFood().getFoodId());
            }
            
            Food food = foodOpt.get();
            if (food.getQuantity() < detail.getQuantity()) {
                throw new IllegalArgumentException(
                    "Insufficient quantity available for " + food.getFoodName() + 
                    ". Available: " + food.getQuantity() + ", Requested: " + detail.getQuantity()
                );
            }

            BigDecimal unit = detail.getUnitPriceDecimal();
            if (unit == null)
                throw new IllegalArgumentException("Unit price missing");

            BigDecimal expectedSubtotal = unit.multiply(BigDecimal.valueOf(detail.getQuantity()))
                                              .setScale(2, RoundingMode.HALF_UP);

            BigDecimal actualSubtotal = detail.getSubtotalDecimal() != null
                                        ? detail.getSubtotalDecimal().setScale(2, RoundingMode.HALF_UP)
                                        : BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

            if (expectedSubtotal.compareTo(actualSubtotal) != 0) {
                throw new IllegalArgumentException(
                    "Order detail subtotal mismatch for item: " +
                    (detail.getFood() != null ? detail.getFood().getFoodId() : "unknown")
                );
            }

            computedTotal = computedTotal.add(expectedSubtotal);
        }

        computedTotal = computedTotal.setScale(2, RoundingMode.HALF_UP);
        double totalPrice = computedTotal.doubleValue();

        // Process payment with authentication and get the authenticated payment method
        PaymentMethod paymentMethod;
        try {
            // Process payment first - this authenticates the user and deducts the amount
            paymentService.processPayment(paymentType, identifier, password, totalPrice);
            
            // Retrieve the payment method after successful authentication
            // Based on payment type, use appropriate method to get the payment method
            if ("Bank".equalsIgnoreCase(paymentType)) {
                paymentMethod = paymentMethodRepository
                    .findByCardNumber(identifier)
                    .orElseThrow(() -> new IllegalArgumentException("Payment method not found after authentication"));
            } else {
                paymentMethod = paymentMethodRepository
                    .findByWalletId(identifier)
                    .orElseThrow(() -> new IllegalArgumentException("Payment method not found after authentication"));
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Payment failed: " + e.getMessage());
        }

        // Decrement food quantities after successful payment
        for (OrderDetails detail : orderDetailsList) {
            boolean success = foodRepository.decrementQuantity(
                detail.getFood().getFoodId(), 
                detail.getQuantity()
            );
            if (!success) {
                throw new IllegalArgumentException(
                    "Failed to update quantity for food: " + detail.getFood().getFoodName() + 
                    ". It may have been sold out."
                );
            }
        }

        // Create order using Builder pattern (keeps construction logic centralized)
        Order order = new Order.Builder()
                        .orderDate(new Date())
                        .customer(customer)
                        .orderDetails(orderDetailsList)
                        .totalPrice(BigDecimal.valueOf(totalPrice))
                        .paymentMethod(paymentMethod)
                        .status("COMPLETED")
                        .build();

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

    // Builder moved to model.Order for reuse; no internal builder here.
}
