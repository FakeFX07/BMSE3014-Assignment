package main.java.model;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * PaymentMethod Model Class
 * Represents a payment method entity in the system
 * Follows OOP principles: Encapsulation, Data Hiding
 */
public class PaymentMethod {
    
    private int paymentMethodId;
    private int customerId;
    private String paymentType; // TNG, Grab, Bank
    private BigDecimal balance;
    private String cardNumber;
    private String expiryDate;
    
    // Default constructor
    public PaymentMethod() {
        this.balance = BigDecimal.ZERO;
    }
    
    // Constructor for TNG/Grab (no card details)
    public PaymentMethod(int customerId, String paymentType, double balance) {
        this.customerId = customerId;
        this.paymentType = paymentType;
        this.balance = BigDecimal.valueOf(balance);
    }
    
    // Full constructor (for Bank with card details)
    public PaymentMethod(int paymentMethodId, int customerId, String paymentType, 
                        double balance, String cardNumber, String expiryDate) {
        this.paymentMethodId = paymentMethodId;
        this.customerId = customerId;
        this.paymentType = paymentType;
        this.balance = BigDecimal.valueOf(balance);
        this.cardNumber = cardNumber;
        this.expiryDate = expiryDate;
    }
    
    // Getters
    public int getPaymentMethodId() {
        return paymentMethodId;
    }
    
    public int getCustomerId() {
        return customerId;
    }
    
    public String getPaymentType() {
        return paymentType;
    }
    
    public double getBalance() {
        return balance.doubleValue();
    }
    
    public BigDecimal getBalanceDecimal() {
        return balance;
    }
    
    public String getCardNumber() {
        return cardNumber;
    }
    
    public String getExpiryDate() {
        return expiryDate;
    }
    
    // Setters
    public void setPaymentMethodId(int paymentMethodId) {
        this.paymentMethodId = paymentMethodId;
    }
    
    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }
    
    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }
    
    public void setBalance(double balance) {
        this.balance = BigDecimal.valueOf(balance);
    }
    
    public void setBalanceDecimal(BigDecimal balance) {
        this.balance = balance;
    }
    
    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }
    
    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaymentMethod that = (PaymentMethod) o;
        return paymentMethodId == that.paymentMethodId;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(paymentMethodId);
    }
    
    @Override
    public String toString() {
        return "PaymentMethod{" +
                "paymentMethodId=" + paymentMethodId +
                ", customerId=" + customerId +
                ", paymentType='" + paymentType + '\'' +
                ", balance=" + balance +
                '}';
    }
}
