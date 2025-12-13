package model;

public class BankPayment implements Payment {
    
    private static final double TRANSACTION_FEE = 1.00;
    private double balance;
    
    public BankPayment(double balance) {
        this.balance = balance;
    }
    
    @Override
    public boolean checkAmount(double amount) {
        return balance >= (amount + TRANSACTION_FEE);
    }
    
    @Override
    public double makePayment(double amount) {
        if (checkAmount(amount)) {
            balance -= (amount + TRANSACTION_FEE);
        }
        return balance;
    }
    
    @Override
    public double getBalance() {
        return balance;
    }
    
    @Override
    public String paymentName() {
        return "Bank";
    }
    
    //Get transaction fee
    public double getTransactionFee() {
        return TRANSACTION_FEE;
    }
}
