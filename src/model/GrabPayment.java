package model;

/**
 * Grab Payment Implementation
 * Follows SOLID: Open/Closed Principle, Liskov Substitution Principle
 */
public class GrabPayment implements Payment {
    
    private double balance;
    
    public GrabPayment(double balance) {
        this.balance = balance;
    }
    
    @Override
    public boolean checkAmount(double amount) {
        return balance >= amount;
    }
    
    @Override
    public double makePayment(double amount) {
        if (checkAmount(amount)) {
            balance -= amount;
        }
        return balance;
    }
    
    @Override
    public double getBalance() {
        return balance;
    }
    
    @Override
    public String paymentName() {
        return "Grab";
    }
}
