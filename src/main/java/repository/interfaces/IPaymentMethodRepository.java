package repository.interfaces;

import java.util.Optional;

import model.PaymentMethod;

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
     * Find payment method by wallet ID (for TNG/Grab)
     * 
     * @param walletId Wallet ID
     * @return Optional containing payment method if found
     */
    Optional<PaymentMethod> findByWalletId(String walletId);
    
    /**
     * Find payment method by card number (for Bank)
     * 
     * @param cardNumber Card number
     * @return Optional containing payment method if found
     */
    Optional<PaymentMethod> findByCardNumber(String cardNumber);
    
    /**
     * Authenticate payment method by wallet ID and password
     * 
     * @param walletId Wallet ID
     * @param hashedPassword SHA256 hashed password
     * @return Optional containing payment method if authenticated
     */
    Optional<PaymentMethod> authenticateByWalletId(String walletId, String hashedPassword);
    
    /**
     * Authenticate payment method by card number and password
     * 
     * @param cardNumber Card number
     * @param hashedPassword SHA256 hashed password
     * @return Optional containing payment method if authenticated
     */
    Optional<PaymentMethod> authenticateByCardNumber(String cardNumber, String hashedPassword);
    
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
