package repository.interfaces;

import model.PaymentMethod;
import java.util.List;
import java.util.Optional;

/**
 * Payment Method Repository Interface
 * Defines contract for payment method data access operations
 * Follows SOLID: Interface Segregation Principle, Dependency Inversion Principle
 */
public interface IPaymentMethodRepository {
    
    /**
     * Find payment method by ID
     * 
     * @param paymentMethodId Payment method ID
     * @return Optional containing payment method if found
     */
    Optional<PaymentMethod> findById(int paymentMethodId);
    
    /**
     * Find payment methods by customer ID
     * 
     * @param customerId Customer ID
     * @return List of payment methods
     */
    List<PaymentMethod> findByCustomerId(int customerId);
    
    /**
     * Find payment method by customer ID and payment type
     * 
     * @param customerId Customer ID
     * @param paymentType Payment type (TNG, Grab, Bank)
     * @return Optional containing payment method if found
     */
    Optional<PaymentMethod> findByCustomerIdAndType(int customerId, String paymentType);
    
    /**
     * Save payment method (create or update)
     * 
     * @param paymentMethod Payment method to save
     * @return Saved payment method with generated ID
     */
    PaymentMethod save(PaymentMethod paymentMethod);
    
    /**
     * Update payment method balance
     * 
     * @param paymentMethodId Payment method ID
     * @param newBalance New balance
     * @return true if updated, false otherwise
     */
    boolean updateBalance(int paymentMethodId, double newBalance);
}
