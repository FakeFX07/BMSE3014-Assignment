package controller; 

import model.Payment;
import model.PaymentMethod;
import service.interfaces.IPaymentService;

import java.util.Optional;

/**
 * Payment Controller
 * Handles payment-related user interactions
 * Follows SOLID: Single Responsibility Principle, Dependency Inversion Principle
 */
public class PaymentController {
    
    private final IPaymentService paymentService;
    
    public PaymentController(IPaymentService paymentService) {
        this.paymentService = paymentService;
    }
    
    /**
     * Process payment
     * 
     * @param customerId Customer ID
     * @param paymentType Payment type
     * @param amount Amount to pay
     * @param cardNumber Card number (for Bank, null for others)
     * @param expiryDate Expiry date (for Bank, null for others)
     * @return Payment object if successful, null if payment fails
     */
    public Payment processPayment(int customerId, String paymentType, double amount, 
                                 String cardNumber, String expiryDate) {
        try {
            return paymentService.processPayment(customerId, paymentType, amount, cardNumber, expiryDate);
        } catch (IllegalArgumentException e) {
            System.out.println("Payment failed: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Get payment method for customer
     * 
     * @param customerId Customer ID
     * @param paymentType Payment type
     * @return Payment method if found, null otherwise
     */
    public PaymentMethod getPaymentMethod(int customerId, String paymentType) {
        Optional<PaymentMethod> paymentMethodOpt = paymentService.getPaymentMethod(customerId, paymentType);
        return paymentMethodOpt.orElse(null);
    }
}
