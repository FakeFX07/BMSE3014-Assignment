package service.interfaces;

import model.Payment;
import model.PaymentMethod;

/**
 * Payment Service Interface
 * Defines contract for payment operations
 * Follows SOLID: Interface Segregation Principle, Dependency Inversion Principle
 */
public interface IPaymentService {
    
    /**
     * Process payment with authentication
     * 
     * @param paymentType Payment type (TNG, Grab, Bank)
     * @param identifier Wallet ID (for TNG/Grab) or Card Number (for Bank)
     * @param password Password for authentication (will be hashed with SHA256)
     * @param amount Amount to pay
     * @return Payment object if successful
     * @throws IllegalArgumentException if payment fails or authentication fails
     */
    Payment processPayment(String paymentType, String identifier, String password, double amount) 
            throws IllegalArgumentException;
    
    /**
     * Create payment instance from payment method
     * 
     * @param paymentMethod Payment method
     * @return Payment instance
     */
    Payment createPayment(PaymentMethod paymentMethod);
}
