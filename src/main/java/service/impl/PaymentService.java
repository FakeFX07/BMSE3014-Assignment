package service.impl;

import java.util.Optional;

import model.*;
import repository.interfaces.IPaymentMethodRepository;
import service.interfaces.IPaymentService;

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
    private static final int CARD_NUMBER_LENGTH = 16;
    private static final int EXPIRY_DATE_LENGTH = 4;
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
    public Payment processPayment(int customerId, String paymentType, double amount, 
                                  String cardNumber, String expiryDate) throws IllegalArgumentException {
        
        // 1. Retrieve Payment Method
        PaymentMethod paymentMethod = getPaymentMethodOrThrow(customerId, paymentType);
        
        // 2. Validate Bank Details if applicable
        if (PAYMENT_TYPE_BANK.equalsIgnoreCase(paymentType)) {
            validateBankDetails(cardNumber, expiryDate);
        }
        
        // 3. Create Payment Strategy
        Payment payment = createPayment(paymentMethod);
        
        // 4. Check Balance
        validateBalance(payment, amount);
        
        // 5. Process Transaction
        double newBalance = payment.makePayment(amount);
        
        // 6. Update Persistence
        boolean updateSuccess = paymentMethodRepository.updateBalance(paymentMethod.getPaymentMethodId(), newBalance);
        if (!updateSuccess) {
            throw new RuntimeException("System Error: Failed to update balance in database.");
        }
        
        return payment;
    }
    
    @Override
    public Optional<PaymentMethod> getPaymentMethod(int customerId, String paymentType) {
        return paymentMethodRepository.findByCustomerIdAndType(customerId, paymentType);
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
     * Retrieves payment method or throws exception if not found.
     */
    private PaymentMethod getPaymentMethodOrThrow(int customerId, String paymentType) {
        return paymentMethodRepository.findByCustomerIdAndType(customerId, paymentType)
                .orElseThrow(() -> new IllegalArgumentException("Payment method not found for customer"));
    }

    /**
     * Validates card details specifically for Bank payments.
     */
    private void validateBankDetails(String cardNumber, String expiryDate) {
        if (cardNumber == null || cardNumber.length() != CARD_NUMBER_LENGTH) {
            throw new IllegalArgumentException("Invalid card number. Must be " + CARD_NUMBER_LENGTH + " digits");
        }
        if (expiryDate == null || expiryDate.length() != EXPIRY_DATE_LENGTH) {
            throw new IllegalArgumentException("Invalid expiry date. Must be " + EXPIRY_DATE_LENGTH + " digits (MMYY)");
        }
    }

    /**
     * Checks if the payment source has enough funds.
     */
    private void validateBalance(Payment payment, double amount) {
        if (!payment.checkAmount(amount)) {
            throw new IllegalArgumentException("Insufficient balance");
        }
    }
}