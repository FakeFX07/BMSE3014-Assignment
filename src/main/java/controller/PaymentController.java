package controller;

import model.Payment;
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
     * Process a user payment with authentication.
     * @param paymentType The type of payment (TNG, Grab, Bank)
     * @param identifier Wallet ID (for TNG/Grab) or Card Number (for Bank)
     * @param password Password for authentication
     * @param amount The transaction amount
     * @return The Payment object if successful, null otherwise
     */
    public Payment processPayment(String paymentType, String identifier, 
                                 String password, double amount) {
        try {
            return paymentService.processPayment(paymentType, identifier, password, amount);
        } catch (RuntimeException e) {
            System.out.println("Payment Processing Error: " + e.getMessage());
            return null;
        }
    }
}