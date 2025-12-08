package model;

/**
 * Payment Interface
 * Defines contract for payment processing
 * Follows SOLID: Interface Segregation Principle
 */
public interface Payment {
    
    /**
     * Check if balance is sufficient for payment
     * 
     * @param amount Amount to check
     * @return true if sufficient balance, false otherwise
     */
    boolean checkAmount(double amount);
    
    /**
     * Process payment and return new balance
     * 
     * @param amount Amount to pay
     * @return New balance after payment
     */
    double makePayment(double amount);
    
    /**
     * Get current balance
     * 
     * @return Current balance
     */
    double getBalance();
    
    /**
     * Get payment method name
     * 
     * @return Payment method name
     */
    String paymentName();
}
