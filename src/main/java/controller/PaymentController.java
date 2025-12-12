package controller;

import model.Payment;
import model.PaymentMethod;
import repository.impl.PaymentMethodRepository;
import service.impl.PaymentService;
import service.interfaces.IPaymentService;

/**
 * Payment Controller
 * Handles payment-related user interactions
 * Follows SOLID: Single Responsibility Principle
 * Single Responsibility： Orchestrates flow, handles exceptions, returns results.
 * KISS: Keep It Simple - no complex logic here。
 */
public class PaymentController {
    
    private final IPaymentService paymentService;
    
    /**
     * Constructor for Dependency Injection.
     * @param paymentService The service to handle business logic
     */
    public PaymentController(IPaymentService paymentService) {
        this.paymentService = paymentService;
    }
    
    /**
     * Default constructor (Wiring).
     * Maintains backward compatibility while allowing DI via the other constructor.
     */
    public PaymentController() {
        this(new PaymentService(new PaymentMethodRepository()));
    }
    
    /**
     * Process a user payment.
     * @param customerId The customer making the payment
     * @param paymentType The type of payment (TNG, Grab, Bank)
     * @param amount The transaction amount
     * @param cardNumber Optional card number (for Bank)
     * @param expiryDate Optional expiry date (for Bank)
     * @return The Payment object if successful, null otherwise
     */
    public Payment processPayment(int customerId, String paymentType, double amount, 
                                 String cardNumber, String expiryDate) {
        try {
            return paymentService.processPayment(customerId, paymentType, amount, cardNumber, expiryDate);
        } catch (RuntimeException e) { // FIXED: Caught RuntimeException only (covers IllegalArgumentException)
            System.out.println("Payment Processing Error: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Retrieve a specific payment method.
     * @param customerId Customer ID
     * @param paymentType Payment Type Name
     * @return PaymentMethod object or null if not found
     */
    public PaymentMethod getPaymentMethod(int customerId, String paymentType) {
        return paymentService.getPaymentMethod(customerId, paymentType).orElse(null);
    }
}