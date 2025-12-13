package repository.interfaces;

import java.util.Optional;

import model.PaymentMethod;

public interface IPaymentMethodRepository {
    
    /**
     * Find payment method by ID
     */
    Optional<PaymentMethod> findById(int paymentMethodId);
    
    /**
     * Find payment method by wallet ID (for TNG/Grab)
     */
    Optional<PaymentMethod> findByWalletId(String walletId);
    
    /**
     * Find payment method by card number (for Bank)
     */
    Optional<PaymentMethod> findByCardNumber(String cardNumber);
    
    /**
     * Authenticate payment method by wallet ID and password
     */
    Optional<PaymentMethod> authenticateByWalletId(String walletId, String hashedPassword);
    
    /**
     * Authenticate payment method by card number and password
     */
    Optional<PaymentMethod> authenticateByCardNumber(String cardNumber, String hashedPassword);
    
    /**
     * Save payment method (create or update)
     */
    PaymentMethod save(PaymentMethod paymentMethod);
    
    /**
     * Update payment method balance
     */
    boolean updateBalance(int paymentMethodId, double newBalance);
}
