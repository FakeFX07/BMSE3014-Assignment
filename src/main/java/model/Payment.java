package model;

public interface Payment {
    
    //Check if balance is sufficient for payment
    boolean checkAmount(double amount);
    
    //Process payment and return new balance
    double makePayment(double amount);
    
    //Get current balance
    double getBalance();
    
    //Get payment method name
    String paymentName();
}
