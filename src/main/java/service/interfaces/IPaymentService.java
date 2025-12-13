package service.interfaces;

import model.Payment;
import model.PaymentMethod;

public interface IPaymentService {
    
    Payment processPayment(String paymentType, String identifier, String password, double amount) 
            throws IllegalArgumentException;
    
    /**
     * Create payment instance from payment method
     */
    Payment createPayment(PaymentMethod paymentMethod);
}
