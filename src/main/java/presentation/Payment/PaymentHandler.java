package presentation.Payment;

import controller.PaymentController;
import model.Payment;
import presentation.General.UserInputHandler;

public class PaymentHandler {
    
    private final PaymentController paymentController;
    private final UserInputHandler inputHandler;
    
    public PaymentHandler(PaymentController paymentController, UserInputHandler inputHandler) {
        this.paymentController = paymentController;
        this.inputHandler = inputHandler;
    }
    
    /**
     * Handles the payment flow with wallet ID/card number and password authentication.
     * Displays menu using Enum and processes selection.
     */
    public void handlePayment(double totalAmount) {
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
        
        // 3. Get identifier and password based on payment type
        String paymentType = selectedOption.getDisplayText();
        String identifier;
        String password;
        
        if (selectedOption == PaymentOption.BANK) {
            identifier = inputHandler.readString("Enter Card Number (16 digits): ");
            password = inputHandler.readPassword("Enter Card Password: ");
        } else {
            // TNG or Grab
            identifier = inputHandler.readString("Enter " + paymentType + " Wallet ID: ");
            password = inputHandler.readPassword("Enter " + paymentType + " Password: ");
        }
        
        // 4. Confirm and Process
        if (inputHandler.readYesNo("Confirm payment of RM " + totalAmount + "? (Y/N): ")) {
            Payment result = paymentController.processPayment(paymentType, identifier, password, totalAmount);
            
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