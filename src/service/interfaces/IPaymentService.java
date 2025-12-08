package service.interfaces;

import model.Payment;
import model.PaymentMethod;
import java.util.Optional;

/**
 * Payment Service Interface
 * Defines contract for payment operations
 * Follows SOLID: Interface Segregation Principle, Dependency Inversion Principle
 */
public interface IPaymentService {
    
    /**
     * Process payment
     * 
     * @param customerId Customer ID
     * @param paymentType Payment type (TNG, Grab, Bank)
     * @param amount Amount to pay
     * @param cardNumber Card number (for Bank, null for others)
     * @param expiryDate Expiry date (for Bank, null for others)
     * @return Payment object if successful
     * @throws IllegalArgumentException if payment fails
     */
    Payment processPayment(int customerId, String paymentType, double amount, 
                          String cardNumber, String expiryDate) throws IllegalArgumentException;
    
    /**
     * Get payment method for customer
     * 
     * @param customerId Customer ID
     * @param paymentType Payment type
     * @return Optional containing payment method if found
     */
    Optional<PaymentMethod> getPaymentMethod(int customerId, String paymentType);
    
    /**
     * Create payment instance from payment method
     * 
     * @param paymentMethod Payment method
     * @return Payment instance
     */
    Payment createPayment(PaymentMethod paymentMethod);
}
