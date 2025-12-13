package controller;

import model.Payment;
import repository.impl.PaymentMethodRepository;
import service.impl.PaymentService;
import service.interfaces.IPaymentService;

public class PaymentController {
    
    private final IPaymentService paymentService;
    
    //Constructor for Dependency Injection.
    public PaymentController(IPaymentService paymentService) {
        this.paymentService = paymentService;
    }
    
    public PaymentController() {
        this(new PaymentService(new PaymentMethodRepository()));
    }
    
    //Process a user payment with authentication.
    public Payment processPayment(String paymentType, String identifier, 
                                 String password, double amount) {
        try {
            return paymentService.processPayment(paymentType, identifier, password, amount);
        } catch (RuntimeException e) {
            System.out.println("Payment Processing Error: " + e.getMessage());
            return null;
        }
    }
}