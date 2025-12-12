package presentation.Payment;

import controller.PaymentController;
import model.Payment;
import model.PaymentMethod;
import presentation.General.UserInputHandler;

/**
 * Payment Handler
 * Manages UI interactions for the Payment Module.
 * Matches the pattern of FoodHandler.
 */
public class PaymentHandler {
    
    private final PaymentController paymentController;
    private final UserInputHandler inputHandler;
    
    public PaymentHandler(PaymentController paymentController, UserInputHandler inputHandler) {
        this.paymentController = paymentController;
        this.inputHandler = inputHandler;
    }
    
    /**
     * Handles the payment flow.
     * Displays menu using Enum and processes selection.
     */
    public void handlePayment(int customerId, double totalAmount) {
        System.out.println("\n--- Proceeding to Payment ---");
        System.out.printf("Total Amount to Pay: RM %.2f%n", totalAmount);
        
        // 1. Display Menu using Enum
        PaymentOption.displayMenu();
        
        // 2. Get Selection
        int choice = inputHandler.readInt("Select Payment Method: ");
        PaymentOption selectedOption = PaymentOption.getByOptionNumber(choice);
        
        if (selectedOption == null) {
            System.out.println("Invalid payment selection!");
            return;
        }
        
        // 3. Gather Details
        String paymentType = selectedOption.getDisplayText();
        String cardNumber = null;
        String expiryDate = null;
        
        if (selectedOption == PaymentOption.BANK) {
            cardNumber = inputHandler.readString("Enter Card Number (16 digits): ");
            expiryDate = inputHandler.readString("Enter Expiry Date (MMYY): ");
        }
        
        // 4. Confirm and Process
        if (inputHandler.readYesNo("Confirm payment of RM " + totalAmount + "? (Y/N): ")) {
            Payment result = paymentController.processPayment(customerId, paymentType, totalAmount, cardNumber, expiryDate);
            
            if (result != null) {
                printReceipt(result, totalAmount);
            } else {
                System.out.println("Payment failed. Please try again.");
            }
        } else {
            System.out.println("Payment cancelled by user.");
        }
    }
    
    private void printReceipt(Payment payment, double amountPaid) {
        System.out.println("\n==============================");
        System.out.println("       PAYMENT RECEIPT        ");
        System.out.println("==============================");
        System.out.println("Method:      " + payment.paymentName());
        System.out.printf("Amount Paid: RM %.2f%n", amountPaid);
        System.out.printf("New Balance: RM %.2f%n", payment.getBalance());
        System.out.println("==============================\n");
    }
}