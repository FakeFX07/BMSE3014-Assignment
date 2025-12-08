package service.impl;

import java.util.Optional;

import model.*;
import repository.interfaces.IPaymentMethodRepository;
import service.interfaces.IPaymentService;

/**
 * Payment Service Implementation
 * Contains business logic for payment operations
 * Follows SOLID: Single Responsibility Principle, Dependency Inversion Principle, Open/Closed Principle
 */
public class PaymentService implements IPaymentService {
    
    private final IPaymentMethodRepository paymentMethodRepository;
    
    public PaymentService(IPaymentMethodRepository paymentMethodRepository) {
        this.paymentMethodRepository = paymentMethodRepository;
    }
    
    @Override
    public Payment processPayment(int customerId, String paymentType, double amount, 
                                  String cardNumber, String expiryDate) throws IllegalArgumentException {
        // Get payment method
        Optional<PaymentMethod> paymentMethodOpt = paymentMethodRepository.findByCustomerIdAndType(customerId, paymentType);
        
        if (paymentMethodOpt.isEmpty()) {
            throw new IllegalArgumentException("Payment method not found for customer");
        }
        
        PaymentMethod paymentMethod = paymentMethodOpt.get();
        
        // Validate card details for Bank payment
        if ("Bank".equalsIgnoreCase(paymentType)) {
            if (cardNumber == null || cardNumber.length() != 16) {
                throw new IllegalArgumentException("Invalid card number. Must be 16 digits");
            }
            if (expiryDate == null || expiryDate.length() != 4) {
                throw new IllegalArgumentException("Invalid expiry date. Must be 4 digits (MMYY)");
            }
        }
        
        // Create payment instance
        Payment payment = createPayment(paymentMethod);
        
        // Check if balance is sufficient
        if (!payment.checkAmount(amount)) {
            throw new IllegalArgumentException("Insufficient balance");
        }
        
        // Process payment
        double newBalance = payment.makePayment(amount);
        
        // Update balance in database
        paymentMethodRepository.updateBalance(paymentMethod.getPaymentMethodId(), newBalance);
        
        return payment;
    }
    
    @Override
    public Optional<PaymentMethod> getPaymentMethod(int customerId, String paymentType) {
        return paymentMethodRepository.findByCustomerIdAndType(customerId, paymentType);
    }
    
    @Override
    public Payment createPayment(PaymentMethod paymentMethod) {
        String paymentType = paymentMethod.getPaymentType();
        double balance = paymentMethod.getBalance();
        
        // Factory pattern - create appropriate payment instance
        // Follows Open/Closed Principle - easy to extend with new payment types
        switch (paymentType.toUpperCase()) {
            case "TNG":
                return new TNGPayment(balance);
            case "GRAB":
                return new GrabPayment(balance);
            case "BANK":
                return new BankPayment(balance);
            default:
                throw new IllegalArgumentException("Unknown payment type: " + paymentType);
        }
    }
}
