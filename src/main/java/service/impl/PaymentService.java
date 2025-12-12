package service.impl;

import model.*;
import repository.interfaces.IPaymentMethodRepository;
import service.interfaces.IPaymentService;
import util.PasswordUtil;

/**
 * Payment Service Implementation
 * Contains business logic for payment operations
 * Follows SOLID: Single Responsibility Principle, Dependency Inversion Principle, Open/Closed Principle
 * SRP：Handles only business logic (validation, processing。
 * DIP： Depends on IPaymentMethodRepository abstraction。
 * Meaningful Names： Methods describe exactly what they verify。
 */
public class PaymentService implements IPaymentService {
    
    private final IPaymentMethodRepository paymentMethodRepository;
    
    // Constants for validation
    private static final String PAYMENT_TYPE_BANK = "BANK";
    private static final String PAYMENT_TYPE_TNG = "TNG";
    private static final String PAYMENT_TYPE_GRAB = "GRAB";

    /**
     * Constructor for Dependency Injection.
     * * @param paymentMethodRepository The repository interface for data access
     */
    public PaymentService(IPaymentMethodRepository paymentMethodRepository) {
        this.paymentMethodRepository = paymentMethodRepository;
    }
    
    @Override
    public Payment processPayment(String paymentType, String identifier, String password, double amount) 
            throws IllegalArgumentException {
        
        // 1. Hash the password
        String hashedPassword = PasswordUtil.hashPassword(password);
        
        // 2. Authenticate and retrieve payment method based on type
        PaymentMethod paymentMethod;
        if (PAYMENT_TYPE_BANK.equalsIgnoreCase(paymentType)) {
            // For Bank, identifier is card number
            paymentMethod = paymentMethodRepository
                .authenticateByCardNumber(identifier, hashedPassword)
                .orElseThrow(() -> new IllegalArgumentException(
                    "Invalid card number or password"));
        } else {
            // For TNG/Grab, identifier is wallet ID
            paymentMethod = paymentMethodRepository
                .authenticateByWalletId(identifier, hashedPassword)
                .orElseThrow(() -> new IllegalArgumentException(
                    "Invalid wallet ID or password"));
        }
        
        // 3. Create Payment Strategy
        Payment payment = createPayment(paymentMethod);
        
        // 4. Check Balance
        validateBalance(payment, amount);
        
        // 5. Process Transaction
        double newBalance = payment.makePayment(amount);
        
        // 6. Update Persistence
        boolean updateSuccess = paymentMethodRepository.updateBalance(
            paymentMethod.getPaymentMethodId(), newBalance);
        if (!updateSuccess) {
            throw new RuntimeException("System Error: Failed to update balance in database.");
        }
        
        return payment;
    }
    
    @Override
    public Payment createPayment(PaymentMethod paymentMethod) {
        String type = paymentMethod.getPaymentType().toUpperCase();
        double balance = paymentMethod.getBalance();
        
        // Factory Logic - encapsulated creation
        switch (type) {
            case PAYMENT_TYPE_TNG:
                return new TNGPayment(balance);
            case PAYMENT_TYPE_GRAB:
                return new GrabPayment(balance);
            case PAYMENT_TYPE_BANK:
                return new BankPayment(balance);
            default:
                throw new IllegalArgumentException("Unsupported payment type: " + type);
        }
    }

    // ========================================================================
    // Private Helper Methods (DRY & Meaningful Names)
    // ========================================================================

    /**
     * Checks if the payment source has enough funds.
     */
    private void validateBalance(Payment payment, double amount) {
        if (!payment.checkAmount(amount)) {
            throw new IllegalArgumentException("Insufficient balance");
        }
    }
}