package model;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * PaymentMethod Model Class
 * Represents a payment method entity in the system
 * Follows OOP principles: Encapsulation, Data Hiding
 */
public class PaymentMethod {
    
    private int paymentMethodId;
    private String password; // SHA256 hashed password
    private String paymentType; // TNG, Grab, Bank
    private String walletId; // For TNG/Grab e-wallets
    private BigDecimal balance;
    private String cardNumber; // For Bank payments
    private String expiryDate; // For Bank payments
    
    // Default constructor
    public PaymentMethod() {
        this.balance = BigDecimal.ZERO;
    }
    
    // Constructor for TNG/Grab (wallet-based)
    public PaymentMethod(String walletId, String paymentType, String password, double balance) {
        this.walletId = walletId;
        this.paymentType = paymentType;
        this.password = password;
        this.balance = BigDecimal.valueOf(balance);
    }
    
    // Full constructor (for Bank with card details)
    public PaymentMethod(int paymentMethodId, String paymentType, String walletId,
                        double balance, String cardNumber, String expiryDate, String password) {
        this.paymentMethodId = paymentMethodId;
        this.paymentType = paymentType;
        this.walletId = walletId;
        this.balance = BigDecimal.valueOf(balance);
        this.cardNumber = cardNumber;
        this.expiryDate = expiryDate;
        this.password = password;
    }
    
    // Getters
    public int getPaymentMethodId() {
        return paymentMethodId;
    }
    
    public String getPassword() {
        return password;
    }
    
    public String getPaymentType() {
        return paymentType;
    }
    
    public String getWalletId() {
        return walletId;
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
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }
    
    public void setWalletId(String walletId) {
        this.walletId = walletId;
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
                ", paymentType='" + paymentType + '\'' +
                ", walletId='" + walletId + '\'' +
                ", balance=" + balance +
                '}';
    }
}
